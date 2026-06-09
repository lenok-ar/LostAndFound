package com.example.services.analytics

interface AnalyticsService {
    fun initialize()
    fun trackEvent(name: String, params: Map<String, Any> = emptyMap())
    fun trackError(message: String, error: Throwable?)
}
