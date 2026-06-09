package com.example.services.analytics

import android.content.Context
import android.app.Application
import android.util.Log
import com.example.services.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMetricaAnalyticsService @Inject constructor(
    @ApplicationContext private val context: Context
) : AnalyticsService {

    override fun initialize() {
        if (BuildConfig.APPMETRICA_API_KEY.isBlank()) {
            Log.i(TAG, "APPMETRICA_API_KEY is not configured")
            return
        }
        val config = AppMetricaConfig.newConfigBuilder(
            BuildConfig.APPMETRICA_API_KEY).build()
        AppMetrica.activate(context, config)
        AppMetrica.enableActivityAutoTracking(context as Application)
    }

    override fun trackEvent(name: String, params: Map<String, Any>) {
        if (BuildConfig.APPMETRICA_API_KEY.isBlank()) {
            Log.d(TAG, "$name: $params")
        } else {
            AppMetrica.reportEvent(name, params)
        }
    }

    override fun trackError(message: String, error: Throwable?) {
        if (BuildConfig.APPMETRICA_API_KEY.isBlank()) {
            Log.e(TAG, message, error)
        } else {
            AppMetrica.reportError(message, error)
        }
    }

    private companion object {
        const val TAG = "AnalyticsService"
    }
}
