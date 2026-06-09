package com.example.services.remoteconfig

interface RemoteConfigService {
    fun initialize()
    fun refresh(onUpdated: () -> Unit = {})
    fun welcomeMessage(): String
    fun isExperimentalFeatureEnabled(): Boolean
}
