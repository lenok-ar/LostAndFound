package com.example.services.crash

import io.appmetrica.analytics.AppMetrica
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMetricaCrashReporter @Inject constructor() : CrashReporter {
    override fun log(message: String) {
        AppMetrica.reportEvent("crash_context", mapOf("message" to message))
    }

    override fun setKey(key: String, value: String) {
        AppMetrica.reportEvent("crash_context", mapOf(key to value))
    }

    override fun setUserId(userId: String?) = Unit

    override fun recordNonFatal(error: Throwable) {
        AppMetrica.reportError("non_fatal_error", error)
    }
}
