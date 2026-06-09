package com.example.services.crash

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseCrashReporter @Inject constructor() : CrashReporter {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(message: String) = crashlytics.log(message)

    override fun setKey(key: String, value: String) = crashlytics.setCustomKey(key, value)

    override fun setUserId(userId: String?) = crashlytics.setUserId(userId.orEmpty())

    override fun recordNonFatal(error: Throwable) = crashlytics.recordException(error)
}
