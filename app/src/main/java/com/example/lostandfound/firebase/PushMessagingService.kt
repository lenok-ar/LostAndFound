package com.example.lostandfound.firebase

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.lostandfound.MainActivity
import com.example.lostandfound.R
import com.example.services.crash.CrashReporter
import com.example.services.profile.UserProfileService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PushMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var profileService: UserProfileService
    @Inject
    lateinit var crashReporter: CrashReporter

    override fun onNewToken(token: String) {
        runCatching {
            getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit().putString(FCM_TOKEN, token).apply()
            Log.d(TAG, "FCM token generated")
            profileService.updateFcmToken(token)
        }.onFailure(::reportError)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        runCatching {
            val title = message.data["title"] ?: message.notification?.title ?: "Потерял-Нашёл"
            val body = message.data["body"] ?: message.notification?.body ?: "Новое уведомление"
            showNotification(title, body, message.data)
        }.onFailure(::reportError)
    }

    private fun reportError(error: Throwable) {
        crashReporter.setKey("component", "push_messaging")
        crashReporter.recordNonFatal(error)
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data.forEach { (key, value) -> putExtra(key, value) }
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Основные уведомления",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private companion object {
        const val TAG = "PushMessagingService"
        const val PREFERENCES = "firebase"
        const val FCM_TOKEN = "fcm_token"
        const val CHANNEL_ID = "lost_and_found_notifications"
        const val NOTIFICATION_ID = 1001
    }
}
