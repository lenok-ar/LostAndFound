package com.example.services.profile

interface UserProfileService {
    fun saveCurrentProfile(name: String, email: String)
    fun updateFcmToken(token: String)
    fun observeCurrentProfile(
        onChanged: (CloudUserProfile?) -> Unit,
        onError: (Throwable) -> Unit
    ): () -> Unit
}
