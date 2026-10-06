package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_transactions")
data class CachedTransactionEntity(
    @PrimaryKey val id: String,
    val accountId: String,
    val amount: Double,
    val type: String, // "DEBIT", "CREDIT", "SWAP", "TRANSFER", "SETTLEMENT"
    val title: String,
    val merchant: String,
    val category: String,
    val status: String, // "COMPLETED", "PENDING_APPROVAL", "FLAGGED", "SETTLED"
    val requiresApproval: Boolean = false,
    val approvalCode: String? = null,
    val riskScore: Int = 0,
    val authChannelUsed: String = "MULTI_FACTOR", // "RESEND_EMAIL", "TWILIO_SMS", "FCM_PUSH"
    val timestamp: Long,
    val referenceNumber: String,
    val memo: String = "",
    val recipientDetails: String = "",
    val fee: Double = 0.0,
    val cachedAt: Long = System.currentTimeMillis(),
    val isOfflineCached: Boolean = true
)
