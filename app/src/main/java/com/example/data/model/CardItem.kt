package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CardTheme {
    OBSIDIAN,
    CHASE_SAPPHIRE,
    AMEX_PLATINUM,
    APPLE_TITANIUM,
    GOLD,
    EMERALD,
    CITI_CUSTOM,
    CAPITAL_ONE_VENTURE,
    BARCLAYS_BLACK
}

enum class CardTier(val label: String) {
    METAL("Official Heavy Metal"),
    VIRTUAL("Verified Digital Token"),
    DISPOSABLE("Burner Single-Use")
}

enum class CardNetwork(val displayName: String) {
    VISA_INFINITE("Visa Infinite"),
    MASTERCARD_WORLD_ELITE("Mastercard World Elite"),
    AMEX("American Express Centurion")
}

enum class PhysicalDeliveryStatus(val label: String, val stepIndex: Int) {
    DRAFT("Draft Application", 0),
    SUBMITTED("Submitted", 1),
    UNDER_REVIEW("Under Review", 2),
    APPROVED("Approved", 3),
    CARD_ISSUED("Card Issued", 4),
    CARD_SHIPPED("Armored Courier Dispatched", 5),
    DELIVERED("Delivered & Activated", 6)
}

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey val id: String,
    val bankName: String = "Lead Private Bank",
    val cardProductName: String = "Lead Obsidian Reserve",
    val cardholderName: String = "ALEXANDER VANCE",
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val theme: CardTheme = CardTheme.OBSIDIAN,
    val tier: CardTier = CardTier.METAL,
    val network: CardNetwork = CardNetwork.VISA_INFINITE,
    val isFrozen: Boolean = false,
    val spendingLimit: Double = 50000.0,
    val currentSpent: Double = 6348.70,
    val contactlessEnabled: Boolean = true,
    val onlinePurchasesEnabled: Boolean = true,
    val linkedAccountId: String = "acc_checking",
    val isOfficialLinkedBankCard: Boolean = false,
    val deliveryStatus: PhysicalDeliveryStatus = PhysicalDeliveryStatus.DELIVERED,
    val trackingNumber: String = "LD-88492019-US",
    val supabaseId: String? = null,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastSyncedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    val maskedNumber: String
        get() = "•••• •••• •••• " + cardNumber.takeLast(4)

    val availableCreditOrBalance: Double
        get() = (spendingLimit - currentSpent).coerceAtLeast(0.0)
}
