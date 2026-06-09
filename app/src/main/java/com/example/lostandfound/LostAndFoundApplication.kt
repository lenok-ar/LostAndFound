package com.example.lostandfound

import android.app.Application
import com.example.services.analytics.AnalyticsService
import com.example.services.crash.CrashReporter
import com.example.services.map.MapService
import com.example.services.remoteconfig.RemoteConfigService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.lostandfound.worker.RemoteConfigSyncWorker
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class LostAndFoundApplication : Application() {
    @Inject
    lateinit var analyticsService: AnalyticsService
    @Inject
    lateinit var mapService: MapService
    @Inject
    lateinit var remoteConfigService: RemoteConfigService
    @Inject
    lateinit var crashReporter: CrashReporter

    override fun onCreate() {
        super.onCreate()
        initializeSafely("AppMetrica") { analyticsService.initialize() }
        initializeSafely("Yandex MapKit") { mapService.initialize() }
        initializeSafely("Firebase Remote Config") { remoteConfigService.initialize() }
        scheduleRemoteConfigSync()
    }

    private fun initializeSafely(service: String, initialize: () -> Unit) {
        runCatching(initialize).onFailure { error ->
            crashReporter.setKey("initialization_service", service)
            crashReporter.recordNonFatal(error)
        }
    }

    private fun scheduleRemoteConfigSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = PeriodicWorkRequestBuilder<RemoteConfigSyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            RemoteConfigSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
