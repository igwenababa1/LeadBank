package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val apy: Double = 5.15,
    val targetDate: String,
    val autoRoundUp: Boolean = true,
    val iconName: String = "Vault",
    val supabaseId: String? = null,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastSyncedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    val progress: Float
        get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f

    val projectedAnnualInterest: Double
        get() = currentAmount * (apy / 100.0)
}
