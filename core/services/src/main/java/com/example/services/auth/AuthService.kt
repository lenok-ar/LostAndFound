package com.example.services.auth

import android.app.Activity
import android.content.Intent

interface AuthService {
    fun createYandexLoginIntent(): Intent?
    fun handleYandexResult(resultCode: Int, data: Intent?): AuthResult
    fun loginWithVk(activity: Activity, onResult: (AuthResult) -> Unit)
}
