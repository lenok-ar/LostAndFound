package com.example.services.auth

interface TokenStorage {
    fun save(token: String, user: AuthUser, expiresAtMillis: Long)
    fun token(): String?
    fun user(): AuthUser?
    fun isTokenValid(): Boolean
    fun clear()
}
