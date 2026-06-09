package com.example.lostandfound.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class RemoteConfigSyncWorker(
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        val latch = CountDownLatch(1)
        var successful = false

        FirebaseRemoteConfig.getInstance().fetchAndActivate()
            .addOnCompleteListener {
                successful = it.isSuccessful
                latch.countDown()
            }

        val completed = latch.await(20, TimeUnit.SECONDS)
        return when {
            !completed -> Result.retry()
            successful -> Result.success()
            else -> Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "remote_config_periodic_sync"
    }
}
