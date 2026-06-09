package com.example.services.remoteconfig

import android.util.Log
import com.example.services.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRemoteConfigService @Inject constructor() : RemoteConfigService {
    override fun initialize() {
        val config = remoteConfig() ?: return
        config.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
        )
        config.setDefaultsAsync(R.xml.remote_config_defaults)
        refresh()
    }

    override fun refresh(onUpdated: () -> Unit) {
        remoteConfig()?.fetchAndActivate()?.addOnCompleteListener {
            Log.d(TAG, "Remote Config updated: ${it.isSuccessful}")
            onUpdated()
        }
    }

    override fun welcomeMessage(): String =
        remoteConfig()?.getString(WELCOME_MESSAGE).orEmpty()
            .ifBlank { "Добро пожаловать в Потерял-Нашёл" }

    override fun isExperimentalFeatureEnabled(): Boolean =
        remoteConfig()?.getBoolean(EXPERIMENT_ENABLED) ?: false

    private fun remoteConfig(): FirebaseRemoteConfig? =
        runCatching { FirebaseRemoteConfig.getInstance() }.getOrNull()

    private companion object {
        const val TAG = "RemoteConfig"
        const val WELCOME_MESSAGE = "welcome_message"
        const val EXPERIMENT_ENABLED = "experimental_feature_enabled"
    }
}
