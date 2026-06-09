package com.example.services.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.yandex.authsdk.YandexAuthException
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.VKIDAuthFail
import com.vk.id.auth.AuthCodeData
import com.vk.id.auth.VKIDAuthCallback
import com.vk.id.auth.VKIDAuthParams
import androidx.lifecycle.LifecycleOwner
import com.example.services.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

@Singleton
class SdkAuthService @Inject constructor(
    @ApplicationContext context: Context,
    private val tokenStorage: TokenStorage
) : AuthService {
    private val appContext = context
    private val httpClient = OkHttpClient()
    private val yandexSdk by lazy {
        YandexAuthSdk(YandexAuthOptions(appContext))
    }

    override fun createYandexLoginIntent(): Intent? =
        if (BuildConfig.YANDEX_CLIENT_ID.isBlank()) null else
        runCatching {
            yandexSdk.createLoginIntent(YandexAuthLoginOptions.Builder().build())
        }.getOrNull()

    override fun handleYandexResult(resultCode: Int, data: Intent?): AuthResult =
        try {
            val token = yandexSdk.extractToken(resultCode, data)
                ?: return AuthResult.Cancelled
            val profile = loadYandexProfile(token.value)
            val user = AuthUser(
                name = profile?.name ?: "Пользователь Яндекс",
                provider = "yandex",
                userId = profile?.userId ?: token.value.take(32)
            )
            tokenStorage.save(
                token = token.value,
                user = user,
                expiresAtMillis = System.currentTimeMillis() + token.expiresIn() * 1_000L
            )
            AuthResult.Success(user)
        } catch (exception: YandexAuthException) {
            AuthResult.Error(exception.message ?: "Ошибка входа через Яндекс")
        }

    private fun loadYandexProfile(token: String): YandexProfile? =
        runCatching {
            runBlocking(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("https://login.yandex.ru/info?format=json")
                    .header("Authorization", "OAuth $token")
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use null
                    val json = JSONObject(response.body?.string().orEmpty())
                    val fullName = listOf(
                        json.optString("first_name"),
                        json.optString("last_name")
                    ).filter(String::isNotBlank).joinToString(" ")
                    val name = sequenceOf(
                        json.optString("real_name"),
                        fullName,
                        json.optString("display_name"),
                        json.optString("login")
                    ).firstOrNull(String::isNotBlank) ?: return@use null

                    YandexProfile(
                        name = name,
                        userId = json.optString("id").ifBlank { token.take(32) }
                    )
                }
            }
        }.getOrNull()

    override fun loginWithVk(activity: Activity, onResult: (AuthResult) -> Unit) {
        if (BuildConfig.VK_CLIENT_ID.isBlank() || BuildConfig.VK_CLIENT_SECRET.isBlank()) {
            onResult(AuthResult.Error("Добавьте VK_CLIENT_ID и VK_CLIENT_SECRET в local.properties"))
            return
        }
        val lifecycleOwner = activity as? LifecycleOwner
        if (lifecycleOwner == null) {
            onResult(AuthResult.Error("Экран входа не поддерживает VK ID"))
            return
        }

        VKID.init(appContext)
        VKID.instance.authorize(
            lifecycleOwner,
            object : VKIDAuthCallback {
                override fun onAuth(accessToken: AccessToken) {
                    val data = accessToken.userData
                    val user = AuthUser(
                        name = "${data.firstName} ${data.lastName}".trim(),
                        provider = "vk",
                        userId = accessToken.token.take(32)
                    )
                    val expiresAtMillis = if (accessToken.expireTime < 1_000_000_000_000L) {
                        accessToken.expireTime * 1_000L
                    } else {
                        accessToken.expireTime
                    }
                    tokenStorage.save(accessToken.token, user, expiresAtMillis)
                    onResult(AuthResult.Success(user))
                }

                override fun onAuthCode(data: AuthCodeData, isCompletion: Boolean) {
                    onResult(AuthResult.Error("VK ID вернул код без токена"))
                }

                override fun onFail(fail: VKIDAuthFail) {
                    onResult(AuthResult.Error(fail.description))
                }
            },
            VKIDAuthParams.Builder().build()
        )
    }

    private data class YandexProfile(val name: String, val userId: String)
}
