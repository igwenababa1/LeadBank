package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.security.MessageDigest

enum class AdminTicketStatus(val label: String) {
    OPEN("Open Inquiry"),
    IN_REVIEW("In Back-Office Review"),
    RESOLVED("Resolved & Closed"),
    ESCALATED("Escalated to Compliance")
}

enum class AdminTicketPriority(val label: String) {
    CRITICAL("P1 - Critical"),
    HIGH("P2 - High Priority"),
    MEDIUM("P3 - Standard"),
    LOW("P4 - Routine")
}

data class AdminTicketNote(
    val id: String,
    val author: String,
    val timestamp: Long,
    val message: String,
    val isInternal: Boolean = true
)

data class AdminTicket(
    val id: String,
    val customerName: String,
    val accountNumber: String,
    val category: String,
    val subject: String,
    val description: String,
    val status: AdminTicketStatus,
    val priority: AdminTicketPriority,
    val createdAt: Long,
    val assignedAgent: String,
    val notes: List<AdminTicketNote> = emptyList()
)

@Entity(tableName = "immutable_audit_logs")
data class AdminAuditLogEntity(
    @PrimaryKey val id: String,
    val sequenceNumber: Long,
    val timestamp: Long,
    val operatorEmail: String,
    val actionCategory: String,
    val actionType: String,
    val targetEntityId: String,
    val targetCustomer: String,
    val amount: Double?,
    val details: String,
    val reasonCode: String,
    val ipAddress: String = "10.240.12.8 [FED-VPN-RTGS]",
    val prevHash: String,
    val cryptographicHash: String
)

@Entity(tableName = "customer_emails")
data class CustomerEmailEntity(
    @PrimaryKey val id: String,
    val recipientEmail: String,
    val recipientName: String,
    val subject: String,
    val templateType: String,
    val headline: String,
    val bodyText: String,
    val amount: Double? = null,
    val referenceCode: String,
    val dispatchedBy: String,
    val timestamp: Long,
    val deliveryStatus: String = "DELIVERED_SECURE"
)

data class SystemGatewayStatus(
    val id: String,
    val name: String,
    val protocol: String,
    val status: String,
    val latencyMs: Int,
    val uptimePct: Double,
    val dailyVolume: String
)

enum class AdminDashboardTab(val label: String) {
    ACCOUNTS("Ledgers & Accounts"),
    CONFIG("Configurations & APY"),
    WIRES("Wire Injection"),
    EMAILS("Customer Communications"),
    DISPUTES("Disputes & AML"),
    CARDS("Card Oversight"),
    SUPPORT("Customer Care"),
    TELEMETRY("Core Banking Systems"),
    AUDIT("Immutable Audit Log")
}

object CryptoAuditHasher {
    fun calculateHash(
        sequence: Long,
        timestamp: Long,
        operator: String,
        action: String,
        target: String,
        amount: Double?,
        details: String,
        prevHash: String
    ): String {
        val payload = "$sequence:$timestamp:$operator:$action:$target:${amount ?: 0.0}:$details:$prevHash"
        val bytes = MessageDigest.getInstance("SHA-256").digest(payload.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
