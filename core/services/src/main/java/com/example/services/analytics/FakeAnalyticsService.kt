package com.example.services.analytics

import android.util.Log

class FakeAnalyticsService : AnalyticsService {
    val events = mutableListOf<Pair<String, Map<String, Any>>>()
    val errors = mutableListOf<Pair<String, Throwable?>>()

    override fun initialize() = Unit

    override fun trackEvent(name: String, params: Map<String, Any>) {
        events += name to params
        runCatching { Log.d(TAG, "$name: $params") }
    }

    override fun trackError(message: String, error: Throwable?) {
        errors += message to error
        runCatching { Log.d(TAG, message, error) }
    }

    private companion object {
        const val TAG = "FakeAnalyticsService"
    }
}
