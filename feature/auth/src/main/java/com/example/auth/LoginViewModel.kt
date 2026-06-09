package com.example.auth

import android.app.Activity
import android.content.Intent
import com.example.services.analytics.AnalyticsService
import com.example.services.auth.AuthResult
import com.example.services.auth.AuthService

class LoginViewModel(
    private val authService: AuthService,
    private val analyticsService: AnalyticsService
) {
    fun createYandexLoginIntent(): Intent? =
        try {
            authService.createYandexLoginIntent()
        } catch (exception: Exception) {
            onLoginError(exception)
            null
        }

    fun handleYandexResult(resultCode: Int, data: Intent?): AuthResult =
        try {
            authService.handleYandexResult(resultCode, data)
        } catch (exception: Exception) {
            onLoginError(exception)
            AuthResult.Error("Не удалось выполнить вход через Яндекс")
        }.also(::onLoginResult)

    fun loginWithVk(activity: Activity, onResult: (AuthResult) -> Unit) {
        try {
            authService.loginWithVk(activity) { result ->
                onLoginResult(result)
                onResult(result)
            }
        } catch (exception: Exception) {
            onLoginError(exception)
            onResult(AuthResult.Error("Не удалось выполнить вход через VK"))
        }
    }

    fun onLoginResult(result: AuthResult) {
        if (result is AuthResult.Success) {
            analyticsService.trackEvent(
                "user_logged_in",
                mapOf("provider" to result.user.provider)
            )
        } else if (result is AuthResult.Error) {
            analyticsService.trackError("login_failed", IllegalStateException(result.message))
        }
    }

    fun onScreenOpened() {
        analyticsService.trackEvent("screen_viewed", mapOf("screen_name" to "login"))
    }

    fun onLoginError(error: Throwable) {
        analyticsService.trackError("login_failed", error)
    }
}
