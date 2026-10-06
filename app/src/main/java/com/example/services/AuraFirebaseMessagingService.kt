package com.example.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AuraFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "Aura Security Alert"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "New notification from Aura Private Wealth."

        val eventType = remoteMessage.data["event_type"] ?: "SECURITY_ALERT"
        val referenceId = remoteMessage.data["reference_id"] ?: ""

        showNotification(applicationContext, title, body, eventType, referenceId)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "Refreshed FCM Device Registration Token: $token")
        saveFcmToken(applicationContext, token)
    }

    companion object {
        private const val TAG = "AuraFCMService"
        const val CHANNEL_ID = "aura_security_banking_alerts"
        const val CHANNEL_NAME = "Aura Priority Security & Banking Alerts"
        private const val PREFS_NAME = "aura_fcm_prefs"
        private const val KEY_FCM_TOKEN = "fcm_device_token"

        fun saveFcmToken(context: Context, token: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
        }

        fun getFcmToken(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_FCM_TOKEN, null) ?: "fcm_token_device_${Build.MODEL.replace(" ", "_")}"
        }

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Instant multi-factor alerts for wire approvals, fraud shields, and deposit settlements."
                    enableLights(true)
                    lightColor = 0xFFE5C378.toInt()
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 250, 150, 250)
                    setShowBadge(true)
                }

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun showNotification(
            context: Context,
            title: String,
            body: String,
            eventType: String = "BANKING_ALERT",
            referenceId: String = ""
        ) {
            createNotificationChannel(context)

            // Verify POST_NOTIFICATIONS permission on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.w(TAG, "POST_NOTIFICATIONS permission not granted. Push notification skipped.")
                    return
                }
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("alert_event_type", eventType)
                putExtra("alert_reference_id", referenceId)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(0xFFE5C378.toInt())
                .setContentIntent(pendingIntent)

            try {
                val notificationManager = NotificationManagerCompat.from(context)
                val notificationId = (System.currentTimeMillis() % 100000).toInt()
                notificationManager.notify(notificationId, notificationBuilder.build())
                Log.i(TAG, "Posted push notification: $title [ID: $notificationId]")
            } catch (e: SecurityException) {
                Log.w(TAG, "SecurityException while notifying: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to post notification", e)
            }
        }
    }
}
