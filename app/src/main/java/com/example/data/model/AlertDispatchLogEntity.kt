package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert_dispatch_logs")
data class AlertDispatchLogEntity(
    @PrimaryKey val id: String,
    val channel: String, // "EMAIL_RESEND", "SMS_TWILIO", "PUSH_FCM"
    val eventType: String, // "TRANSACTION_APPROVAL", "SECURITY_CHANGE", "DEPOSIT_NOTIFICATION"
    val recipient: String,
    val subject: String,
    val bodySummary: String,
    val fullContent: String,
    val status: String, // "DELIVERED", "SENT", "SIMULATED_SANDBOX", "FAILED"
    val externalMessageId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val latencyMs: Long = 0L
)
