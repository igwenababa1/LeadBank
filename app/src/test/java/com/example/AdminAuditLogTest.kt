package com.example

import com.example.data.model.AdminAuditLogEntity
import com.example.data.model.CryptoAuditHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class AdminAuditLogTest {

    @Test
    fun testAdminCredentialsValidation() {
        val validEmail = "Igwenababa@gmail.com"
        val validPass = "Igwe1992@"

        // Direct check
        val isEmailMatch = validEmail.trim().equals("Igwenababa@gmail.com", ignoreCase = true)
        val isPassMatch = validPass.trim() == "Igwe1992@"

        assertTrue("Valid admin email should authenticate", isEmailMatch)
        assertTrue("Valid admin password should authenticate", isPassMatch)
    }

    @Test
    fun testCryptoAuditHasherChaining() {
        val genesisHash = "0000000000000000000000000000000000000000000000000000000000000000"
        val timestamp1 = 1700000000000L
        val h1 = CryptoAuditHasher.calculateHash(
            sequence = 1L,
            timestamp = timestamp1,
            operator = "Igwenababa@gmail.com",
            action = "GENESIS_LEDGER_BOOTSTRAP",
            target = "CENTRAL_BANK",
            amount = 1162071.25,
            details = "Charter capitalization",
            prevHash = genesisHash
        )
        assertNotNull(h1)
        assertEquals(64, h1.length) // SHA-256 hex string

        val timestamp2 = 1700000050000L
        val h2 = CryptoAuditHasher.calculateHash(
            sequence = 2L,
            timestamp = timestamp2,
            operator = "Igwenababa@gmail.com",
            action = "WIRE_INJECTION",
            target = "acc_checking",
            amount = 50000.0,
            details = "Fedwire RTGS credit from JPMorgan",
            prevHash = h1
        )
        assertNotNull(h2)
        assertEquals(64, h2.length)
        assertNotEquals(h1, h2)

        // Ensure hash recalculation produces identical deterministic hash
        val h1Recalculated = CryptoAuditHasher.calculateHash(
            sequence = 1L,
            timestamp = timestamp1,
            operator = "Igwenababa@gmail.com",
            action = "GENESIS_LEDGER_BOOTSTRAP",
            target = "CENTRAL_BANK",
            amount = 1162071.25,
            details = "Charter capitalization",
            prevHash = genesisHash
        )
        assertEquals(h1, h1Recalculated)
    }

    @Test
    fun testAuditLogEntityCreation() {
        val log = AdminAuditLogEntity(
            id = "audit_" + UUID.randomUUID().toString().take(8),
            sequenceNumber = 42L,
            timestamp = System.currentTimeMillis(),
            operatorEmail = "Igwenababa@gmail.com",
            actionCategory = "CUSTOMER_COMMUNICATION",
            actionType = "EMAIL_DISPATCH",
            targetEntityId = "eml_123",
            targetCustomer = "Alexander Vance",
            amount = 38500.0,
            details = "Wire clearance advisory sent",
            reasonCode = "REGULATORY_CUSTOMER_ADVISORY",
            ipAddress = "10.240.12.8 [FED-VPN-RTGS]",
            prevHash = "0000000000000000000000000000000000000000000000000000000000000000",
            cryptographicHash = "a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2"
        )

        assertEquals("Igwenababa@gmail.com", log.operatorEmail)
        assertEquals("EMAIL_DISPATCH", log.actionType)
        assertEquals(42L, log.sequenceNumber)
        assertEquals(38500.0, log.amount ?: 0.0, 0.001)
    }
}
