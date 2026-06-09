package com.example.profile

import androidx.lifecycle.ViewModel
import com.example.services.auth.TokenStorage
import com.example.services.profile.CloudUserProfile
import com.example.services.profile.UserProfileService
import com.example.services.crash.CrashReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel(
    private val profileService: UserProfileService,
    private val tokenStorage: TokenStorage,
    private val crashReporter: CrashReporter
) : ViewModel() {
    private val _user = MutableStateFlow<CloudUserProfile?>(null)
    val user = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val stopObserving: () -> Unit

    init {
        val storedUser = tokenStorage.user()
        storedUser?.let { user ->
            _user.value = CloudUserProfile(
                userId = user.userId,
                name = user.name,
                email = "${user.provider}@lostandfound.local"
            )
            profileService.saveCurrentProfile(user.name, "${user.provider}@lostandfound.local")
        }
        _isLoading.value = false

        stopObserving = profileService.observeCurrentProfile(
            onChanged = {
                if (it != null) _user.value = it
            },
            onError = {
                crashReporter.setKey("screen", "profile")
                crashReporter.recordNonFatal(it)
                _error.value = "Облачный профиль временно недоступен"
            }
        )
    }

    override fun onCleared() {
        stopObserving()
        super.onCleared()
    }

    fun generateTestCrash(): Nothing {
        crashReporter.log("Generate crash button clicked")
        crashReporter.setKey("screen", "profile")
        throw RuntimeException("Manual crash from control task")
    }

    fun refreshAfterLogin() {
        tokenStorage.user()?.let { user ->
            val email = "${user.provider}@lostandfound.local"
            _user.value = CloudUserProfile(
                userId = user.userId,
                name = user.name,
                email = email
            )
            _error.value = null
            profileService.saveCurrentProfile(user.name, email)
        }
    }
}
