package com.example.services.auth

data class AuthUser(
    val name: String,
    val provider: String,
    val userId: String = ""
)

sealed interface AuthResult {
    data class Success(val user: AuthUser) : AuthResult
    data class Error(val message: String) : AuthResult
    data object Cancelled : AuthResult
}
