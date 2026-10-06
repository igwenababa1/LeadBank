package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crypto_holdings")
data class CryptoHoldingEntity(
    @PrimaryKey val symbol: String, // BTC, ETH, SOL, AVAX, LINK, USDC
    val name: String,
    val balance: Double,
    val currentPriceUsd: Double,
    val change24hPercent: Double,
    val high24h: Double,
    val low24h: Double,
    val volume24hUsd: Double,
    val iconColorHex: Long,
    val networkName: String,
    val sparklineCsv: String = "",
    val supabaseId: String? = null,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastSyncedAt: Long = System.currentTimeMillis()
) {
    val totalValueUsd: Double get() = balance * currentPriceUsd
    val sparklineValues: List<Float>
        get() = if (sparklineCsv.isBlank()) emptyList() else sparklineCsv.split(",").mapNotNull { it.trim().toFloatOrNull() }
}

@Entity(tableName = "crypto_orders")
data class CryptoOrderEntity(
    @PrimaryKey val orderId: String,
    val fromSymbol: String, // USD, EUR, BTC, ETH
    val toSymbol: String, // BTC, ETH, USD, SOL
    val fromAmount: Double,
    val toAmount: Double,
    val executionPrice: Double,
    val feeUsd: Double = 0.0,
    val slippagePercent: Double = 0.1,
    val txHash: String,
    val status: String = "CONFIRMED",
    val timestamp: Long = System.currentTimeMillis(),
    val supabaseId: String? = null,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

data class OrderBookEntry(
    val price: Double,
    val amount: Double,
    val total: Double
)

data class LiveMarketDepth(
    val bids: List<OrderBookEntry>,
    val asks: List<OrderBookEntry>
)

data class SupabaseSyncState(
    val isOnline: Boolean = true,
    val isSyncing: Boolean = false,
    val lastSyncedTimestamp: Long = System.currentTimeMillis(),
    val pendingSyncCount: Int = 0,
    val supabaseProjectRef: String = "lb-wealth-core-prd",
    val syncEndpoint: String = "https://lb-wealth-core.supabase.co/rest/v1"
)

enum class BlockchainNetwork(
    val displayName: String,
    val symbol: String,
    val standard: String,
    val iconColor: Long,
    val explorerPrefix: String,
    val defaultAddressPrefix: String
) {
    BITCOIN("Bitcoin Mainnet", "BTC", "Native SegWit (Bech32)", 0xFFF7931A, "https://mempool.space/address/", "bc1q"),
    ETHEREUM("Ethereum", "ETH", "ERC-20 / EVM", 0xFF627EEA, "https://etherscan.io/address/", "0x"),
    SOLANA("Solana", "SOL", "SPL Native", 0xFF14F195, "https://solscan.io/account/", "Sol"),
    POLYGON("Polygon PoS", "POL", "ERC-20 zkEVM", 0xFF8247E5, "https://polygonscan.com/address/", "0x")
}

@Entity(tableName = "crypto_wallets")
data class CryptoWalletAccount(
    @PrimaryKey val id: String,
    val name: String,
    val network: BlockchainNetwork,
    val publicAddress: String,
    val balanceUsd: Double,
    val nativeBalance: Double,
    val isConnectedExternal: Boolean = false,
    val providerName: String = "Lead Sovereign Enclave", // e.g. "Coinbase Connect", "MetaMask", "Ledger Vault", "Phantom"
    val createdAtFormatted: String = "Oct 2026",
    val status: String = "ACTIVE • SECURE HSM ENCLAVE",
    val isPrimary: Boolean = false,
    val supabaseId: String? = null,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastSyncedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    val maskedAddress: String
        get() = if (publicAddress.length > 14) publicAddress.take(6) + "••••" + publicAddress.takeLast(6) else publicAddress
}
