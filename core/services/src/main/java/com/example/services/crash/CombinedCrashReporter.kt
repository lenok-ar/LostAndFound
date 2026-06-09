package com.example.services.crash

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CombinedCrashReporter @Inject constructor(
    private val firebase: FirebaseCrashReporter,
    private val appMetrica: AppMetricaCrashReporter
) : CrashReporter {
    override fun log(message: String) {
        runCatching { firebase.log(message) }
        runCatching { appMetrica.log(message) }
    }

    override fun setKey(key: String, value: String) {
        runCatching { firebase.setKey(key, value) }
        runCatching { appMetrica.setKey(key, value) }
    }

    override fun setUserId(userId: String?) {
        runCatching { firebase.setUserId(userId) }
        runCatching { appMetrica.setUserId(userId) }
    }

    override fun recordNonFatal(error: Throwable) {
        runCatching { firebase.recordNonFatal(error) }
        runCatching { appMetrica.recordNonFatal(error) }
    }
}
