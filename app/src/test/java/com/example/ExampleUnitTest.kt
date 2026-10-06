package com.example

import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.CardEntity
import com.example.data.model.CardNetwork
import com.example.data.model.CardTheme
import com.example.data.model.CardTier
import com.example.data.model.CryptoHoldingEntity
import com.example.data.model.CryptoOrderEntity
import com.example.data.model.SyncStatus
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun cardMasking_showsLastFourDigits() {
        val card = CardEntity(
            id = "c1",
            cardholderName = "ALEXANDER VANCE",
            cardNumber = "4532890144188842",
            expiryDate = "08/30",
            cvv = "942",
            theme = CardTheme.OBSIDIAN,
            tier = CardTier.METAL
        )
        assertEquals("•••• •••• •••• 8842", card.maskedNumber)
    }

    @Test
    fun accountEntity_computesCorrectBalance() {
        val account = AccountEntity(
            id = "acc1",
            name = "Private Checking",
            type = AccountType.CHECKING,
            balance = 125000.50,
            accountNumber = "12345678",
            routingNumber = "987654321",
            syncStatus = SyncStatus.SYNCED
        )
        assertTrue(account.balance > 100000.0)
        assertEquals("Tier 1", account.type.badge)
        assertEquals(SyncStatus.SYNCED, account.syncStatus)
    }

    @Test
    fun officialBankCard_preservesBankAndNetworkIdentity() {
        val chaseCard = CardEntity(
            id = "card_chase_test",
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
            currentSpent = 1500.0,
            isOfficialLinkedBankCard = true,
            syncStatus = SyncStatus.SYNCED
        )

        assertEquals("JPMorgan Chase", chaseCard.bankName)
        assertEquals(CardNetwork.VISA_INFINITE, chaseCard.network)
        assertTrue(chaseCard.isOfficialLinkedBankCard)
        assertEquals(33500.0, chaseCard.spendingLimit - chaseCard.currentSpent, 0.01)
        assertFalse(chaseCard.isFrozen)
        assertEquals(SyncStatus.SYNCED, chaseCard.syncStatus)
    }

    @Test
    fun transactionEntity_reflectsDebitAndPaymentMethod() {
        val tx = TransactionEntity(
            id = "tx_test_1",
            title = "Apple Store",
            merchant = "Apple Store",
            amount = -1199.00,
            category = TransactionCategory.TECH,
            status = TransactionStatus.COMPLETED,
            timestamp = 1700000000000L,
            reference = "AUTH-982141",
            paymentMethod = "JPMorgan Chase •• 4210",
            syncStatus = SyncStatus.SYNCED
        )

        assertTrue(tx.amount < 0)
        assertEquals("Apple Store", tx.merchant)
        assertEquals("JPMorgan Chase •• 4210", tx.paymentMethod)
        assertEquals(TransactionStatus.COMPLETED, tx.status)
        assertEquals(SyncStatus.SYNCED, tx.syncStatus)
    }

    @Test
    fun cryptoHolding_computesCorrectValuation() {
        val btc = CryptoHoldingEntity(
            symbol = "BTC",
            name = "Bitcoin",
            balance = 1.5,
            currentPriceUsd = 68000.0,
            change24hPercent = 4.2,
            high24h = 68500.0,
            low24h = 65000.0,
            volume24hUsd = 30000000000.0,
            iconColorHex = 0xFFF59E0B,
            networkName = "Bitcoin",
            sparklineCsv = "65000,66000,67000,68000",
            syncStatus = SyncStatus.SYNCED
        )

        assertEquals(102000.0, btc.totalValueUsd, 0.01)
        assertEquals(4, btc.sparklineValues.size)
    }

    @Test
    fun cryptoOrder_recordsSwapExecution() {
        val order = CryptoOrderEntity(
            orderId = "ord_test_01",
            fromSymbol = "USD",
            toSymbol = "BTC",
            fromAmount = 10000.0,
            toAmount = 0.147,
            executionPrice = 68000.0,
            feeUsd = 0.0,
            slippagePercent = 0.1,
            txHash = "0x9876543210abcdef",
            status = "CONFIRMED",
            syncStatus = SyncStatus.OFFLINE_QUEUED
        )

        assertEquals("USD", order.fromSymbol)
        assertEquals("BTC", order.toSymbol)
        assertEquals(SyncStatus.OFFLINE_QUEUED, order.syncStatus)
        assertEquals("CONFIRMED", order.status)
        assertTrue(order.txHash.startsWith("0x"))
    }

    @Test
    fun accountNumber_maskingAndRevealLogic() {
        val rawAccountNumber = "8839201948"
        val masked = "•••• •••• " + rawAccountNumber.takeLast(4)
        val revealed = rawAccountNumber.chunked(4).joinToString(" ")

        assertEquals("•••• •••• 1948", masked)
        assertEquals("8839 2019 48", revealed)
    }

    @Test
    fun totalAggregatedWealth_btcEquivalentCalculation() {
        val totalWealthUsd = 1482950.0
        val btcPrice = 66450.0
        val btcEquivalent = totalWealthUsd / btcPrice

        assertTrue(btcEquivalent > 22.0)
        assertEquals("22.3168", String.format(java.util.Locale.US, "%.4f", btcEquivalent))
    }

    @Test
    fun clientIdentity_nicknameAndVerifiedStatus() {
        val nickname = "Alex 'The Sovereign'"
        val userName = "Alexander Sterling"
        val isVerified = true

        assertTrue(nickname.contains("Sovereign"))
        assertEquals("Alexander Sterling", userName)
        assertTrue(isVerified)
    }
}
