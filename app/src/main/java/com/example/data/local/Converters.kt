package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AccountType
import com.example.data.model.BlockchainNetwork
import com.example.data.model.CardNetwork
import com.example.data.model.CardTheme
import com.example.data.model.CardTier
import com.example.data.model.PhysicalDeliveryStatus
import com.example.data.model.SyncStatus
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionStatus

class Converters {
    @TypeConverter
    fun fromAccountType(value: AccountType?): String = value?.name ?: AccountType.CHECKING.name

    @TypeConverter
    fun toAccountType(value: String?): AccountType = try {
        value?.let { AccountType.valueOf(it) } ?: AccountType.CHECKING
    } catch (_: Exception) {
        AccountType.CHECKING
    }

    @TypeConverter
    fun fromTransactionCategory(value: TransactionCategory?): String = value?.name ?: TransactionCategory.TRANSFER.name

    @TypeConverter
    fun toTransactionCategory(value: String?): TransactionCategory = try {
        value?.let { TransactionCategory.valueOf(it) } ?: TransactionCategory.TRANSFER
    } catch (_: Exception) {
        TransactionCategory.TRANSFER
    }

    @TypeConverter
    fun fromTransactionStatus(value: TransactionStatus?): String = value?.name ?: TransactionStatus.COMPLETED.name

    @TypeConverter
    fun toTransactionStatus(value: String?): TransactionStatus = try {
        value?.let { TransactionStatus.valueOf(it) } ?: TransactionStatus.COMPLETED
    } catch (_: Exception) {
        TransactionStatus.COMPLETED
    }

    @TypeConverter
    fun fromCardTheme(value: CardTheme?): String = value?.name ?: CardTheme.OBSIDIAN.name

    @TypeConverter
    fun toCardTheme(value: String?): CardTheme = try {
        value?.let { CardTheme.valueOf(it) } ?: CardTheme.OBSIDIAN
    } catch (_: Exception) {
        CardTheme.OBSIDIAN
    }

    @TypeConverter
    fun fromCardTier(value: CardTier?): String = value?.name ?: CardTier.METAL.name

    @TypeConverter
    fun toCardTier(value: String?): CardTier = try {
        value?.let { CardTier.valueOf(it) } ?: CardTier.METAL
    } catch (_: Exception) {
        CardTier.METAL
    }

    @TypeConverter
    fun fromCardNetwork(value: CardNetwork?): String = value?.name ?: CardNetwork.VISA_INFINITE.name

    @TypeConverter
    fun toCardNetwork(value: String?): CardNetwork = try {
        value?.let { CardNetwork.valueOf(it) } ?: CardNetwork.VISA_INFINITE
    } catch (_: Exception) {
        CardNetwork.VISA_INFINITE
    }

    @TypeConverter
    fun fromPhysicalDeliveryStatus(value: PhysicalDeliveryStatus?): String = value?.name ?: PhysicalDeliveryStatus.DELIVERED.name

    @TypeConverter
    fun toPhysicalDeliveryStatus(value: String?): PhysicalDeliveryStatus = try {
        value?.let { PhysicalDeliveryStatus.valueOf(it) } ?: PhysicalDeliveryStatus.DELIVERED
    } catch (_: Exception) {
        PhysicalDeliveryStatus.DELIVERED
    }

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus?): String = value?.name ?: SyncStatus.SYNCED.name

    @TypeConverter
    fun toSyncStatus(value: String?): SyncStatus = try {
        value?.let { SyncStatus.valueOf(it) } ?: SyncStatus.SYNCED
    } catch (_: Exception) {
        SyncStatus.SYNCED
    }

    @TypeConverter
    fun fromBlockchainNetwork(value: BlockchainNetwork?): String = value?.name ?: BlockchainNetwork.BITCOIN.name

    @TypeConverter
    fun toBlockchainNetwork(value: String?): BlockchainNetwork = try {
        value?.let { BlockchainNetwork.valueOf(it) } ?: BlockchainNetwork.BITCOIN
    } catch (_: Exception) {
        BlockchainNetwork.BITCOIN
    }
}
