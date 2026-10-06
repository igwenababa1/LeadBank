package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AccountType(val displayName: String, val badge: String) {
    CHECKING("Private Checking", "Tier 1"),
    HIGH_YIELD_SAVINGS("High-Yield Vault", "5.15% APY"),
    TREASURY_INVESTMENT("Treasury & Equities", "Managed"),
    CRYPTO_ALPHA("Digital Assets", "Institutional")
}

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val currency: String = "USD",
    val accountNumber: String,
    val routingNumber: String,
    val apy: Double = 0.0,
    val isPrimary: Boolean = false,
    val supabaseId: String? = null,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastSyncedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
