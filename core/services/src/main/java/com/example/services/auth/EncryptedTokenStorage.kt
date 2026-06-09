package com.example.services.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedTokenStorage @Inject constructor(
    @ApplicationContext context: Context
) : TokenStorage {
    private val preferences = EncryptedSharedPreferences.create(
        context,
        "secure_auth",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun save(token: String, user: AuthUser, expiresAtMillis: Long) {
        preferences.edit()
            .putString(TOKEN, token)
            .putString(NAME, user.name)
            .putString(PROVIDER, user.provider)
            .putString(USER_ID, user.userId)  // добавить
            .putLong(EXPIRES_AT, expiresAtMillis)
            .apply()
    }

    override fun token(): String? = preferences.getString(TOKEN, null)

    override fun user(): AuthUser? {
        val name = preferences.getString(NAME, null) ?: return null
        val provider = preferences.getString(PROVIDER, null) ?: return null
        val userId = preferences.getString(USER_ID, "") ?: ""  // добавить
        return AuthUser(name, provider, userId)
    }

    override fun isTokenValid(): Boolean {
        val token = token() ?: return false
        val expiresAt = preferences.getLong(EXPIRES_AT, 0L)
        val valid = token.isNotBlank() && expiresAt > System.currentTimeMillis()
        if (!valid) clear()
        return valid
    }

    override fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val TOKEN = "access_token"
        const val NAME = "user_name"
        const val PROVIDER = "provider"
        const val USER_ID = "user_id"  // добавить
        const val EXPIRES_AT = "expires_at"
    }
}
