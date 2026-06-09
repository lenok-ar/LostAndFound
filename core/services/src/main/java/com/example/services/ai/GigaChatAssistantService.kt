package com.example.services.ai

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.services.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

@Singleton
class GigaChatAssistantService @Inject constructor(
    @ApplicationContext context: Context
) : AiAssistantService {
    private val client = OkHttpClient()
    private val preferences = EncryptedSharedPreferences.create(
        context,
        PREFERENCES_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override val isConfigured: Boolean
        get() = BuildConfig.GIGACHAT_AUTH_KEY.isNotBlank()

    override suspend fun createSearchAdvice(
        description: String,
        circumstances: String
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            check(isConfigured) { "Добавьте GIGACHAT_AUTH_KEY в local.properties" }
            val token = getValidToken()
            val requestJson = JSONObject()
                .put("model", "GigaChat")
                .put("temperature", 0.5)
                .put("max_tokens", 500)
                .put(
                    "messages",
                    JSONArray()
                        .put(
                            JSONObject()
                                .put("role", "system")
                                .put("content", SYSTEM_PROMPT)
                        )
                        .put(
                            JSONObject()
                                .put("role", "user")
                                .put(
                                    "content",
                                    "Описание вещи: $description\nГде и когда потеряна: $circumstances"
                                )
                        )
                )

            val request = Request.Builder()
                .url(CHAT_URL)
                .header("Authorization", "Bearer $token")
                .post(requestJson.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    throw IOException(errorMessage(response.code))
                }
                JSONObject(body)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            }
        }
    }

    private fun getValidToken(): String {
        val savedToken = preferences.getString(TOKEN_KEY, null)
        val expiresAt = preferences.getLong(EXPIRES_AT_KEY, 0L)
        if (savedToken != null && expiresAt > System.currentTimeMillis() + TOKEN_RESERVE_MS) {
            return savedToken
        }

        val request = Request.Builder()
            .url(OAUTH_URL)
            .header("Authorization", "Basic ${BuildConfig.GIGACHAT_AUTH_KEY}")
            .header("RqUID", UUID.randomUUID().toString())
            .header("Accept", "application/json")
            .post(FormBody.Builder().add("scope", "GIGACHAT_API_PERS").build())
            .build()

        return client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException(errorMessage(response.code))
            }
            val json = JSONObject(body)
            val token = json.getString("access_token")
            val expiration = json.optLong("expires_at", System.currentTimeMillis() + DEFAULT_TOKEN_LIFETIME_MS)
            preferences.edit()
                .putString(TOKEN_KEY, token)
                .putLong(EXPIRES_AT_KEY, expiration)
                .apply()
            token
        }
    }

    private fun errorMessage(code: Int): String = when (code) {
        401 -> "Ключ GigaChat недействителен"
        429 -> "Превышен лимит запросов. Попробуйте позже"
        in 500..599 -> "Сервис GigaChat временно недоступен"
        else -> "Ошибка GigaChat: $code"
    }

    private companion object {
        const val OAUTH_URL = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth"
        const val CHAT_URL = "https://gigachat.devices.sberbank.ru/api/v1/chat/completions"
        const val PREFERENCES_NAME = "giga_chat_preferences"
        const val TOKEN_KEY = "access_token"
        const val EXPIRES_AT_KEY = "expires_at"
        const val TOKEN_RESERVE_MS = 60_000L
        const val DEFAULT_TOKEN_LIFETIME_MS = 30 * 60 * 1000L
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        const val SYSTEM_PROMPT = """
Ты помощник сервиса поиска потерянных вещей в КемГУ.
Ответь на русском языке и создай два коротких блока:
1. Улучшенный текст объявления.
2. Советы, где и как искать вещь.
Не придумывай факты, которых нет в описании.
"""
    }
}
