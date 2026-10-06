package com.example.data.model

data class PayeeContact(
    val id: String,
    val name: String,
    val handle: String,
    val bank: String,
    val avatarInitials: String,
    val colorHex: Long = 0xFF3B82F6
)

enum class AlertType {
    SECURITY,
    TRANSACTION,
    WEALTH,
    SYSTEM
}

data class NotificationAlert(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: AlertType,
    val isRead: Boolean = false
)

data class PlanTierInfo(
    val id: String,
    val name: String,
    val priceMonthly: String,
    val tagline: String,
    val isPopular: Boolean,
    val features: List<String>,
    val cardMaterial: String
)

data class FaqItem(
    val question: String,
    val answer: String
)

data class TestimonialItem(
    val quote: String,
    val author: String,
    val role: String,
    val company: String
)
