package com.example.services.profile

data class CloudUserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val fcmToken: String = "",
    val updatedAt: Long = 0L
)
