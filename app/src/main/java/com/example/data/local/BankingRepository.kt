package com.example.data.local

import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.AdminAuditLogEntity
import com.example.data.model.AlertDispatchLogEntity
import com.example.data.model.BlockchainNetwork
import com.example.data.model.CachedTransactionEntity
import com.example.data.model.CardEntity
import com.example.data.model.CardNetwork
import com.example.data.model.CardTheme
import com.example.data.model.CardTier
import com.example.data.model.CryptoAuditHasher
import com.example.data.model.CryptoHoldingEntity
import com.example.data.model.CryptoOrderEntity
import com.example.data.model.CryptoWalletAccount
import com.example.data.model.CustomerEmailEntity
import com.example.data.model.LinkedAccountMetadataEntity
import com.example.data.model.LiveMarketDepth
import com.example.data.model.OrderBookEntry
import com.example.data.model.PhysicalDeliveryStatus
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.SupabaseSyncState
import com.example.data.model.SyncStatus
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class BankingRepository(private val dao: BankingDao) {

    val accounts: Flow<List<AccountEntity>> = dao.getAllAccounts()
    val transactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val cards: Flow<List<CardEntity>> = dao.getAllCards()
    val savingsGoals: Flow<List<SavingsGoalEntity>> = dao.getAllSavingsGoals()
    val cryptoHoldings: Flow<List<CryptoHoldingEntity>> = dao.getAllCryptoHoldings()
    val cryptoOrders: Flow<List<CryptoOrderEntity>> = dao.getAllCryptoOrders()
    val cryptoWallets: Flow<List<CryptoWalletAccount>> = dao.getAllCryptoWallets()
    val auditLogs: Flow<List<AdminAuditLogEntity>> = dao.getAllAuditLogs()
    val customerEmails: Flow<List<CustomerEmailEntity>> = dao.getAllCustomerEmails()

    // Real-Time 3-Channel Alerts and Room Local Caches
    val cachedTransactions: Flow<List<CachedTransactionEntity>> = dao.getAllCachedTransactions()
    val linkedAccountsMetadata: Flow<List<LinkedAccountMetadataEntity>> = dao.getAllLinkedAccountsMetadata()
    val alertLogs: Flow<List<AlertDispatchLogEntity>> = dao.getAllAlertLogs()

    private val _supabaseSyncState = MutableStateFlow(SupabaseSyncState())
    val supabaseSyncState: StateFlow<SupabaseSyncState> = _supabaseSyncState.asStateFlow()

    suspend fun seedInitialDataIfEmpty() {
        val existingAccounts = dao.getAllAccounts().firstOrNull()
        if (existingAccounts.isNullOrEmpty()) {
            val initialAccounts = listOf(
                AccountEntity(
                    id = "acc_checking",
                    name = "Lead Private Checking",
                    type = AccountType.CHECKING,
                    balance = 128450.75,
                    currency = "USD",
                    accountNumber = "8839201948",
                    routingNumber = "121000358",
                    apy = 1.25,
                    isPrimary = true,
                    supabaseId = "sup_acc_01",
                    syncStatus = SyncStatus.SYNCED
                ),
                AccountEntity(
                    id = "acc_vault",
                    name = "Lead High-Yield Reserve",
                    type = AccountType.HIGH_YIELD_SAVINGS,
                    balance = 345000.00,
                    currency = "USD",
                    accountNumber = "9940129381",
                    routingNumber = "121000358",
                    apy = 5.40,
                    isPrimary = false,
                    supabaseId = "sup_acc_02",
                    syncStatus = SyncStatus.SYNCED
                ),
                AccountEntity(
                    id = "acc_treasury",
                    name = "Lead Treasury & Equities",
                    type = AccountType.TREASURY_INVESTMENT,
                    balance = 520840.50,
                    currency = "USD",
                    accountNumber = "4410928371",
                    routingNumber = "121000358",
                    apy = 4.85,
                    isPrimary = false,
                    supabaseId = "sup_acc_03",
                    syncStatus = SyncStatus.SYNCED
                ),
                AccountEntity(
                    id = "acc_crypto",
                    name = "Lead Institutional Crypto Desk",
                    type = AccountType.CRYPTO_ALPHA,
                    balance = 168780.00,
                    currency = "USD",
                    accountNumber = "0x7F91B...482A",
                    routingNumber = "CHAIN-ETH-L1",
                    apy = 6.20,
                    isPrimary = false,
                    supabaseId = "sup_acc_04",
                    syncStatus = SyncStatus.SYNCED
                )
            )
            dao.insertAccounts(initialAccounts)

            // Seed Realistic Transactions
            val now = System.currentTimeMillis()
            val day = 86400000L
            val initialTransactions = listOf(
                TransactionEntity(
                    id = "tx_01",
                    title = "Stripe Payout Enterprise",
                    merchant = "Stripe Inc.",
                    amount = 38500.00,
                    category = TransactionCategory.SALARY,
                    status = TransactionStatus.COMPLETED,
                    timestamp = now - (2 * 3600000L),
                    reference = "FED-992104812",
                    note = "Monthly platform enterprise disbursement",
                    paymentMethod = "Direct ACH Inflow",
                    location = "San Francisco, CA",
                    supabaseId = "sup_tx_01",
                    syncStatus = SyncStatus.SYNCED
                ),
                TransactionEntity(
                    id = "tx_02",
                    title = "Apple Store Fifth Avenue",
                    merchant = "Apple Retail",
                    amount = -3849.00,
                    category = TransactionCategory.SHOPPING,
                    status = TransactionStatus.COMPLETED,
                    timestamp = now - (5 * 3600000L),
                    reference = "AUTH-441829",
                    note = "MacBook Pro M3 Max Titanium & Studio Display",
                    paymentMethod = "Lead Obsidian •• 8842",
                    location = "New York, NY",
                    supabaseId = "sup_tx_02",
                    syncStatus = SyncStatus.SYNCED
                ),
                TransactionEntity(
                    id = "tx_03",
                    title = "Amazon Web Services",
                    merchant = "AWS Cloud EMEA",
                    amount = -1420.50,
                    category = TransactionCategory.TECH,
                    status = TransactionStatus.COMPLETED,
                    timestamp = now - day,
                    reference = "INV-2024-8841",
                    note = "Production Kubernetes Cluster & S3 Compute",
                    paymentMethod = "JPMorgan Chase •• 4210",
                    location = "Dublin, Ireland",
                    supabaseId = "sup_tx_03",
                    syncStatus = SyncStatus.SYNCED
                ),
                TransactionEntity(
                    id = "tx_04",
                    title = "Institutional BTC Spot Buy",
                    merchant = "Lead Crypto Liquidity Hub",
                    amount = -25000.00,
                    category = TransactionCategory.CRYPTO_TRADE,
                    status = TransactionStatus.COMPLETED,
                    timestamp = now - (day * 2),
                    reference = "SWAP-0x9f18e2",
                    note = "Swapped USD to 0.3721 BTC with 0.1% execution spread",
                    paymentMethod = "Lead Private Checking",
                    location = "Institutional Execution Desk",
                    supabaseId = "sup_tx_04",
                    syncStatus = SyncStatus.SYNCED
                ),
                TransactionEntity(
                    id = "tx_05",
                    title = "Nobu Restaurants",
                    merchant = "Nobu Malibu",
                    amount = -640.20,
                    category = TransactionCategory.DINING,
                    status = TransactionStatus.COMPLETED,
                    timestamp = now - (day * 3),
                    reference = "AUTH-881923",
                    note = "Private Chef Omakase Tasting Menu",
                    paymentMethod = "American Express •• 1009",
                    location = "Malibu, CA",
                    supabaseId = "sup_tx_05",
                    syncStatus = SyncStatus.SYNCED
                )
            )
            dao.insertTransactions(initialTransactions)

            // Seed Official Cards
            val initialCards = listOf(
                CardEntity(
                    id = "card_obsidian_lead",
                    bankName = "Lead Private Bank",
                    cardProductName = "Lead Obsidian Reserve",
                    cardholderName = "ALEXANDER VANCE",
                    cardNumber = "4532890144188842",
                    expiryDate = "08/30",
                    cvv = "942",
                    theme = CardTheme.OBSIDIAN,
                    tier = CardTier.METAL,
                    network = CardNetwork.VISA_INFINITE,
                    isFrozen = false,
                    spendingLimit = 50000.0,
                    currentSpent = 6348.70,
                    contactlessEnabled = true,
                    onlinePurchasesEnabled = true,
                    linkedAccountId = "acc_checking",
                    isOfficialLinkedBankCard = false,
                    supabaseId = "sup_card_01",
                    syncStatus = SyncStatus.SYNCED
                ),
                CardEntity(
                    id = "card_chase_sapphire",
                    bankName = "JPMorgan Chase",
                    cardProductName = "Sapphire Reserve Metal",
                    cardholderName = "ALEXANDER VANCE",
                    cardNumber = "4112893049104210",
                    expiryDate = "05/29",
                    cvv = "402",
                    theme = CardTheme.CHASE_SAPPHIRE,
                    tier = CardTier.METAL,
                    network = CardNetwork.VISA_INFINITE,
                    isFrozen = false,
                    spendingLimit = 35000.0,
                    currentSpent = 482.60,
                    contactlessEnabled = true,
                    onlinePurchasesEnabled = true,
                    linkedAccountId = "acc_checking",
                    isOfficialLinkedBankCard = true,
                    supabaseId = "sup_card_02",
                    syncStatus = SyncStatus.SYNCED
                ),
                CardEntity(
                    id = "card_amex_platinum",
                    bankName = "American Express",
                    cardProductName = "Platinum Centurion",
                    cardholderName = "ALEXANDER VANCE",
                    cardNumber = "378282246311009",
                    expiryDate = "12/28",
                    cvv = "8421",
                    theme = CardTheme.AMEX_PLATINUM,
                    tier = CardTier.METAL,
                    network = CardNetwork.AMEX,
                    isFrozen = false,
                    spendingLimit = 100000.0,
                    currentSpent = 360.00,
                    contactlessEnabled = true,
                    onlinePurchasesEnabled = true,
                    linkedAccountId = "acc_checking",
                    isOfficialLinkedBankCard = true,
                    supabaseId = "sup_card_03",
                    syncStatus = SyncStatus.SYNCED
                ),
                CardEntity(
                    id = "card_apple_titanium",
                    bankName = "Goldman Sachs",
                    cardProductName = "Apple Card Titanium",
                    cardholderName = "ALEXANDER VANCE",
                    cardNumber = "5412759902147183",
                    expiryDate = "09/31",
                    cvv = "619",
                    theme = CardTheme.APPLE_TITANIUM,
                    tier = CardTier.METAL,
                    network = CardNetwork.MASTERCARD_WORLD_ELITE,
                    isFrozen = false,
                    spendingLimit = 25000.0,
                    currentSpent = 187.35,
                    contactlessEnabled = true,
                    onlinePurchasesEnabled = true,
                    linkedAccountId = "acc_checking",
                    isOfficialLinkedBankCard = true,
                    supabaseId = "sup_card_04",
                    syncStatus = SyncStatus.SYNCED
                ),
                CardEntity(
                    id = "card_lead_gold_virtual",
                    bankName = "Lead Private Bank",
                    cardProductName = "Lead Gold Digital",
                    cardholderName = "ALEXANDER VANCE",
                    cardNumber = "5412753390214019",
                    expiryDate = "11/29",
                    cvv = "318",
                    theme = CardTheme.GOLD,
                    tier = CardTier.VIRTUAL,
                    network = CardNetwork.MASTERCARD_WORLD_ELITE,
                    isFrozen = false,
                    spendingLimit = 15000.0,
                    currentSpent = 1842.10,
                    contactlessEnabled = true,
                    onlinePurchasesEnabled = true,
                    linkedAccountId = "acc_vault",
                    isOfficialLinkedBankCard = false,
                    supabaseId = "sup_card_05",
                    syncStatus = SyncStatus.SYNCED
                )
            )
            dao.insertCards(initialCards)

            // Seed Savings Goals
            val initialGoals = listOf(
                SavingsGoalEntity(
                    id = "goal_01",
                    title = "Alpine Chalet Reserve (Courchevel)",
                    targetAmount = 500000.0,
                    currentAmount = 285000.0,
                    apy = 5.40,
                    targetDate = "Dec 2026",
                    autoRoundUp = true,
                    iconName = "Chalet",
                    supabaseId = "sup_goal_01",
                    syncStatus = SyncStatus.SYNCED
                ),
                SavingsGoalEntity(
                    id = "goal_02",
                    title = "Pre-IPO Syndicate Allocation",
                    targetAmount = 250000.0,
                    currentAmount = 142500.0,
                    apy = 5.40,
                    targetDate = "Aug 2026",
                    autoRoundUp = false,
                    iconName = "Syndicate",
                    supabaseId = "sup_goal_02",
                    syncStatus = SyncStatus.SYNCED
                )
            )
            dao.insertSavingsGoals(initialGoals)

            // Seed Crypto Holdings
            val initialCryptoHoldings = listOf(
                CryptoHoldingEntity(
                    symbol = "BTC",
                    name = "Bitcoin",
                    balance = 1.4500,
                    currentPriceUsd = 67420.00,
                    change24hPercent = 3.84,
                    high24h = 68150.00,
                    low24h = 64920.00,
                    volume24hUsd = 38400000000.0,
                    iconColorHex = 0xFFF59E0B,
                    networkName = "Bitcoin Core / Lightning",
                    sparklineCsv = "64920,65400,65100,66200,65900,67100,67420",
                    supabaseId = "sup_crypto_btc",
                    syncStatus = SyncStatus.SYNCED
                ),
                CryptoHoldingEntity(
                    symbol = "ETH",
                    name = "Ethereum",
                    balance = 8.2500,
                    currentPriceUsd = 3520.50,
                    change24hPercent = 4.25,
                    high24h = 3590.00,
                    low24h = 3380.00,
                    volume24hUsd = 21200000000.0,
                    iconColorHex = 0xFF6366F1,
                    networkName = "Ethereum Mainnet",
                    sparklineCsv = "3380,3410,3395,3460,3490,3515,3520.5",
                    supabaseId = "sup_crypto_eth",
                    syncStatus = SyncStatus.SYNCED
                ),
                CryptoHoldingEntity(
                    symbol = "SOL",
                    name = "Solana",
                    balance = 115.0000,
                    currentPriceUsd = 158.40,
                    change24hPercent = -1.15,
                    high24h = 164.20,
                    low24h = 154.80,
                    volume24hUsd = 6400000000.0,
                    iconColorHex = 0xFF10B981,
                    networkName = "Solana High-TPS",
                    sparklineCsv = "162,164.2,160,157,155,157.5,158.4",
                    supabaseId = "sup_crypto_sol",
                    syncStatus = SyncStatus.SYNCED
                ),
                CryptoHoldingEntity(
                    symbol = "USDC",
                    name = "USD Coin",
                    balance = 25000.0000,
                    currentPriceUsd = 1.00,
                    change24hPercent = 0.01,
                    high24h = 1.001,
                    low24h = 0.999,
                    volume24hUsd = 8900000000.0,
                    iconColorHex = 0xFF3B82F6,
                    networkName = "Circle Native ERC-20",
                    sparklineCsv = "1.0,1.0,1.0,1.0,1.0,1.0,1.0",
                    supabaseId = "sup_crypto_usdc",
                    syncStatus = SyncStatus.SYNCED
                ),
                CryptoHoldingEntity(
                    symbol = "AVAX",
                    name = "Avalanche",
                    balance = 320.0000,
                    currentPriceUsd = 28.75,
                    change24hPercent = 2.10,
                    high24h = 29.50,
                    low24h = 27.80,
                    volume24hUsd = 920000000.0,
                    iconColorHex = 0xFFEF4444,
                    networkName = "Avalanche C-Chain",
                    sparklineCsv = "27.8,28.1,28.0,28.5,28.9,28.6,28.75",
                    supabaseId = "sup_crypto_avax",
                    syncStatus = SyncStatus.SYNCED
                )
            )
            dao.insertCryptoHoldings(initialCryptoHoldings)

            // Seed Initial Crypto Order History
            val initialOrders = listOf(
                CryptoOrderEntity(
                    orderId = "ord_01",
                    fromSymbol = "USD",
                    toSymbol = "BTC",
                    fromAmount = 25000.00,
                    toAmount = 0.3721,
                    executionPrice = 67180.00,
                    feeUsd = 0.0,
                    slippagePercent = 0.05,
                    txHash = "0x8fa1c94b2938a120ef93847291a0c842",
                    status = "CONFIRMED",
                    timestamp = now - (day * 2),
                    supabaseId = "sup_ord_01",
                    syncStatus = SyncStatus.SYNCED
                ),
                CryptoOrderEntity(
                    orderId = "ord_02",
                    fromSymbol = "ETH",
                    toSymbol = "USDC",
                    fromAmount = 2.50,
                    toAmount = 8800.00,
                    executionPrice = 3520.00,
                    feeUsd = 0.0,
                    slippagePercent = 0.08,
                    txHash = "0x4b901a8837190f82348572019ab7c104",
                    status = "CONFIRMED",
                    timestamp = now - (day * 5),
                    supabaseId = "sup_ord_02",
                    syncStatus = SyncStatus.SYNCED
                )
            )
            initialOrders.forEach { dao.insertCryptoOrder(it) }

            // Seed Crypto Wallets into Local Room Database (Offline-First State Management)
            val initialWallets = listOf(
                CryptoWalletAccount(
                    id = "wal_btc_primary",
                    name = "Sovereign BTC Cold Vault",
                    network = BlockchainNetwork.BITCOIN,
                    publicAddress = "bc1q9v8p4a273k9x4p278k3m8w1",
                    balanceUsd = 184200.0,
                    nativeBalance = 2.732,
                    isConnectedExternal = false,
                    providerName = "Lead Sovereign Enclave",
                    createdAtFormatted = "May 2026",
                    status = "ACTIVE • FIPS-140 ENCLAVE",
                    isPrimary = true,
                    supabaseId = "sup_wal_01",
                    syncStatus = SyncStatus.SYNCED
                ),
                CryptoWalletAccount(
                    id = "wal_eth_coinbase",
                    name = "Coinbase Institutional Prime",
                    network = BlockchainNetwork.ETHEREUM,
                    publicAddress = "0x4A8F90274eD29b8c16E318D41299A",
                    balanceUsd = 82500.0,
                    nativeBalance = 22.0,
                    isConnectedExternal = true,
                    providerName = "Coinbase Institutional",
                    createdAtFormatted = "Jul 2026",
                    status = "CONNECTED • OAUTH 2.0 WATCH",
                    isPrimary = false,
                    supabaseId = "sup_wal_02",
                    syncStatus = SyncStatus.SYNCED
                ),
                CryptoWalletAccount(
                    id = "wal_sol_phantom",
                    name = "Phantom High-Speed Treasury",
                    network = BlockchainNetwork.SOLANA,
                    publicAddress = "Sol8mZqP9xW27kQ4p88kYt319A",
                    balanceUsd = 34100.0,
                    nativeBalance = 235.2,
                    isConnectedExternal = true,
                    providerName = "Phantom Custody",
                    createdAtFormatted = "Aug 2026",
                    status = "CONNECTED • READ-ONLY",
                    isPrimary = false,
                    supabaseId = "sup_wal_03",
                    syncStatus = SyncStatus.SYNCED
                )
            )
            dao.insertCryptoWallets(initialWallets)
        }

        // Check if crypto wallets exist in Room even if accounts already seeded
        val existingWallets = dao.getAllCryptoWallets().firstOrNull()
        if (existingWallets.isNullOrEmpty()) {
            val fallbackWallets = listOf(
                CryptoWalletAccount(
                    id = "wal_btc_primary",
                    name = "Sovereign BTC Cold Vault",
                    network = BlockchainNetwork.BITCOIN,
                    publicAddress = "bc1q9v8p4a273k9x4p278k3m8w1",
                    balanceUsd = 184200.0,
                    nativeBalance = 2.732,
                    isConnectedExternal = false,
                    providerName = "Lead Sovereign Enclave",
                    createdAtFormatted = "May 2026",
                    status = "ACTIVE • FIPS-140 ENCLAVE",
                    isPrimary = true,
                    supabaseId = "sup_wal_01",
                    syncStatus = SyncStatus.SYNCED
                ),
                CryptoWalletAccount(
                    id = "wal_eth_coinbase",
                    name = "Coinbase Institutional Prime",
                    network = BlockchainNetwork.ETHEREUM,
                    publicAddress = "0x4A8F90274eD29b8c16E318D41299A",
                    balanceUsd = 82500.0,
                    nativeBalance = 22.0,
                    isConnectedExternal = true,
                    providerName = "Coinbase Institutional",
                    createdAtFormatted = "Jul 2026",
                    status = "CONNECTED • OAUTH 2.0 WATCH",
                    isPrimary = false,
                    supabaseId = "sup_wal_02",
                    syncStatus = SyncStatus.SYNCED
                ),
                CryptoWalletAccount(
                    id = "wal_sol_phantom",
                    name = "Phantom High-Speed Treasury",
                    network = BlockchainNetwork.SOLANA,
                    publicAddress = "Sol8mZqP9xW27kQ4p88kYt319A",
                    balanceUsd = 34100.0,
                    nativeBalance = 235.2,
                    isConnectedExternal = true,
                    providerName = "Phantom Custody",
                    createdAtFormatted = "Aug 2026",
                    status = "CONNECTED • READ-ONLY",
                    isPrimary = false,
                    supabaseId = "sup_wal_03",
                    syncStatus = SyncStatus.SYNCED
                )
            )
            dao.insertCryptoWallets(fallbackWallets)
        }

        val existingAuditLogs = dao.getAuditLogCount()
        if (existingAuditLogs == 0L) {
            val now = System.currentTimeMillis()
            val genesisHash = "0000000000000000000000000000000000000000000000000000000000000000"
            val h1 = CryptoAuditHasher.calculateHash(1L, now - 86400000L, "Igwenababa@gmail.com", "GENESIS_LEDGER_BOOTSTRAP", "CENTRAL_BANK", 1162071.25, "Depository charter initialization under FDIC $5M sweep network", genesisHash)
            val l1 = AdminAuditLogEntity(
                id = "audit_01",
                sequenceNumber = 1L,
                timestamp = now - 86400000L,
                operatorEmail = "Igwenababa@gmail.com",
                actionCategory = "SYSTEM_GENESIS",
                actionType = "GENESIS_LEDGER_BOOTSTRAP",
                targetEntityId = "CENTRAL_BANK",
                targetCustomer = "Lead Private Bank Charter #NY-00421",
                amount = 1162071.25,
                details = "Depository charter initialization under FDIC $5M sweep network.",
                reasonCode = "GENESIS_COMPLIANCE_START",
                prevHash = genesisHash,
                cryptographicHash = h1
            )
            val h2 = CryptoAuditHasher.calculateHash(2L, now - 43200000L, "Igwenababa@gmail.com", "COMPLIANCE_AML_AUDIT", "acc_checking", 38500.00, "Automated FinCEN OFAC screening passed for Stripe Payout", h1)
            val l2 = AdminAuditLogEntity(
                id = "audit_02",
                sequenceNumber = 2L,
                timestamp = now - 43200000L,
                operatorEmail = "Igwenababa@gmail.com",
                actionCategory = "COMPLIANCE_DISPUTE",
                actionType = "COMPLIANCE_AML_AUDIT",
                targetEntityId = "acc_checking",
                targetCustomer = "Alexander Vance (•• 1948)",
                amount = 38500.00,
                details = "Automated FinCEN OFAC screening passed for Stripe Payout. Level 5 clearance validated.",
                reasonCode = "AML_SANCTION_CLEARED",
                prevHash = h1,
                cryptographicHash = h2
            )
            dao.insertAuditLog(l1)
            dao.insertAuditLog(l2)

            // Seed Initial Customer Communications
            val e1 = CustomerEmailEntity(
                id = "eml_init_01",
                recipientEmail = "alexander.vance@leadwealth.com",
                recipientName = "Alexander Vance",
                subject = "Inbound Fedwire Deposit Confirmation ($38,500.00)",
                templateType = "WIRE_CONFIRMATION",
                headline = "Funds Available in Private Checking",
                bodyText = "We are pleased to inform you that your wire settlement of $38,500.00 from Stripe Enterprise has cleared central RTGS verification and is now available for immediate disbursement.",
                amount = 38500.00,
                referenceCode = "NOTIF-FW-99210",
                dispatchedBy = "Igwenababa@gmail.com",
                timestamp = now - 7200000L,
                deliveryStatus = "DELIVERED_SECURE"
            )
            val e2 = CustomerEmailEntity(
                id = "eml_init_02",
                recipientEmail = "alexander.vance@leadwealth.com",
                recipientName = "Alexander Vance",
                subject = "Annual FDIC Sweep Network Coverage Advisory ($5,000,000)",
                templateType = "REGULATORY_DISCLOSURE",
                headline = "Federal Deposit Insurance Corporation Verification",
                bodyText = "Your aggregate depository assets remain fully covered under Lead's Multi-Bank Insured Sweep Program, securing up to $5,000,000.00 in FDIC insurance with zero counterparty lockup.",
                amount = 5000000.00,
                referenceCode = "NOTIF-FDIC-2026",
                dispatchedBy = "Igwenababa@gmail.com",
                timestamp = now - 86400000L,
                deliveryStatus = "DELIVERED_SECURE"
            )
            dao.insertCustomerEmail(e1)
            dao.insertCustomerEmail(e2)
        }

        // Seed Initial Cached Transaction History (Room Persistence)
        val existingCachedTxs = dao.getAllCachedTransactions().firstOrNull()
        if (existingCachedTxs.isNullOrEmpty()) {
            val now = System.currentTimeMillis()
            val day = 86400000L
            val seedCached = listOf(
                CachedTransactionEntity(
                    id = "CTX-88912",
                    accountId = "acc_checking",
                    amount = -18500.00,
                    type = "TRANSFER",
                    title = "Wire to Morgan Stanley Private",
                    merchant = "Morgan Stanley Wealth",
                    category = "Wire & Transfers",
                    status = "COMPLETED",
                    requiresApproval = true,
                    approvalCode = "482-194",
                    riskScore = 20,
                    authChannelUsed = "RESEND+TWILIO+FCM",
                    timestamp = now - (day * 1),
                    referenceNumber = "CTX-88912",
                    recipientDetails = "Morgan Stanley (Account •• 9921)",
                    fee = 0.0,
                    cachedAt = now,
                    isOfflineCached = true
                ),
                CachedTransactionEntity(
                    id = "CTX-77401",
                    accountId = "acc_checking",
                    amount = 50000.00,
                    type = "CREDIT",
                    title = "FedNow Direct Liquidity Wire",
                    merchant = "Federal Reserve Liquidity Facility",
                    category = "Income & Payroll",
                    status = "SETTLED",
                    requiresApproval = false,
                    timestamp = now - (day * 3),
                    referenceNumber = "CTX-77401",
                    recipientDetails = "Lead Private Checking",
                    fee = 0.0,
                    cachedAt = now,
                    isOfflineCached = true
                ),
                CachedTransactionEntity(
                    id = "CTX-66219",
                    accountId = "acc_vault",
                    amount = 1250.00,
                    type = "CREDIT",
                    title = "Monthly APY Yield Capitalization",
                    merchant = "Aura Sovereign Treasury",
                    category = "Dividends & Returns",
                    status = "SETTLED",
                    requiresApproval = false,
                    timestamp = now - (day * 5),
                    referenceNumber = "CTX-66219",
                    recipientDetails = "High-Yield Reserve",
                    fee = 0.0,
                    cachedAt = now,
                    isOfflineCached = true
                )
            )
            dao.insertCachedTransactions(seedCached)
        }

        // Seed Initial Linked Bank Account Metadata (Room Persistence)
        val existingMeta = dao.getAllLinkedAccountsMetadata().firstOrNull()
        if (existingMeta.isNullOrEmpty()) {
            val now = System.currentTimeMillis()
            val seedMeta = listOf(
                LinkedAccountMetadataEntity(
                    accountId = "acc_checking",
                    institutionId = "ins_lead_private",
                    institutionName = "Aura Private Wealth",
                    accountName = "Lead Private Checking",
                    officialAccountType = "CHECKING",
                    mask = "8842",
                    fullAccountNumberMasked = "•••• •••• 8842",
                    routingNumber = "121000358",
                    wireRoutingTransit = "026009593",
                    availableBalance = 128450.75,
                    currentBalance = 128450.75,
                    currency = "USD",
                    dailyTransferLimit = 500000.0,
                    monthlyTransferLimit = 5000000.0,
                    verificationTier = "KYC_TIER_1_CERTIFIED",
                    isPrimary = true,
                    isDirectDepositActive = true,
                    linkedAtTimestamp = now - (86400000L * 30),
                    lastSyncedTimestamp = now
                ),
                LinkedAccountMetadataEntity(
                    accountId = "acc_vault",
                    institutionId = "ins_lead_vault",
                    institutionName = "Aura Custody Vaults",
                    accountName = "Lead High-Yield Reserve",
                    officialAccountType = "HIGH_YIELD_SAVINGS",
                    mask = "9381",
                    fullAccountNumberMasked = "•••• •••• 9381",
                    routingNumber = "121000358",
                    wireRoutingTransit = "026009593",
                    availableBalance = 345000.00,
                    currentBalance = 345000.00,
                    currency = "USD",
                    dailyTransferLimit = 1000000.0,
                    monthlyTransferLimit = 10000000.0,
                    verificationTier = "FDIC_SWEEP_5M_INSURED",
                    isPrimary = false,
                    isDirectDepositActive = false,
                    linkedAtTimestamp = now - (86400000L * 25),
                    lastSyncedTimestamp = now
                ),
                LinkedAccountMetadataEntity(
                    accountId = "acc_treasury",
                    institutionId = "ins_lead_treasury",
                    institutionName = "Goldman Sachs Institutional Sweep",
                    accountName = "Lead Treasury & Equities",
                    officialAccountType = "TREASURY_INVESTMENT",
                    mask = "8371",
                    fullAccountNumberMasked = "•••• •••• 8371",
                    routingNumber = "121000358",
                    wireRoutingTransit = "021000021",
                    availableBalance = 520840.50,
                    currentBalance = 520840.50,
                    currency = "USD",
                    dailyTransferLimit = 2500000.0,
                    monthlyTransferLimit = 25000000.0,
                    verificationTier = "FINRA_SIPC_PROTECTED",
                    isPrimary = false,
                    isDirectDepositActive = false,
                    linkedAtTimestamp = now - (86400000L * 14),
                    lastSyncedTimestamp = now
                )
            )
            dao.insertLinkedAccountsMetadata(seedMeta)
        }

        // Seed Initial Alert Dispatch Logs (Resend, Twilio, FCM)
        val existingAlertLogs = dao.getAllAlertLogs().firstOrNull()
        if (existingAlertLogs.isNullOrEmpty()) {
            val now = System.currentTimeMillis()
            val seedAlerts = listOf(
                AlertDispatchLogEntity(
                    id = "log_init_01",
                    channel = "EMAIL_RESEND",
                    eventType = "SECURITY_CHANGE",
                    recipient = "alexander.vance@leadwealth.com",
                    subject = "Security Advisory: Biometric Defense Shield Activated",
                    bodySummary = "Hardware secure enclave & biometric signature verification enabled.",
                    fullContent = "Delivered via Resend SMTP REST Pipeline (Status 200 OK)",
                    status = "DELIVERED",
                    externalMessageId = "resend_msg_init_01",
                    timestamp = now - 3600000L
                ),
                AlertDispatchLogEntity(
                    id = "log_init_02",
                    channel = "SMS_TWILIO",
                    eventType = "TRANSACTION_APPROVAL",
                    recipient = "+1 (415) 555-2671",
                    subject = "SMS Wire Authorization",
                    bodySummary = "[AURA BANK] Transfer Approval Required: $18,500 to Morgan Stanley. One-Time Code: 482-194.",
                    fullContent = "Delivered via Twilio Wireless Carrier Route",
                    status = "DELIVERED",
                    externalMessageId = "SM_init_02",
                    timestamp = now - 7200000L
                ),
                AlertDispatchLogEntity(
                    id = "log_init_03",
                    channel = "PUSH_FCM",
                    eventType = "DEPOSIT_NOTIFICATION",
                    recipient = "fcm_token_device_aura_client",
                    subject = "Deposit Confirmed: +$50,000.00",
                    bodySummary = "FedNow institutional wire settled into Lead Private Checking.",
                    fullContent = "Pushed via Firebase Cloud Messaging High-Priority Channel",
                    status = "DELIVERED",
                    externalMessageId = "fcm_msg_init_03",
                    timestamp = now - 86400000L
                )
            )
            seedAlerts.forEach { dao.insertAlertLog(it) }
        }
    }

    suspend fun syncAllDataToLocalRoomCache() {
        val allTxs = dao.getAllTransactions().firstOrNull() ?: emptyList()
        val allAccs = dao.getAllAccounts().firstOrNull() ?: emptyList()
        val now = System.currentTimeMillis()

        val cachedTxs = allTxs.map { tx ->
            CachedTransactionEntity(
                id = tx.id,
                accountId = "acc_checking",
                amount = tx.amount,
                type = if (tx.amount >= 0) "CREDIT" else "DEBIT",
                title = tx.title,
                merchant = tx.merchant,
                category = tx.category.displayName,
                status = tx.status.name,
                requiresApproval = tx.amount < -10000.0,
                approvalCode = if (tx.amount < -10000.0) "839-204" else null,
                riskScore = if (tx.isDisputed) 90 else if (tx.amount < -5000.0) 40 else 10,
                authChannelUsed = "MULTI_FACTOR",
                timestamp = tx.timestamp,
                referenceNumber = tx.reference,
                memo = tx.note,
                cachedAt = now,
                isOfflineCached = true
            )
        }
        dao.insertCachedTransactions(cachedTxs)

        val metadataList = allAccs.map { acc ->
            LinkedAccountMetadataEntity(
                accountId = acc.id,
                institutionId = "ins_" + acc.id,
                institutionName = "Aura Private Wealth",
                accountName = acc.name,
                officialAccountType = acc.type.name,
                mask = acc.accountNumber.takeLast(4),
                fullAccountNumberMasked = "•••• •••• " + acc.accountNumber.takeLast(4),
                routingNumber = acc.routingNumber,
                wireRoutingTransit = "026009593",
                availableBalance = acc.balance,
                currentBalance = acc.balance,
                currency = acc.currency,
                dailyTransferLimit = 250000.0,
                monthlyTransferLimit = 2500000.0,
                verificationTier = "KYC_TIER_1_CERTIFIED",
                isPrimary = acc.isPrimary,
                isDirectDepositActive = acc.isPrimary,
                linkedAtTimestamp = now,
                lastSyncedTimestamp = now
            )
        }
        dao.insertLinkedAccountsMetadata(metadataList)
    }

    suspend fun clearAlertLogs() = dao.clearAllAlertLogs()

    // --- Supabase Cloud Sync Engine (Online & Offline Support) ---

    fun toggleNetworkOnline(isOnline: Boolean) {
        _supabaseSyncState.value = _supabaseSyncState.value.copy(isOnline = isOnline)
    }

    suspend fun triggerSupabaseSync(): Int {
        val currentState = _supabaseSyncState.value
        if (!currentState.isOnline) return 0

        _supabaseSyncState.value = currentState.copy(isSyncing = true)
        delay(800) // Realistic latency simulation for Supabase REST call

        // Find all pending records
        val pendingAccounts = dao.getPendingSyncAccounts()
        val pendingTxs = dao.getPendingSyncTransactions()
        val pendingCards = dao.getPendingSyncCards()
        val pendingGoals = dao.getPendingSyncSavingsGoals()
        val pendingCryptoOrders = dao.getPendingSyncCryptoOrders()
        val pendingCryptoWallets = dao.getPendingSyncCryptoWallets()

        val totalPending = pendingAccounts.size + pendingTxs.size + pendingCards.size + pendingGoals.size + pendingCryptoOrders.size + pendingCryptoWallets.size

        val syncTime = System.currentTimeMillis()
        pendingAccounts.forEach { dao.updateAccountSyncStatus(it.id, SyncStatus.SYNCED, syncTime, it.supabaseId ?: "sup_${it.id}") }
        pendingTxs.forEach { dao.updateTransactionSyncStatus(it.id, SyncStatus.SYNCED, syncTime, it.supabaseId ?: "sup_${it.id}") }
        pendingCards.forEach { dao.updateCardSyncStatus(it.id, SyncStatus.SYNCED, syncTime, it.supabaseId ?: "sup_${it.id}") }
        pendingGoals.forEach { dao.updateSavingsGoalSyncStatus(it.id, SyncStatus.SYNCED, syncTime, it.supabaseId ?: "sup_${it.id}") }
        pendingCryptoOrders.forEach { dao.updateCryptoOrderSyncStatus(it.orderId, SyncStatus.SYNCED, syncTime) }
        pendingCryptoWallets.forEach { dao.updateCryptoWalletSyncStatus(it.id, SyncStatus.SYNCED, syncTime, it.supabaseId ?: "sup_${it.id}") }

        _supabaseSyncState.value = _supabaseSyncState.value.copy(
            isSyncing = false,
            lastSyncedTimestamp = syncTime,
            pendingSyncCount = 0
        )
        return totalPending
    }

    // --- Real-Time Cryptocurrency Swapping & Trading Engine ---

    fun getMarketDepth(symbol: String, currentPrice: Double): LiveMarketDepth {
        val bids = listOf(
            OrderBookEntry(currentPrice * 0.9995, 0.45, (currentPrice * 0.9995) * 0.45),
            OrderBookEntry(currentPrice * 0.9988, 1.20, (currentPrice * 0.9988) * 1.20),
            OrderBookEntry(currentPrice * 0.9975, 3.85, (currentPrice * 0.9975) * 3.85),
            OrderBookEntry(currentPrice * 0.9960, 5.10, (currentPrice * 0.9960) * 5.10),
            OrderBookEntry(currentPrice * 0.9940, 12.40, (currentPrice * 0.9940) * 12.40)
        )
        val asks = listOf(
            OrderBookEntry(currentPrice * 1.0005, 0.62, (currentPrice * 1.0005) * 0.62),
            OrderBookEntry(currentPrice * 1.0012, 1.84, (currentPrice * 1.0012) * 1.84),
            OrderBookEntry(currentPrice * 1.0025, 2.90, (currentPrice * 1.0025) * 2.90),
            OrderBookEntry(currentPrice * 1.0040, 7.30, (currentPrice * 1.0040) * 7.30),
            OrderBookEntry(currentPrice * 1.0060, 14.80, (currentPrice * 1.0060) * 14.80)
        )
        return LiveMarketDepth(bids, asks)
    }

    suspend fun executeCryptoSwap(
        fromSymbol: String,
        toSymbol: String,
        fromAmount: Double,
        slippageTolerancePercent: Double = 0.1,
        sourceAccountId: String = "acc_checking"
    ): Result<CryptoOrderEntity> {
        val isOnline = _supabaseSyncState.value.isOnline
        val syncStatus = if (isOnline) SyncStatus.SYNCED else SyncStatus.OFFLINE_QUEUED

        val holdings = dao.getAllCryptoHoldings().firstOrNull() ?: emptyList()
        val account = dao.getAccountById(sourceAccountId) ?: return Result.failure(Exception("Source account not found"))

        val txHash = "0x" + UUID.randomUUID().toString().replace("-", "")

        // Case 1: Swapping USD to Crypto (e.g. USD -> BTC)
        if (fromSymbol == "USD") {
            if (account.balance < fromAmount) {
                return Result.failure(Exception("Insufficient USD balance ($${"%,.2f".format(account.balance)} available)"))
            }
            val targetCrypto = holdings.find { it.symbol == toSymbol }
                ?: return Result.failure(Exception("Crypto $toSymbol not found"))

            val executionPrice = targetCrypto.currentPriceUsd
            val toAmount = fromAmount / executionPrice

            // Deduct USD from account
            dao.updateAccount(
                account.copy(
                    balance = account.balance - fromAmount,
                    syncStatus = syncStatus,
                    lastSyncedAt = System.currentTimeMillis()
                )
            )

            // Add Crypto to holding
            dao.updateCryptoHolding(
                targetCrypto.copy(
                    balance = targetCrypto.balance + toAmount,
                    syncStatus = syncStatus,
                    lastSyncedAt = System.currentTimeMillis()
                )
            )

            // Create Order
            val order = CryptoOrderEntity(
                orderId = "ord_" + UUID.randomUUID().toString().take(8),
                fromSymbol = "USD",
                toSymbol = toSymbol,
                fromAmount = fromAmount,
                toAmount = toAmount,
                executionPrice = executionPrice,
                feeUsd = 0.0,
                slippagePercent = slippageTolerancePercent,
                txHash = txHash,
                status = "CONFIRMED",
                timestamp = System.currentTimeMillis(),
                syncStatus = syncStatus
            )
            dao.insertCryptoOrder(order)

            // Inject into Banking Transaction Ledger
            val tx = TransactionEntity(
                id = "tx_" + UUID.randomUUID().toString().take(8),
                title = "Swap USD to $toSymbol",
                merchant = "Lead Institutional Crypto Desk",
                amount = -fromAmount,
                category = TransactionCategory.CRYPTO_TRADE,
                status = TransactionStatus.COMPLETED,
                timestamp = System.currentTimeMillis(),
                reference = "SWAP-" + txHash.take(10),
                note = "Acquired ${"%.4f".format(toAmount)} $toSymbol at $${"%,.2f".format(executionPrice)}/coin",
                paymentMethod = "${account.name} (USD)",
                location = "Lead Core Trading Engine",
                syncStatus = syncStatus
            )
            dao.insertTransaction(tx)

            recordImmutableAuditLog(
                operatorEmail = "SYSTEM_AUTOMATION",
                actionCategory = "FINANCIAL_TRANSACTION",
                actionType = "CRYPTO_SWAP_SETTLEMENT",
                targetEntityId = account.id,
                targetCustomer = "Client USD -> $toSymbol",
                amount = fromAmount,
                details = "Acquired ${"%.4f".format(toAmount)} $toSymbol for $$fromAmount USD at $${"%,.2f".format(executionPrice)}/coin",
                reasonCode = "DIGITAL_ASSET_EXCHANGE"
            )

            return Result.success(order)
        }

        // Case 2: Swapping Crypto to USD (e.g. BTC -> USD)
        if (toSymbol == "USD") {
            val sourceCrypto = holdings.find { it.symbol == fromSymbol }
                ?: return Result.failure(Exception("Crypto $fromSymbol not found"))

            if (sourceCrypto.balance < fromAmount) {
                return Result.failure(Exception("Insufficient $fromSymbol balance (${"%.4f".format(sourceCrypto.balance)} available)"))
            }

            val executionPrice = sourceCrypto.currentPriceUsd
            val toAmount = fromAmount * executionPrice

            // Deduct Crypto
            dao.updateCryptoHolding(
                sourceCrypto.copy(
                    balance = sourceCrypto.balance - fromAmount,
                    syncStatus = syncStatus,
                    lastSyncedAt = System.currentTimeMillis()
                )
            )

            // Credit USD to account
            dao.updateAccount(
                account.copy(
                    balance = account.balance + toAmount,
                    syncStatus = syncStatus,
                    lastSyncedAt = System.currentTimeMillis()
                )
            )

            // Create Order
            val order = CryptoOrderEntity(
                orderId = "ord_" + UUID.randomUUID().toString().take(8),
                fromSymbol = fromSymbol,
                toSymbol = "USD",
                fromAmount = fromAmount,
                toAmount = toAmount,
                executionPrice = executionPrice,
                feeUsd = 0.0,
                slippagePercent = slippageTolerancePercent,
                txHash = txHash,
                status = "CONFIRMED",
                timestamp = System.currentTimeMillis(),
                syncStatus = syncStatus
            )
            dao.insertCryptoOrder(order)

            // Inject into Banking Transaction Ledger
            val tx = TransactionEntity(
                id = "tx_" + UUID.randomUUID().toString().take(8),
                title = "Liquidate $fromSymbol to USD",
                merchant = "Lead Institutional Crypto Desk",
                amount = toAmount,
                category = TransactionCategory.CRYPTO_TRADE,
                status = TransactionStatus.COMPLETED,
                timestamp = System.currentTimeMillis(),
                reference = "LIQ-" + txHash.take(10),
                note = "Sold ${"%.4f".format(fromAmount)} $fromSymbol for $${"%,.2f".format(toAmount)} USD",
                paymentMethod = "Crypto Desk Credit",
                location = "Lead Core Trading Engine",
                syncStatus = syncStatus
            )
            dao.insertTransaction(tx)

            recordImmutableAuditLog(
                operatorEmail = "SYSTEM_AUTOMATION",
                actionCategory = "FINANCIAL_TRANSACTION",
                actionType = "CRYPTO_LIQUIDATION_SETTLEMENT",
                targetEntityId = sourceAccountId,
                targetCustomer = "Client $fromSymbol -> USD",
                amount = toAmount,
                details = "Liquidated ${"%.4f".format(fromAmount)} $fromSymbol for $${"%,.2f".format(toAmount)} USD at $${"%,.2f".format(executionPrice)}/coin",
                reasonCode = "DIGITAL_ASSET_EXCHANGE"
            )

            return Result.success(order)
        }

        // Case 3: Crypto to Crypto Swap (e.g. BTC -> ETH)
        val sourceCrypto = holdings.find { it.symbol == fromSymbol }
            ?: return Result.failure(Exception("Source crypto $fromSymbol not found"))
        val targetCrypto = holdings.find { it.symbol == toSymbol }
            ?: return Result.failure(Exception("Target crypto $toSymbol not found"))

        if (sourceCrypto.balance < fromAmount) {
            return Result.failure(Exception("Insufficient $fromSymbol balance"))
        }

        val usdValue = fromAmount * sourceCrypto.currentPriceUsd
        val toAmount = usdValue / targetCrypto.currentPriceUsd
        val executionPrice = sourceCrypto.currentPriceUsd / targetCrypto.currentPriceUsd

        dao.updateCryptoHolding(
            sourceCrypto.copy(
                balance = sourceCrypto.balance - fromAmount,
                syncStatus = syncStatus,
                lastSyncedAt = System.currentTimeMillis()
            )
        )
        dao.updateCryptoHolding(
            targetCrypto.copy(
                balance = targetCrypto.balance + toAmount,
                syncStatus = syncStatus,
                lastSyncedAt = System.currentTimeMillis()
            )
        )

        val order = CryptoOrderEntity(
            orderId = "ord_" + UUID.randomUUID().toString().take(8),
            fromSymbol = fromSymbol,
            toSymbol = toSymbol,
            fromAmount = fromAmount,
            toAmount = toAmount,
            executionPrice = executionPrice,
            feeUsd = 0.0,
            slippagePercent = slippageTolerancePercent,
            txHash = txHash,
            status = "CONFIRMED",
            timestamp = System.currentTimeMillis(),
            syncStatus = syncStatus
        )
        dao.insertCryptoOrder(order)

        return Result.success(order)
    }

    // --- Standard Banking Ledger & Transfers ---

    suspend fun executeTransfer(
        sourceAccountId: String,
        recipientName: String,
        amount: Double,
        note: String,
        rail: String
    ): Boolean {
        val account = dao.getAccountById(sourceAccountId) ?: return false
        if (account.balance < amount) return false

        val syncStatus = if (_supabaseSyncState.value.isOnline) SyncStatus.SYNCED else SyncStatus.OFFLINE_QUEUED

        val updatedAccount = account.copy(
            balance = account.balance - amount,
            syncStatus = syncStatus,
            lastSyncedAt = System.currentTimeMillis()
        )
        dao.updateAccount(updatedAccount)

        val tx = TransactionEntity(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            title = "Transfer to $recipientName",
            merchant = recipientName,
            amount = -amount,
            category = TransactionCategory.TRANSFER,
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis(),
            reference = "XFER-" + System.currentTimeMillis().toString().takeLast(8),
            note = note.ifBlank { "Private Dispatched Liquidity via $rail" },
            paymentMethod = "${account.name} ($rail)",
            location = "Lead Core Banking",
            syncStatus = syncStatus
        )
        dao.insertTransaction(tx)

        recordImmutableAuditLog(
            operatorEmail = "CLIENT_OR_SYSTEM",
            actionCategory = "FINANCIAL_TRANSACTION",
            actionType = "CUSTOMER_TRANSFER_OUT",
            targetEntityId = sourceAccountId,
            targetCustomer = recipientName,
            amount = amount,
            details = "Transferred $${"%,.2f".format(amount)} to $recipientName via $rail. Note: $note",
            reasonCode = "CUSTOMER_DIRECT_TRANSFER"
        )
        return true
    }

    suspend fun executeWithdrawal(
        sourceAccountId: String,
        destinationName: String,
        amount: Double,
        method: String,
        referenceNote: String
    ): Boolean {
        val account = dao.getAccountById(sourceAccountId) ?: return false
        if (account.balance < amount) return false

        val syncStatus = if (_supabaseSyncState.value.isOnline) SyncStatus.SYNCED else SyncStatus.OFFLINE_QUEUED

        val updatedAccount = account.copy(
            balance = account.balance - amount,
            syncStatus = syncStatus,
            lastSyncedAt = System.currentTimeMillis()
        )
        dao.updateAccount(updatedAccount)

        val tx = TransactionEntity(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            title = "Withdrawal - $destinationName",
            merchant = destinationName,
            amount = -amount,
            category = TransactionCategory.TRANSFER,
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis(),
            reference = "WTH-" + System.currentTimeMillis().toString().takeLast(8),
            note = referenceNote.ifBlank { "Direct withdrawal clearing via $method" },
            paymentMethod = "${account.name} ($method)",
            location = "Lead Core Vault",
            syncStatus = syncStatus
        )
        dao.insertTransaction(tx)

        recordImmutableAuditLog(
            operatorEmail = "CLIENT_OR_SYSTEM",
            actionCategory = "FINANCIAL_TRANSACTION",
            actionType = "CUSTOMER_WITHDRAWAL",
            targetEntityId = sourceAccountId,
            targetCustomer = destinationName,
            amount = amount,
            details = "Withdrawal of $${"%,.2f".format(amount)} to $destinationName via $method. Note: $referenceNote",
            reasonCode = "CUSTOMER_WITHDRAWAL_REQUEST"
        )
        return true
    }

    suspend fun executeBillPayment(
        sourceAccountId: String,
        billerName: String,
        billCategory: String,
        amount: Double,
        accountNumber: String,
        memo: String
    ): Boolean {
        val account = dao.getAccountById(sourceAccountId) ?: return false
        if (account.balance < amount) return false

        val syncStatus = if (_supabaseSyncState.value.isOnline) SyncStatus.SYNCED else SyncStatus.OFFLINE_QUEUED

        val updatedAccount = account.copy(
            balance = account.balance - amount,
            syncStatus = syncStatus,
            lastSyncedAt = System.currentTimeMillis()
        )
        dao.updateAccount(updatedAccount)

        val tx = TransactionEntity(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            title = "Bill Pay - $billerName",
            merchant = billerName,
            amount = -amount,
            category = TransactionCategory.UTILITIES,
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis(),
            reference = "BILL-" + System.currentTimeMillis().toString().takeLast(8),
            note = "Acct #$accountNumber • $billCategory: $memo",
            paymentMethod = "${account.name} (Direct BillPay)",
            location = "FedNow Bill Settlement",
            syncStatus = syncStatus
        )
        dao.insertTransaction(tx)

        recordImmutableAuditLog(
            operatorEmail = "CLIENT_OR_SYSTEM",
            actionCategory = "FINANCIAL_TRANSACTION",
            actionType = "CUSTOMER_BILL_PAYMENT",
            targetEntityId = sourceAccountId,
            targetCustomer = billerName,
            amount = amount,
            details = "Bill paid to $billerName for $${"%,.2f".format(amount)}. Acct #$accountNumber",
            reasonCode = "CUSTOMER_BILL_SETTLEMENT"
        )
        return true
    }

    suspend fun depositFunds(accountId: String, amount: Double, sourceName: String) {
        val account = dao.getAccountById(accountId) ?: return
        val syncStatus = if (_supabaseSyncState.value.isOnline) SyncStatus.SYNCED else SyncStatus.OFFLINE_QUEUED

        val updatedAccount = account.copy(
            balance = account.balance + amount,
            syncStatus = syncStatus,
            lastSyncedAt = System.currentTimeMillis()
        )
        dao.updateAccount(updatedAccount)

        val tx = TransactionEntity(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            title = "Deposit from $sourceName",
            merchant = sourceName,
            amount = amount,
            category = TransactionCategory.TRANSFER,
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis(),
            reference = "DEP-" + System.currentTimeMillis().toString().takeLast(8),
            note = "Funds credited to ${account.name}",
            paymentMethod = "ACH Inflow",
            location = "Direct Deposit",
            syncStatus = syncStatus
        )
        dao.insertTransaction(tx)

        recordImmutableAuditLog(
            operatorEmail = "SYSTEM_AUTOMATION",
            actionCategory = "FINANCIAL_TRANSACTION",
            actionType = "ACCOUNT_DEPOSIT_INFLOW",
            targetEntityId = accountId,
            targetCustomer = "${account.name} (•• ${account.accountNumber.takeLast(4)})",
            amount = amount,
            details = "Direct deposit credited: $${"%,.2f".format(amount)} from $sourceName",
            reasonCode = "ACH_DEPOSIT_SETTLEMENT"
        )
    }

    suspend fun chargeCardTransaction(
        cardId: String,
        merchant: String,
        amount: Double,
        category: TransactionCategory,
        memo: String
    ): Boolean {
        val cards = dao.getAllCards().firstOrNull() ?: return false
        val card = cards.find { it.id == cardId } ?: return false

        if (card.isFrozen) return false
        if (card.currentSpent + amount > card.spendingLimit) return false

        val syncStatus = if (_supabaseSyncState.value.isOnline) SyncStatus.SYNCED else SyncStatus.OFFLINE_QUEUED

        // Update card spent
        val updatedCard = card.copy(
            currentSpent = card.currentSpent + amount,
            syncStatus = syncStatus,
            lastSyncedAt = System.currentTimeMillis()
        )
        dao.updateCard(updatedCard)

        // Deduct from linked account if available
        val account = dao.getAccountById(card.linkedAccountId)
        if (account != null && account.balance >= amount) {
            dao.updateAccount(
                account.copy(
                    balance = account.balance - amount,
                    syncStatus = syncStatus,
                    lastSyncedAt = System.currentTimeMillis()
                )
            )
        }

        // Record official transaction
        val tx = TransactionEntity(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            title = merchant,
            merchant = merchant,
            amount = -amount,
            category = category,
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis(),
            reference = "AUTH-" + System.currentTimeMillis().toString().takeLast(8),
            note = memo.ifBlank { "Official Card Authorization" },
            paymentMethod = "${card.bankName} •• ${card.cardNumber.takeLast(4)}",
            location = "POS Terminal / Online",
            syncStatus = syncStatus
        )
        dao.insertTransaction(tx)

        recordImmutableAuditLog(
            operatorEmail = "SYSTEM_AUTOMATION",
            actionCategory = "FINANCIAL_TRANSACTION",
            actionType = "CARD_CHARGE_SETTLEMENT",
            targetEntityId = cardId,
            targetCustomer = merchant,
            amount = amount,
            details = "Authorized card payment: $${"%,.2f".format(amount)} at $merchant ($category)",
            reasonCode = "POS_SETTLEMENT"
        )
        return true
    }

    suspend fun toggleCardFreeze(cardId: String, isFrozen: Boolean) {
        dao.updateCardFrozenState(cardId, isFrozen)
        recordImmutableAuditLog(
            operatorEmail = "CLIENT_OR_ADMIN",
            actionCategory = "CARD_SECURITY",
            actionType = if (isFrozen) "CARD_FROZEN" else "CARD_UNFROZEN",
            targetEntityId = cardId,
            targetCustomer = "Card #$cardId",
            amount = null,
            details = "Card freeze state switched to $isFrozen",
            reasonCode = "SECURITY_CARD_LOCK"
        )
    }

    suspend fun updateCardLimit(cardId: String, limit: Double) {
        dao.updateCardLimit(cardId, limit)
        recordImmutableAuditLog(
            operatorEmail = "CLIENT_OR_ADMIN",
            actionCategory = "CARD_SECURITY",
            actionType = "CARD_LIMIT_MODIFIED",
            targetEntityId = cardId,
            targetCustomer = "Card #$cardId",
            amount = limit,
            details = "Card monthly spend limit adjusted to $${"%,.0f".format(limit)}",
            reasonCode = "LIMIT_MANAGEMENT"
        )
    }

    suspend fun linkOfficialBankCard(
        bankName: String,
        productName: String,
        cardholderName: String,
        cardNumber: String,
        expiryDate: String,
        cvv: String,
        theme: CardTheme,
        network: CardNetwork,
        spendingLimit: Double,
        linkedAccountId: String
    ): CardEntity {
        val syncStatus = if (_supabaseSyncState.value.isOnline) SyncStatus.SYNCED else SyncStatus.OFFLINE_QUEUED
        val card = CardEntity(
            id = "card_" + UUID.randomUUID().toString().take(8),
            bankName = bankName,
            cardProductName = productName,
            cardholderName = cardholderName.uppercase(),
            cardNumber = cardNumber,
            expiryDate = expiryDate,
            cvv = cvv,
            theme = theme,
            tier = CardTier.METAL,
            network = network,
            isFrozen = false,
            spendingLimit = spendingLimit,
            currentSpent = 0.0,
            contactlessEnabled = true,
            onlinePurchasesEnabled = true,
            linkedAccountId = linkedAccountId,
            isOfficialLinkedBankCard = true,
            syncStatus = syncStatus
        )
        dao.insertCard(card)

        val tx = TransactionEntity(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            title = "Official Card Linked: $bankName",
            merchant = bankName,
            amount = 0.0,
            category = TransactionCategory.TRANSFER,
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis(),
            reference = "LINK-" + System.currentTimeMillis().toString().takeLast(8),
            note = "Verified $productName tokenization via Open Banking OAuth",
            paymentMethod = "$bankName •• ${cardNumber.takeLast(4)}",
            location = "Lead Core Banking",
            syncStatus = syncStatus
        )
        dao.insertTransaction(tx)

        return card
    }

    suspend fun createVirtualCard(
        bankName: String,
        productName: String,
        theme: CardTheme,
        network: CardNetwork,
        limit: Double,
        linkedAccountId: String
    ): CardEntity {
        val last4 = (1000..9999).random().toString()
        val randomCvv = (100..999).random().toString()
        val syncStatus = if (_supabaseSyncState.value.isOnline) SyncStatus.SYNCED else SyncStatus.OFFLINE_QUEUED
        val card = CardEntity(
            id = "card_" + UUID.randomUUID().toString().take(8),
            bankName = bankName,
            cardProductName = productName,
            cardholderName = "ALEXANDER VANCE",
            cardNumber = "491283020042$last4",
            expiryDate = "09/31",
            cvv = randomCvv,
            theme = theme,
            tier = CardTier.VIRTUAL,
            network = network,
            isFrozen = false,
            spendingLimit = limit,
            currentSpent = 0.0,
            contactlessEnabled = true,
            onlinePurchasesEnabled = true,
            linkedAccountId = linkedAccountId,
            isOfficialLinkedBankCard = false,
            syncStatus = syncStatus
        )
        dao.insertCard(card)
        return card
    }

    suspend fun createSavingsGoal(title: String, targetAmount: Double, initialDeposit: Double) {
        val syncStatus = if (_supabaseSyncState.value.isOnline) SyncStatus.SYNCED else SyncStatus.OFFLINE_QUEUED
        val goal = SavingsGoalEntity(
            id = "goal_" + UUID.randomUUID().toString().take(8),
            title = title,
            targetAmount = targetAmount,
            currentAmount = initialDeposit,
            apy = 5.40,
            targetDate = "Dec 2027",
            autoRoundUp = true,
            iconName = "Vault",
            syncStatus = syncStatus
        )
        dao.insertSavingsGoal(goal)
    }

    suspend fun depositToSavingsGoal(goalId: String, amount: Double) {
        val goals = dao.getAllSavingsGoals().firstOrNull() ?: return
        val goal = goals.find { it.id == goalId } ?: return
        dao.updateSavingsGoal(goal.copy(currentAmount = goal.currentAmount + amount))
    }

    suspend fun disputeTransaction(transactionId: String, isDisputed: Boolean) {
        dao.updateDisputeStatus(transactionId, isDisputed)
    }

    suspend fun adminAdjustAccountBalance(
        accountId: String,
        amount: Double,
        isCredit: Boolean,
        reasonCode: String,
        auditMemo: String,
        operatorId: String = "Igwenababa@gmail.com"
    ): Boolean {
        val account = dao.getAccountById(accountId) ?: return false
        val newBalance = if (isCredit) account.balance + amount else (account.balance - amount).coerceAtLeast(0.0)
        dao.updateAccount(account.copy(balance = newBalance, lastSyncedAt = System.currentTimeMillis()))

        val txId = "tx_adm_" + UUID.randomUUID().toString().take(8)
        val transaction = TransactionEntity(
            id = txId,
            title = if (isCredit) "Back-Office Credit: $reasonCode" else "Back-Office Debit: $reasonCode",
            merchant = "Lead Bank Central Back-Office Ops",
            amount = if (isCredit) amount else -amount,
            category = TransactionCategory.TRANSFER,
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis(),
            reference = "ADM-${reasonCode}-${System.currentTimeMillis().toString().takeLast(6)}",
            note = "$auditMemo | Operator: $operatorId",
            paymentMethod = "Central Bank Ledger Adjustment",
            location = "New York Fed / RTGS Gateway",
            syncStatus = SyncStatus.SYNCED
        )
        dao.insertTransaction(transaction)

        recordImmutableAuditLog(
            operatorEmail = operatorId,
            actionCategory = "FINANCIAL_LEDGER",
            actionType = if (isCredit) "CREDIT_INJECTION" else "DEBIT_ADJUSTMENT",
            targetEntityId = accountId,
            targetCustomer = "${account.name} (•• ${account.accountNumber.takeLast(4)})",
            amount = amount,
            details = "$reasonCode: $auditMemo",
            reasonCode = reasonCode
        )
        return true
    }

    suspend fun adminInjectWireTransfer(
        accountId: String,
        senderName: String,
        senderBank: String,
        routingNumber: String,
        amount: Double,
        wireType: String,
        memo: String,
        operatorId: String = "Igwenababa@gmail.com"
    ): TransactionEntity? {
        val account = dao.getAccountById(accountId) ?: return null
        val newBalance = account.balance + amount
        dao.updateAccount(account.copy(balance = newBalance, lastSyncedAt = System.currentTimeMillis()))

        val txId = "tx_wire_" + UUID.randomUUID().toString().take(8)
        val ref = "WIRE-${wireType.take(4).uppercase()}-${UUID.randomUUID().toString().take(6).uppercase()}"
        val transaction = TransactionEntity(
            id = txId,
            title = "Inbound Wire: $senderName",
            merchant = senderBank,
            amount = amount,
            category = TransactionCategory.TRANSFER,
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis(),
            reference = ref,
            note = "Direct $wireType via Fedwire / Central Clearing | Routing: $routingNumber | Memo: $memo",
            paymentMethod = "$wireType Settlement Gateway",
            location = senderBank,
            syncStatus = SyncStatus.SYNCED
        )
        dao.insertTransaction(transaction)

        recordImmutableAuditLog(
            operatorEmail = operatorId,
            actionCategory = "CENTRAL_WIRE",
            actionType = "WIRE_INJECTION",
            targetEntityId = accountId,
            targetCustomer = "${account.name} (•• ${account.accountNumber.takeLast(4)})",
            amount = amount,
            details = "$wireType inbound wire from $senderBank ($senderName). Ref: $ref | Memo: $memo",
            reasonCode = "RTGS_INBOUND_SETTLEMENT"
        )
        return transaction
    }

    suspend fun adminResolveDispute(
        txId: String,
        approveRefund: Boolean,
        targetAccountId: String?,
        resolutionNote: String,
        operatorId: String = "Igwenababa@gmail.com"
    ): Boolean {
        dao.updateDisputeStatus(txId, false)
        var refundAmount = 0.0
        if (approveRefund) {
            val transactions = dao.getAllTransactions().firstOrNull() ?: emptyList()
            val tx = transactions.find { it.id == txId }
            refundAmount = if (tx != null) kotlin.math.abs(tx.amount) else 0.0
            val targetAccount = if (targetAccountId != null) dao.getAccountById(targetAccountId) else dao.getAllAccounts().firstOrNull()?.firstOrNull()
            if (targetAccount != null && refundAmount > 0.0) {
                dao.updateAccount(targetAccount.copy(balance = targetAccount.balance + refundAmount))
                val refundTx = TransactionEntity(
                    id = "tx_refund_" + UUID.randomUUID().toString().take(8),
                    title = "Dispute Settled - Full Refund",
                    merchant = tx?.merchant ?: "Lead Customer Care Resolution",
                    amount = refundAmount,
                    category = TransactionCategory.TRANSFER,
                    status = TransactionStatus.REFUNDED,
                    timestamp = System.currentTimeMillis(),
                    reference = "REFUND-${txId.takeLast(6).uppercase()}",
                    note = "Case closed by $operatorId. $resolutionNote",
                    paymentMethod = "Provisional Credit Reversal to Checking",
                    location = "Central Back-Office Customer Care"
                )
                dao.insertTransaction(refundTx)
            }
        }

        recordImmutableAuditLog(
            operatorEmail = operatorId,
            actionCategory = "COMPLIANCE_DISPUTE",
            actionType = if (approveRefund) "DISPUTE_REFUND_APPROVED" else "DISPUTE_CLAIM_REJECTED",
            targetEntityId = txId,
            targetCustomer = "Dispute Case #${txId.takeLast(6).uppercase()}",
            amount = if (approveRefund) refundAmount else null,
            details = "Dispute outcome: ${if (approveRefund) "Full refund approved & credited ($$refundAmount)" else "Claim rejected"}. Note: $resolutionNote",
            reasonCode = "ARBITRATION_FINAL_ORDER"
        )
        return true
    }

    suspend fun adminOverrideCard(
        cardId: String,
        isFrozen: Boolean,
        limit: Double,
        operatorId: String = "Igwenababa@gmail.com"
    ) {
        val cards = dao.getAllCards().firstOrNull() ?: return
        val card = cards.find { it.id == cardId } ?: return
        dao.updateCard(card.copy(isFrozen = isFrozen, spendingLimit = limit))

        recordImmutableAuditLog(
            operatorEmail = operatorId,
            actionCategory = "CARD_SECURITY",
            actionType = "CARD_CONTROL_OVERRIDE",
            targetEntityId = cardId,
            targetCustomer = "${card.bankName} ${card.cardProductName} (•• ${card.cardNumber.takeLast(4)})",
            amount = limit,
            details = "Administrative card override. Freeze=${isFrozen}, Limit=$${"%,.0f".format(limit)}",
            reasonCode = "BACK_OFFICE_SECURITY_ORDER"
        )
    }

    suspend fun adminDeleteTransaction(txId: String, operatorId: String = "Igwenababa@gmail.com") {
        dao.deleteTransaction(txId)
        recordImmutableAuditLog(
            operatorEmail = operatorId,
            actionCategory = "FINANCIAL_LEDGER",
            actionType = "TRANSACTION_EXPUNGED",
            targetEntityId = txId,
            targetCustomer = "Transaction #$txId",
            amount = null,
            details = "Transaction record expunged by executive administrative order.",
            reasonCode = "COMPLIANCE_EXPUNGEMENT"
        )
    }

    suspend fun recordImmutableAuditLog(
        operatorEmail: String = "Igwenababa@gmail.com",
        actionCategory: String,
        actionType: String,
        targetEntityId: String,
        targetCustomer: String,
        amount: Double?,
        details: String,
        reasonCode: String,
        ipAddress: String = "10.240.12.8 [FED-VPN-RTGS]"
    ): AdminAuditLogEntity {
        val latest = dao.getLatestAuditLog()
        val nextSeq = (latest?.sequenceNumber ?: 0L) + 1L
        val prevHash = latest?.cryptographicHash ?: "0000000000000000000000000000000000000000000000000000000000000000"
        val timestamp = System.currentTimeMillis()
        val hash = CryptoAuditHasher.calculateHash(
            sequence = nextSeq,
            timestamp = timestamp,
            operator = operatorEmail,
            action = actionType,
            target = targetEntityId,
            amount = amount,
            details = details,
            prevHash = prevHash
        )
        val log = AdminAuditLogEntity(
            id = "audit_" + UUID.randomUUID().toString().take(10),
            sequenceNumber = nextSeq,
            timestamp = timestamp,
            operatorEmail = operatorEmail,
            actionCategory = actionCategory,
            actionType = actionType,
            targetEntityId = targetEntityId,
            targetCustomer = targetCustomer,
            amount = amount,
            details = details,
            reasonCode = reasonCode,
            ipAddress = ipAddress,
            prevHash = prevHash,
            cryptographicHash = hash
        )
        dao.insertAuditLog(log)
        return log
    }

    suspend fun dispatchCustomerEmail(
        recipientEmail: String,
        recipientName: String,
        subject: String,
        templateType: String,
        headline: String,
        bodyText: String,
        amount: Double?,
        operatorEmail: String = "Igwenababa@gmail.com"
    ): CustomerEmailEntity {
        val ref = "NOTIF-EML-" + UUID.randomUUID().toString().take(8).uppercase()
        val email = CustomerEmailEntity(
            id = "eml_" + UUID.randomUUID().toString().take(8),
            recipientEmail = recipientEmail,
            recipientName = recipientName,
            subject = subject,
            templateType = templateType,
            headline = headline,
            bodyText = bodyText,
            amount = amount,
            referenceCode = ref,
            dispatchedBy = operatorEmail,
            timestamp = System.currentTimeMillis(),
            deliveryStatus = "DELIVERED_SECURE"
        )
        dao.insertCustomerEmail(email)
        recordImmutableAuditLog(
            operatorEmail = operatorEmail,
            actionCategory = "CUSTOMER_COMMUNICATION",
            actionType = "EMAIL_DISPATCH",
            targetEntityId = email.id,
            targetCustomer = "$recipientName <$recipientEmail>",
            amount = amount,
            details = "Dispatched official email notice: '$subject'. Template: $templateType. Ref: $ref",
            reasonCode = "REGULATORY_CUSTOMER_ADVISORY"
        )
        return email
    }

    suspend fun adminUpdateAccountConfig(
        accountId: String,
        name: String,
        apy: Double,
        routingNumber: String,
        operatorEmail: String = "Igwenababa@gmail.com"
    ): Boolean {
        val account = dao.getAccountById(accountId) ?: return false
        val updated = account.copy(name = name, apy = apy, routingNumber = routingNumber, lastSyncedAt = System.currentTimeMillis())
        dao.updateAccount(updated)
        recordImmutableAuditLog(
            operatorEmail = operatorEmail,
            actionCategory = "CONFIGURATION_EDIT",
            actionType = "ACCOUNT_CONFIG_UPDATED",
            targetEntityId = accountId,
            targetCustomer = "Account: ${account.name} (•• ${account.accountNumber.takeLast(4)})",
            amount = null,
            details = "Updated account config: Name='$name', APY=$apy%, Routing='$routingNumber'",
            reasonCode = "TREASURY_POLICY_UPDATE"
        )
        return true
    }

    // --- CRYPTO WALLETS & P2P PROTOCOL (Local Room Database Persistence) ---
    suspend fun generateNewCryptoWallet(name: String, network: BlockchainNetwork, publicAddress: String): CryptoWalletAccount {
        val isOnline = _supabaseSyncState.value.isOnline
        val syncStatus = if (isOnline) SyncStatus.SYNCED else SyncStatus.OFFLINE_QUEUED
        val newWallet = CryptoWalletAccount(
            id = "wal_" + UUID.randomUUID().toString().take(8),
            name = name,
            network = network,
            publicAddress = publicAddress,
            balanceUsd = 0.0,
            nativeBalance = 0.0,
            isConnectedExternal = false,
            providerName = "Lead Sovereign Enclave",
            createdAtFormatted = "Oct 2026",
            status = "ACTIVE • SECURE HSM ENCLAVE",
            syncStatus = syncStatus,
            lastSyncedAt = System.currentTimeMillis()
        )
        dao.insertCryptoWallet(newWallet)
        return newWallet
    }

    suspend fun connectExternalWallet(wallet: CryptoWalletAccount) {
        val isOnline = _supabaseSyncState.value.isOnline
        val syncStatus = if (isOnline) SyncStatus.SYNCED else SyncStatus.OFFLINE_QUEUED
        dao.insertCryptoWallet(wallet.copy(syncStatus = syncStatus, lastSyncedAt = System.currentTimeMillis()))
    }

    suspend fun disconnectCryptoWallet(walletId: String) {
        dao.deleteCryptoWallet(walletId)
    }

    suspend fun executeP2PCryptoSwap(
        fromSymbol: String,
        toSymbol: String,
        amount: Double,
        counterpartyAddress: String
    ): CryptoOrderEntity {
        val rate = 67420.0
        val toAmount = if (fromSymbol == "BTC") amount * rate else amount / rate
        val order = CryptoOrderEntity(
            orderId = "p2p_" + UUID.randomUUID().toString().take(8),
            fromSymbol = fromSymbol,
            toSymbol = toSymbol,
            fromAmount = amount,
            toAmount = toAmount,
            executionPrice = rate,
            feeUsd = 3.12,
            slippagePercent = 0.05,
            txHash = "0x" + UUID.randomUUID().toString().replace("-", ""),
            status = "CONFIRMED • PEER-TO-PEER",
            timestamp = System.currentTimeMillis()
        )
        dao.insertCryptoOrder(order)
        return order
    }

    // --- CARDS CENTER MANAGEMENT & $299.99 UPFRONT FEE ---
    suspend fun deleteCard(cardId: String) {
        dao.deleteCard(cardId)
    }

    suspend fun replaceCard(cardId: String): CardEntity? {
        val cards = dao.getAllCards().firstOrNull() ?: return null
        val existing = cards.find { it.id == cardId } ?: return null
        val last4 = (1000..9999).random().toString()
        val newCard = existing.copy(
            id = "card_" + UUID.randomUUID().toString().take(8),
            cardNumber = existing.cardNumber.take(12) + last4,
            cvv = (100..999).random().toString(),
            expiryDate = "10/31",
            currentSpent = 0.0
        )
        dao.deleteCard(cardId)
        dao.insertCard(newCard)
        return newCard
    }

    suspend fun payCardBalance(cardId: String, amount: Double, sourceAccountId: String): Boolean {
        val accounts = dao.getAllAccounts().firstOrNull() ?: return false
        val sourceAcc = accounts.find { it.id == sourceAccountId } ?: return false
        if (sourceAcc.balance < amount) return false

        val cards = dao.getAllCards().firstOrNull() ?: return false
        val card = cards.find { it.id == cardId } ?: return false

        dao.updateAccount(sourceAcc.copy(balance = sourceAcc.balance - amount))
        dao.updateCard(card.copy(currentSpent = (card.currentSpent - amount).coerceAtLeast(0.0)))

        val tx = TransactionEntity(
            id = "tx_pay_" + UUID.randomUUID().toString().take(8),
            title = "Credit Card Balance Settlement",
            merchant = "${card.bankName} Card Services",
            amount = -amount,
            category = TransactionCategory.TRANSFER,
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis(),
            reference = "PAY-CRD-" + (10000..99999).random(),
            note = "Payment toward card •• ${card.cardNumber.takeLast(4)} from ${sourceAcc.name}",
            paymentMethod = sourceAcc.name,
            location = "Lead Payment Operations",
            syncStatus = SyncStatus.SYNCED
        )
        dao.insertTransaction(tx)
        return true
    }

    suspend fun applyForCardWithUpfrontFee(
        productName: String,
        theme: CardTheme,
        tier: CardTier,
        network: CardNetwork,
        spendingLimit: Double,
        linkedAccountId: String,
        paymentMethod: String,
        deliveryAddress: String
    ): CardEntity {
        // If payment method is account balance, debit $299.99
        if (paymentMethod == "ACCOUNT_BALANCE") {
            val accounts = dao.getAllAccounts().firstOrNull()
            val account = accounts?.find { it.id == linkedAccountId }
            if (account != null && account.balance >= 299.99) {
                dao.updateAccount(account.copy(balance = account.balance - 299.99))
            }
        }

        val feeTx = TransactionEntity(
            id = "tx_fee_" + UUID.randomUUID().toString().take(8),
            title = "Card Issuance Fee • $productName",
            merchant = "Lead Card Issuing Operations",
            amount = -299.99,
            category = TransactionCategory.UTILITIES,
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis(),
            reference = "FEE-299-" + (10000..99999).random(),
            note = "Upfront $299.99 Issuance Fee ($150 Fabrication/Tokenization, $99.99 KYC AML, $50 Concierge)",
            paymentMethod = paymentMethod,
            location = "Lead Issuance Desk",
            syncStatus = SyncStatus.SYNCED
        )
        dao.insertTransaction(feeTx)

        val last4 = (1000..9999).random().toString()
        val newCard = CardEntity(
            id = "card_" + UUID.randomUUID().toString().take(8),
            bankName = "Lead Private Bank",
            cardProductName = productName,
            cardholderName = "ALEXANDER VANCE",
            cardNumber = "453289014418$last4",
            expiryDate = "10/31",
            cvv = (100..999).random().toString(),
            theme = theme,
            tier = tier,
            network = network,
            isFrozen = false,
            spendingLimit = spendingLimit,
            currentSpent = 0.0,
            contactlessEnabled = true,
            onlinePurchasesEnabled = true,
            linkedAccountId = linkedAccountId,
            isOfficialLinkedBankCard = false,
            deliveryStatus = if (tier == CardTier.METAL) PhysicalDeliveryStatus.CARD_SHIPPED else PhysicalDeliveryStatus.DELIVERED,
            trackingNumber = "LD-88492019-US",
            syncStatus = SyncStatus.SYNCED
        )
        dao.insertCard(newCard)
        return newCard
    }
}
