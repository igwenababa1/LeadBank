package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "linked_accounts_metadata")
data class LinkedAccountMetadataEntity(
    @PrimaryKey val accountId: String,
    val institutionId: String,
    val institutionName: String,
    val accountName: String,
    val officialAccountType: String, // "CHECKING", "HIGH_YIELD_SAVINGS", "TREASURY_INVESTMENT", "CRYPTO_ALPHA"
    val mask: String,
    val fullAccountNumberMasked: String,
    val routingNumber: String,
    val wireRoutingTransit: String,
    val availableBalance: Double,
    val currentBalance: Double,
    val currency: String = "USD",
    val dailyTransferLimit: Double = 250000.0,
    val monthlyTransferLimit: Double = 2500000.0,
    val verificationTier: String = "KYC_TIER_1_VERIFIED",
    val isPrimary: Boolean = false,
    val isDirectDepositActive: Boolean = true,
    val linkedAtTimestamp: Long = System.currentTimeMillis(),
    val lastSyncedTimestamp: Long = System.currentTimeMillis(),
    val metadataVersion: Int = 1
)
