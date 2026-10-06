package com.example.services

import android.content.Context
import android.util.Log
import com.example.data.local.BankingDao
import com.example.data.model.AlertDispatchLogEntity
import com.example.data.model.CachedTransactionEntity
import com.example.data.model.LinkedAccountMetadataEntity
import com.example.services.resend.ResendClient
import com.example.services.twilio.TwilioClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class AlertDispatchResult(
    val eventType: String,
    val resendSuccess: Boolean,
    val resendDetails: String,
    val twilioSuccess: Boolean,
    val twilioDetails: String,
    val fcmSuccess: Boolean,
    val fcmDetails: String,
    val timestamp: Long = System.currentTimeMillis()
)

class AlertNotificationManager(
    private val bankingDao: BankingDao,
    private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _alertEvents = MutableSharedFlow<AlertDispatchResult>(replay = 1)
    val alertEvents = _alertEvents.asSharedFlow()

    init {
        AuraFirebaseMessagingService.createNotificationChannel(context)
    }

    /**
     * 1. TRANSACTION APPROVAL ALERT:
     * Dispatches multi-factor approval request across Resend (HTML Email), Twilio (SMS with OTP), and FCM Push.
     * Caches transaction locally in Room database.
     */
    suspend fun sendTransactionApproval(
        amount: Double,
        recipient: String,
        accountName: String,
        accountId: String,
        approvalCode: String = generateOtpCode(),
        clientEmail: String = "client@auraprivatewealth.com",
        clientPhone: String = "+14155552671"
    ): AlertDispatchResult {
        val reference = "TX-" + UUID.randomUUID().toString().take(8).uppercase()
        val now = System.currentTimeMillis()

        // 1. Resend Email Dispatch
        val emailSubject = "Approval Required: $$amount Wire to $recipient"
        val emailHtml = ResendClient.buildTransactionApprovalEmail(
            amount = amount,
            recipient = recipient,
            approvalCode = approvalCode,
            accountName = accountName,
            reference = reference
        )
        val resendResult = ResendClient.sendEmail(
            to = clientEmail,
            subject = emailSubject,
            htmlBody = emailHtml
        )

        // 2. Twilio SMS Dispatch
        val smsBody = TwilioClient.buildTransactionApprovalSms(
            amount = amount,
            recipient = recipient,
            otpCode = approvalCode
        )
        val twilioResult = TwilioClient.sendSms(
            to = clientPhone,
            body = smsBody
        )

        // 3. Firebase Cloud Messaging (Push Notification)
        val fcmTitle = "⚠️ Wire Authorization: $$amount"
        val fcmBody = "Outbound wire to $recipient requires approval. Code: $approvalCode."
        AuraFirebaseMessagingService.showNotification(
            context = context,
            title = fcmTitle,
            body = fcmBody,
            eventType = "TRANSACTION_APPROVAL",
            referenceId = reference
        )

        // 4. Cache Transaction in Room Database
        val cachedTx = CachedTransactionEntity(
            id = reference,
            accountId = accountId,
            amount = -amount,
            type = "TRANSFER",
            title = "Wire to $recipient",
            merchant = recipient,
            category = "Wire & Transfers",
            status = "PENDING_APPROVAL",
            requiresApproval = true,
            approvalCode = approvalCode,
            riskScore = if (amount > 10000.0) 85 else 45,
            authChannelUsed = "RESEND+TWILIO+FCM",
            timestamp = now,
            referenceNumber = reference,
            recipientDetails = recipient,
            fee = 0.0,
            cachedAt = now,
            isOfflineCached = true
        )
        try {
            bankingDao.insertCachedTransaction(cachedTx)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert cached transaction into Room", e)
        }

        // 5. Persist Alert Logs to Room Database
        saveAlertLog("EMAIL_RESEND", "TRANSACTION_APPROVAL", clientEmail, emailSubject, "Wire approval request for $$amount", resendResult.details, resendResult.messageId)
        saveAlertLog("SMS_TWILIO", "TRANSACTION_APPROVAL", clientPhone, "SMS Wire Approval", smsBody, twilioResult.details, twilioResult.sid)
        saveAlertLog("PUSH_FCM", "TRANSACTION_APPROVAL", AuraFirebaseMessagingService.getFcmToken(context), fcmTitle, fcmBody, "Pushed to device via FCM channel", reference)

        val result = AlertDispatchResult(
            eventType = "TRANSACTION_APPROVAL",
            resendSuccess = resendResult.isSuccess,
            resendDetails = resendResult.details,
            twilioSuccess = twilioResult.isSuccess,
            twilioDetails = twilioResult.details,
            fcmSuccess = true,
            fcmDetails = "FCM Notification Posted to Android System"
        )
        _alertEvents.emit(result)
        return result
    }

    /**
     * 2. ACCOUNT SECURITY CHANGE ALERT:
     * Dispatches instant security advisory when biometrics, fraud defense, or keys are modified.
     */
    suspend fun sendSecurityChangeAlert(
        changeTitle: String,
        details: String,
        clientEmail: String = "client@auraprivatewealth.com",
        clientPhone: String = "+14155552671"
    ): AlertDispatchResult {
        val now = System.currentTimeMillis()
        val timeString = SimpleDateFormat("MMM dd, yyyy HH:mm:ss z", Locale.US).format(Date(now))

        // 1. Resend Email Dispatch
        val emailSubject = "Security Advisory: $changeTitle"
        val emailHtml = ResendClient.buildSecurityChangeEmail(
            changeTitle = changeTitle,
            details = details,
            timestamp = timeString
        )
        val resendResult = ResendClient.sendEmail(
            to = clientEmail,
            subject = emailSubject,
            htmlBody = emailHtml
        )

        // 2. Twilio SMS Dispatch
        val smsBody = TwilioClient.buildSecurityChangeSms(changeTitle, details)
        val twilioResult = TwilioClient.sendSms(
            to = clientPhone,
            body = smsBody
        )

        // 3. FCM Push
        val fcmTitle = "🛡️ Security Updated: $changeTitle"
        val fcmBody = details
        AuraFirebaseMessagingService.showNotification(
            context = context,
            title = fcmTitle,
            body = fcmBody,
            eventType = "SECURITY_CHANGE"
        )

        // 4. Room Alert Logs
        saveAlertLog("EMAIL_RESEND", "SECURITY_CHANGE", clientEmail, emailSubject, details, resendResult.details, resendResult.messageId)
        saveAlertLog("SMS_TWILIO", "SECURITY_CHANGE", clientPhone, "SMS Security Alert", smsBody, twilioResult.details, twilioResult.sid)
        saveAlertLog("PUSH_FCM", "SECURITY_CHANGE", AuraFirebaseMessagingService.getFcmToken(context), fcmTitle, fcmBody, "Pushed to device via FCM channel", null)

        val result = AlertDispatchResult(
            eventType = "SECURITY_CHANGE",
            resendSuccess = resendResult.isSuccess,
            resendDetails = resendResult.details,
            twilioSuccess = twilioResult.isSuccess,
            twilioDetails = twilioResult.details,
            fcmSuccess = true,
            fcmDetails = "FCM Notification Posted to Android System"
        )
        _alertEvents.emit(result)
        return result
    }

    /**
     * 3. DEPOSIT NOTIFICATION:
     * Dispatches instant clearance notice for FedNow / wire deposits.
     * Updates linked account metadata in Room database.
     */
    suspend fun sendDepositNotification(
        amount: Double,
        accountName: String,
        accountId: String,
        newBalance: Double,
        reference: String = "DEP-" + UUID.randomUUID().toString().take(8).uppercase(),
        clientEmail: String = "client@auraprivatewealth.com",
        clientPhone: String = "+14155552671"
    ): AlertDispatchResult {
        val now = System.currentTimeMillis()

        // 1. Resend Email Dispatch
        val emailSubject = "Funds Cleared: +$$amount USD in $accountName"
        val emailHtml = ResendClient.buildDepositNotificationEmail(
            amount = amount,
            accountName = accountName,
            reference = reference,
            newBalance = newBalance
        )
        val resendResult = ResendClient.sendEmail(
            to = clientEmail,
            subject = emailSubject,
            htmlBody = emailHtml
        )

        // 2. Twilio SMS Dispatch
        val smsBody = TwilioClient.buildDepositNotificationSms(
            amount = amount,
            accountName = accountName,
            newBalance = newBalance
        )
        val twilioResult = TwilioClient.sendSms(
            to = clientPhone,
            body = smsBody
        )

        // 3. FCM Push
        val fcmTitle = "💰 Deposit Confirmed: +$${"%,.2f".format(amount)}"
        val fcmBody = "FedNow wire settled into $accountName. New Balance: $${"%,.2f".format(newBalance)}."
        AuraFirebaseMessagingService.showNotification(
            context = context,
            title = fcmTitle,
            body = fcmBody,
            eventType = "DEPOSIT_NOTIFICATION",
            referenceId = reference
        )

        // 4. Cache Deposit Transaction in Room
        val depositTx = CachedTransactionEntity(
            id = reference,
            accountId = accountId,
            amount = amount,
            type = "CREDIT",
            title = "FedNow Wire Deposit",
            merchant = "Federal Reserve Liquidity Facility",
            category = "Income & Payroll",
            status = "SETTLED",
            requiresApproval = false,
            timestamp = now,
            referenceNumber = reference,
            recipientDetails = accountName,
            fee = 0.0,
            cachedAt = now,
            isOfflineCached = true
        )
        try {
            bankingDao.insertCachedTransaction(depositTx)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cache deposit in Room", e)
        }

        // 5. Update or Upsert Linked Account Metadata in Room
        try {
            val existingMeta = bankingDao.getLinkedAccountMetadata(accountId)
            val updatedMeta = existingMeta?.copy(
                availableBalance = newBalance,
                currentBalance = newBalance,
                lastSyncedTimestamp = now
            ) ?: LinkedAccountMetadataEntity(
                accountId = accountId,
                institutionId = "ins_lead_private",
                institutionName = "Aura Private Wealth",
                accountName = accountName,
                officialAccountType = "CHECKING",
                mask = "8842",
                fullAccountNumberMasked = "•••• •••• 8842",
                routingNumber = "121000358",
                wireRoutingTransit = "026009593",
                availableBalance = newBalance,
                currentBalance = newBalance,
                isPrimary = true,
                linkedAtTimestamp = now,
                lastSyncedTimestamp = now
            )
            bankingDao.insertLinkedAccountMetadata(updatedMeta)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update linked account metadata in Room", e)
        }

        // 6. Save Alert Logs
        saveAlertLog("EMAIL_RESEND", "DEPOSIT_NOTIFICATION", clientEmail, emailSubject, "Deposit confirmed +$$amount", resendResult.details, resendResult.messageId)
        saveAlertLog("SMS_TWILIO", "DEPOSIT_NOTIFICATION", clientPhone, "SMS Deposit Confirmation", smsBody, twilioResult.details, twilioResult.sid)
        saveAlertLog("PUSH_FCM", "DEPOSIT_NOTIFICATION", AuraFirebaseMessagingService.getFcmToken(context), fcmTitle, fcmBody, "Pushed to device via FCM channel", reference)

        val result = AlertDispatchResult(
            eventType = "DEPOSIT_NOTIFICATION",
            resendSuccess = resendResult.isSuccess,
            resendDetails = resendResult.details,
            twilioSuccess = twilioResult.isSuccess,
            twilioDetails = twilioResult.details,
            fcmSuccess = true,
            fcmDetails = "FCM Notification Posted to Android System"
        )
        _alertEvents.emit(result)
        return result
    }

    private suspend fun saveAlertLog(
        channel: String,
        eventType: String,
        recipient: String,
        subject: String,
        summary: String,
        statusDetails: String,
        externalId: String?
    ) {
        val log = AlertDispatchLogEntity(
            id = "log_${UUID.randomUUID().toString().take(10)}",
            channel = channel,
            eventType = eventType,
            recipient = recipient,
            subject = subject,
            bodySummary = summary,
            fullContent = statusDetails,
            status = if (statusDetails.contains("Sandbox")) "SIMULATED_SANDBOX" else "DELIVERED",
            externalMessageId = externalId,
            timestamp = System.currentTimeMillis()
        )
        try {
            bankingDao.insertAlertLog(log)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert alert log", e)
        }
    }

    private fun generateOtpCode(): String {
        val randomNum = (100000..999999).random()
        return "${randomNum / 1000}-${randomNum % 1000}"
    }

    companion object {
        private const val TAG = "AlertNotificationMgr"
    }
}
