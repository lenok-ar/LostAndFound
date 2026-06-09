package com.example.services.crash

interface CrashReporter {
    fun log(message: String)
    fun setKey(key: String, value: String)
    fun setUserId(userId: String?)
    fun recordNonFatal(error: Throwable)
}
