package com.example.auth

import android.app.Activity
import android.content.Intent
import com.example.services.analytics.FakeAnalyticsService
import com.example.services.auth.AuthResult
import com.example.services.auth.AuthService
import com.example.services.auth.AuthUser
import org.junit.Assert.assertEquals
import org.junit.Test

class LoginViewModelTest {
    private val analytics = FakeAnalyticsService()
    private val authService = object : AuthService {
        override fun createYandexLoginIntent(): Intent? = null

        override fun handleYandexResult(resultCode: Int, data: Intent?): AuthResult =
            AuthResult.Success(AuthUser("User", "yandex"))

        override fun loginWithVk(activity: Activity, onResult: (AuthResult) -> Unit) {
            onResult(AuthResult.Success(AuthUser("User", "vk")))
        }
    }
    private val viewModel = LoginViewModel(authService, analytics)

    @Test
    fun `opening login logs screen viewed`() {
        viewModel.onScreenOpened()

        assertEquals("screen_viewed", analytics.events.single().first)
        assertEquals("login", analytics.events.single().second["screen_name"])
    }

    @Test
    fun `successful login logs provider`() {
        viewModel.onLoginResult(AuthResult.Success(AuthUser("User",
            "yandex")))

        assertEquals("user_logged_in", analytics.events.single().first)
        assertEquals("yandex", analytics.events.single().second["provider"])
    }

    @Test
    fun `yandex result is handled through auth service`() {
        viewModel.handleYandexResult(0, null)

        assertEquals("yandex", analytics.events.single().second["provider"])
    }

    @Test
    fun `login error is sent to analytics`() {
        val error = IllegalStateException("Test error")

        viewModel.onLoginError(error)

        assertEquals("login_failed", analytics.errors.single().first)
        assertEquals(error, analytics.errors.single().second)
    }
}
