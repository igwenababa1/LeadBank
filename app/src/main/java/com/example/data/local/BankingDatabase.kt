package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.AccountEntity
import com.example.data.model.AdminAuditLogEntity
import com.example.data.model.AlertDispatchLogEntity
import com.example.data.model.CachedTransactionEntity
import com.example.data.model.CardEntity
import com.example.data.model.CryptoHoldingEntity
import com.example.data.model.CryptoOrderEntity
import com.example.data.model.CryptoWalletAccount
import com.example.data.model.CustomerEmailEntity
import com.example.data.model.LinkedAccountMetadataEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity

@Database(
    entities = [
        AccountEntity::class,
        TransactionEntity::class,
        CardEntity::class,
        SavingsGoalEntity::class,
        CryptoHoldingEntity::class,
        CryptoOrderEntity::class,
        CryptoWalletAccount::class,
        AdminAuditLogEntity::class,
        CustomerEmailEntity::class,
        CachedTransactionEntity::class,
        LinkedAccountMetadataEntity::class,
        AlertDispatchLogEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BankingDatabase : RoomDatabase() {

    abstract fun bankingDao(): BankingDao

    companion object {
        @Volatile
        private var INSTANCE: BankingDatabase? = null

        fun getDatabase(context: Context): BankingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BankingDatabase::class.java,
                    "lead_banking.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
