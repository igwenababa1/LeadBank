package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionCategory(val displayName: String) {
    SALARY("Income & Payroll"),
    INVESTMENT("Dividends & Returns"),
    TRANSFER("Wire & Transfers"),
    CRYPTO_TRADE("Digital Assets & Crypto"),
    DINING("Gastronomy & Dining"),
    SHOPPING("Luxury & Retail"),
    TECH("Cloud & Tech"),
    TRAVEL("Aviation & Travel"),
    UTILITIES("Executive Services"),
    ENTERTAINMENT("Lifestyle & Club")
}

enum class TransactionStatus {
    COMPLETED,
    PENDING,
    REFUNDED
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val merchant: String,
    val amount: Double, // positive for credit, negative for debit
    val category: TransactionCategory,
    val status: TransactionStatus,
    val timestamp: Long,
    val reference: String,
    val note: String = "",
    val paymentMethod: String = "Lead Obsidian •• 8842",
    val location: String = "San Francisco, CA",
    val isDisputed: Boolean = false,
    val supabaseId: String? = null,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastSyncedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
