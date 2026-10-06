package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import com.example.data.model.SyncStatus
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BankingDao {

    // Accounts
    @Query("SELECT * FROM accounts WHERE isDeleted = 0 ORDER BY isPrimary DESC, balance DESC")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: String): AccountEntity?

    @Query("UPDATE accounts SET isDeleted = 1 WHERE id = :id")
    suspend fun deleteAccount(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSyncAccounts(): List<AccountEntity>

    @Query("UPDATE accounts SET syncStatus = :status, lastSyncedAt = :lastSyncedAt, supabaseId = COALESCE(:supabaseId, supabaseId) WHERE id = :id")
    suspend fun updateAccountSyncStatus(id: String, status: SyncStatus, lastSyncedAt: Long, supabaseId: String?)

    // Transactions
    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("UPDATE transactions SET isDisputed = :isDisputed WHERE id = :id")
    suspend fun updateDisputeStatus(id: String, isDisputed: Boolean)

    @Query("UPDATE transactions SET isDeleted = 1 WHERE id = :id")
    suspend fun deleteTransaction(id: String)

    @Query("SELECT * FROM transactions WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSyncTransactions(): List<TransactionEntity>

    @Query("UPDATE transactions SET syncStatus = :status, lastSyncedAt = :lastSyncedAt, supabaseId = COALESCE(:supabaseId, supabaseId) WHERE id = :id")
    suspend fun updateTransactionSyncStatus(id: String, status: SyncStatus, lastSyncedAt: Long, supabaseId: String?)

    // Cards
    @Query("SELECT * FROM cards WHERE isDeleted = 0")
    fun getAllCards(): Flow<List<CardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity)

    @Update
    suspend fun updateCard(card: CardEntity)

    @Query("UPDATE cards SET isFrozen = :isFrozen WHERE id = :id")
    suspend fun updateCardFrozenState(id: String, isFrozen: Boolean)

    @Query("UPDATE cards SET isDeleted = 1 WHERE id = :id")
    suspend fun deleteCard(id: String)

    @Query("UPDATE cards SET spendingLimit = :limit WHERE id = :id")
    suspend fun updateCardLimit(id: String, limit: Double)

    @Query("SELECT * FROM cards WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSyncCards(): List<CardEntity>

    @Query("UPDATE cards SET syncStatus = :status, lastSyncedAt = :lastSyncedAt, supabaseId = COALESCE(:supabaseId, supabaseId) WHERE id = :id")
    suspend fun updateCardSyncStatus(id: String, status: SyncStatus, lastSyncedAt: Long, supabaseId: String?)

    // Savings Goals
    @Query("SELECT * FROM savings_goals WHERE isDeleted = 0")
    fun getAllSavingsGoals(): Flow<List<SavingsGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoals(goals: List<SavingsGoalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoal(goal: SavingsGoalEntity)

    @Update
    suspend fun updateSavingsGoal(goal: SavingsGoalEntity)

    @Query("SELECT * FROM savings_goals WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSyncSavingsGoals(): List<SavingsGoalEntity>

    @Query("UPDATE savings_goals SET syncStatus = :status, lastSyncedAt = :lastSyncedAt, supabaseId = COALESCE(:supabaseId, supabaseId) WHERE id = :id")
    suspend fun updateSavingsGoalSyncStatus(id: String, status: SyncStatus, lastSyncedAt: Long, supabaseId: String?)

    // Crypto Holdings
    @Query("SELECT * FROM crypto_holdings ORDER BY (balance * currentPriceUsd) DESC, volume24hUsd DESC")
    fun getAllCryptoHoldings(): Flow<List<CryptoHoldingEntity>>

    @Query("SELECT * FROM crypto_holdings WHERE symbol = :symbol LIMIT 1")
    suspend fun getCryptoHolding(symbol: String): CryptoHoldingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCryptoHoldings(holdings: List<CryptoHoldingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCryptoHolding(holding: CryptoHoldingEntity)

    @Update
    suspend fun updateCryptoHolding(holding: CryptoHoldingEntity)

    @Query("SELECT * FROM crypto_holdings WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSyncCryptoHoldings(): List<CryptoHoldingEntity>

    // Crypto Orders
    @Query("SELECT * FROM crypto_orders ORDER BY timestamp DESC")
    fun getAllCryptoOrders(): Flow<List<CryptoOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCryptoOrder(order: CryptoOrderEntity)

    @Query("SELECT * FROM crypto_orders WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSyncCryptoOrders(): List<CryptoOrderEntity>

    @Query("UPDATE crypto_orders SET syncStatus = :status, lastSyncedAt = :lastSyncedAt WHERE orderId = :orderId")
    suspend fun updateCryptoOrderSyncStatus(orderId: String, status: SyncStatus, lastSyncedAt: Long)

    // Crypto Wallets (Local Room Persistence & Offline State Management)
    @Query("SELECT * FROM crypto_wallets WHERE isDeleted = 0 ORDER BY isPrimary DESC, balanceUsd DESC, lastSyncedAt DESC")
    fun getAllCryptoWallets(): Flow<List<CryptoWalletAccount>>

    @Query("SELECT * FROM crypto_wallets WHERE id = :id LIMIT 1")
    suspend fun getCryptoWalletById(id: String): CryptoWalletAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCryptoWallet(wallet: CryptoWalletAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCryptoWallets(wallets: List<CryptoWalletAccount>)

    @Update
    suspend fun updateCryptoWallet(wallet: CryptoWalletAccount)

    @Query("UPDATE crypto_wallets SET isDeleted = 1 WHERE id = :id")
    suspend fun deleteCryptoWallet(id: String)

    @Query("SELECT * FROM crypto_wallets WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSyncCryptoWallets(): List<CryptoWalletAccount>

    @Query("UPDATE crypto_wallets SET syncStatus = :status, lastSyncedAt = :lastSyncedAt, supabaseId = COALESCE(:supabaseId, supabaseId) WHERE id = :id")
    suspend fun updateCryptoWalletSyncStatus(id: String, status: SyncStatus, lastSyncedAt: Long, supabaseId: String?)

    // Immutable Compliance Audit Logs
    @Query("SELECT * FROM immutable_audit_logs ORDER BY sequenceNumber DESC")
    fun getAllAuditLogs(): Flow<List<AdminAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AdminAuditLogEntity)

    @Query("SELECT * FROM immutable_audit_logs ORDER BY sequenceNumber DESC LIMIT 1")
    suspend fun getLatestAuditLog(): AdminAuditLogEntity?

    @Query("SELECT COUNT(*) FROM immutable_audit_logs")
    suspend fun getAuditLogCount(): Long

    // Customer Communications & Sent Emails
    @Query("SELECT * FROM customer_emails ORDER BY timestamp DESC")
    fun getAllCustomerEmails(): Flow<List<CustomerEmailEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomerEmail(email: CustomerEmailEntity)

    // Cached Transaction History (Local Offline Cache)
    @Query("SELECT * FROM cached_transactions ORDER BY timestamp DESC")
    fun getAllCachedTransactions(): Flow<List<CachedTransactionEntity>>

    @Query("SELECT * FROM cached_transactions WHERE accountId = :accountId ORDER BY timestamp DESC")
    fun getCachedTransactionsForAccount(accountId: String): Flow<List<CachedTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedTransaction(tx: CachedTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedTransactions(txs: List<CachedTransactionEntity>)

    @Query("DELETE FROM cached_transactions WHERE id = :id")
    suspend fun deleteCachedTransaction(id: String)

    @Query("DELETE FROM cached_transactions")
    suspend fun clearAllCachedTransactions()

    // Linked Bank Account Metadata (Local Persistence)
    @Query("SELECT * FROM linked_accounts_metadata ORDER BY isPrimary DESC, availableBalance DESC")
    fun getAllLinkedAccountsMetadata(): Flow<List<LinkedAccountMetadataEntity>>

    @Query("SELECT * FROM linked_accounts_metadata WHERE accountId = :accountId LIMIT 1")
    suspend fun getLinkedAccountMetadata(accountId: String): LinkedAccountMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinkedAccountMetadata(metadata: LinkedAccountMetadataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinkedAccountsMetadata(metadataList: List<LinkedAccountMetadataEntity>)

    @Query("DELETE FROM linked_accounts_metadata WHERE accountId = :accountId")
    suspend fun deleteLinkedAccountMetadata(accountId: String)

    // Real-Time Alert Dispatch Logs (Resend, Twilio, FCM)
    @Query("SELECT * FROM alert_dispatch_logs ORDER BY timestamp DESC")
    fun getAllAlertLogs(): Flow<List<AlertDispatchLogEntity>>

    @Query("SELECT * FROM alert_dispatch_logs WHERE channel = :channel ORDER BY timestamp DESC")
    fun getAlertLogsByChannel(channel: String): Flow<List<AlertDispatchLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlertLog(log: AlertDispatchLogEntity)

    @Query("DELETE FROM alert_dispatch_logs")
    suspend fun clearAllAlertLogs()
}
