package com.example.services.resend

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ResendResult(
    val isSuccess: Boolean,
    val messageId: String?,
    val isSandbox: Boolean,
    val details: String
)

object ResendClient {
    private const val TAG = "ResendClient"
    private const val RESEND_API_URL = "https://api.resend.com/emails"
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return try {
            BuildConfig.RESEND_API_KEY.replace("\"", "").trim()
        } catch (_: Exception) {
            ""
        }
    }

    suspend fun sendEmail(
        to: String,
        subject: String,
        htmlBody: String,
        from: String = "Aura Private Wealth <onboarding@resend.dev>"
    ): ResendResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_RESEND_API_KEY_DEFAULT_VALUE") {
            Log.w(TAG, "Resend API Key is missing or default. Dispatching in Verified Sandbox Mode.")
            return@withContext ResendResult(
                isSuccess = true,
                messageId = "sim_resend_${System.currentTimeMillis()}",
                isSandbox = true,
                details = "Sandbox Delivered (Provide RESEND_API_KEY in Secrets panel for live SMTP dispatch)"
            )
        }

        try {
            val payload = JSONObject().apply {
                put("from", from)
                put("to", JSONArray().apply { put(to) })
                put("subject", subject)
                put("html", htmlBody)
            }

            val request = Request.Builder()
                .url(RESEND_API_URL)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val id = json.optString("id", "resend_${System.currentTimeMillis()}")
                Log.i(TAG, "Resend Email dispatched successfully. ID: $id")
                ResendResult(
                    isSuccess = true,
                    messageId = id,
                    isSandbox = false,
                    details = "Live Resend Email Dispatched (Status 200 OK)"
                )
            } else {
                Log.e(TAG, "Resend API error: ${response.code} $responseBody")
                ResendResult(
                    isSuccess = false,
                    messageId = null,
                    isSandbox = false,
                    details = "Resend API HTTP ${response.code}: $responseBody"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Resend dispatch failed with exception", e)
            ResendResult(
                isSuccess = false,
                messageId = null,
                isSandbox = false,
                details = "Connection Error: ${e.localizedMessage ?: "Unknown network failure"}"
            )
        }
    }

    // High-Fidelity Responsive HTML Email Templates for Aura Banking
    fun buildTransactionApprovalEmail(
        amount: Double,
        recipient: String,
        approvalCode: String,
        accountName: String,
        reference: String
    ): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #080B11; color: #F1F5F9; margin: 0; padding: 24px; }
                    .card { background: linear-gradient(135deg, #131A29, #0D111A); border: 1px solid #E5C37844; border-radius: 16px; padding: 32px; max-width: 540px; margin: 0 auto; box-shadow: 0 10px 30px rgba(0,0,0,0.6); }
                    .header { border-bottom: 1px solid #1E293B; padding-bottom: 20px; margin-bottom: 24px; text-align: center; }
                    .logo { color: #E5C378; font-size: 22px; font-weight: 800; letter-spacing: 2px; }
                    .badge { display: inline-block; background: #E5C37822; color: #E5C378; border: 1px solid #E5C37866; border-radius: 20px; font-size: 11px; font-weight: bold; padding: 4px 12px; margin-top: 8px; text-transform: uppercase; }
                    .title { font-size: 20px; font-weight: 700; color: #FFFFFF; margin: 16px 0 8px 0; }
                    .amount { font-size: 32px; font-weight: 900; color: #E5C378; font-family: monospace; margin: 12px 0; }
                    .details-table { width: 100%; border-collapse: collapse; margin: 20px 0; font-size: 14px; }
                    .details-table td { padding: 8px 0; color: #94A3B8; border-bottom: 1px solid #1E293B; }
                    .details-table td.val { text-align: right; color: #F8FAFC; font-weight: 600; }
                    .otp-box { background: #0A0E17; border: 2px dashed #E5C378; border-radius: 12px; padding: 18px; text-align: center; margin: 24px 0; }
                    .otp-code { font-size: 28px; font-weight: 900; letter-spacing: 6px; color: #FFFFFF; font-family: monospace; }
                    .footer { text-align: center; color: #64748B; font-size: 12px; margin-top: 24px; line-height: 1.5; }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="header">
                        <div class="logo">AURA PRIVATE BANKING</div>
                        <div class="badge">Security Approval Protocol</div>
                    </div>
                    <div class="title">Wire Authorization Required</div>
                    <p style="color: #94A3B8; line-height: 1.5;">An outbound institutional wire transfer requires your multi-factor verification.</p>
                    <div class="amount">$${"%,.2f".format(amount)} USD</div>
                    <table class="details-table">
                        <tr><td>Beneficiary</td><td class="val">$recipient</td></tr>
                        <tr><td>Source Account</td><td class="val">$accountName</td></tr>
                        <tr><td>Transfer Reference</td><td class="val">$reference</td></tr>
                        <tr><td>Network Routing</td><td class="val">FedNow / CHIPS Institutional</td></tr>
                    </table>
                    <div class="otp-box">
                        <div style="color: #E5C378; font-size: 11px; text-transform: uppercase; font-weight: bold; margin-bottom: 6px;">One-Time Approval Token</div>
                        <div class="otp-code">$approvalCode</div>
                    </div>
                    <div class="footer">
                        If you did not authorize this wire, freeze your card instantly in the Aura Bank app or contact your private client advisor immediately.
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun buildSecurityChangeEmail(
        changeTitle: String,
        details: String,
        timestamp: String
    ): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #080B11; color: #F1F5F9; margin: 0; padding: 24px; }
                    .card { background: linear-gradient(135deg, #131A29, #0D111A); border: 1px solid #10B98144; border-radius: 16px; padding: 32px; max-width: 540px; margin: 0 auto; box-shadow: 0 10px 30px rgba(0,0,0,0.6); }
                    .header { border-bottom: 1px solid #1E293B; padding-bottom: 16px; margin-bottom: 20px; }
                    .logo { color: #10B981; font-size: 20px; font-weight: 800; letter-spacing: 2px; }
                    .status-pill { display: inline-block; background: #10B98122; color: #10B981; border: 1px solid #10B98166; border-radius: 20px; font-size: 11px; font-weight: bold; padding: 4px 12px; margin-top: 8px; }
                    .title { font-size: 18px; font-weight: 700; color: #FFFFFF; margin: 16px 0 8px 0; }
                    .info-box { background: #0A0E17; border-radius: 10px; padding: 16px; margin: 16px 0; border-left: 4px solid #10B981; }
                    .footer { text-align: center; color: #64748B; font-size: 12px; margin-top: 24px; }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="header">
                        <div class="logo">AURA DEFENSE INTELLIGENCE</div>
                        <div class="status-pill">Account Security Notification</div>
                    </div>
                    <div class="title">$changeTitle</div>
                    <div class="info-box">
                        <p style="margin: 0; color: #F8FAFC; font-size: 14px; line-height: 1.5;">$details</p>
                        <p style="margin: 8px 0 0 0; color: #64748B; font-size: 12px; font-family: monospace;">Timestamp: $timestamp</p>
                    </div>
                    <p style="color: #94A3B8; font-size: 13px; line-height: 1.5;">
                        This security modification was applied via authorized device telemetry. All active private client session credentials were authenticated.
                    </p>
                    <div class="footer">Aura Institutional Trust • Zero-Knowledge Private Wealth Architecture</div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun buildDepositNotificationEmail(
        amount: Double,
        accountName: String,
        reference: String,
        newBalance: Double
    ): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #080B11; color: #F1F5F9; margin: 0; padding: 24px; }
                    .card { background: linear-gradient(135deg, #131A29, #0D111A); border: 1px solid #10B98166; border-radius: 16px; padding: 32px; max-width: 540px; margin: 0 auto; }
                    .header { border-bottom: 1px solid #1E293B; padding-bottom: 16px; margin-bottom: 20px; }
                    .logo { color: #E5C378; font-size: 20px; font-weight: 800; letter-spacing: 2px; }
                    .deposit-badge { background: #10B98122; color: #10B981; border: 1px solid #10B98166; border-radius: 20px; font-size: 11px; font-weight: bold; padding: 4px 12px; display: inline-block; margin-top: 8px; }
                    .amount { font-size: 34px; font-weight: 900; color: #10B981; font-family: monospace; margin: 16px 0; }
                    .row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #1E293B; font-size: 14px; }
                    .footer { text-align: center; color: #64748B; font-size: 12px; margin-top: 24px; }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="header">
                        <div class="logo">AURA PRIVATE BANKING</div>
                        <div class="deposit-badge">Direct Liquidity Settlement</div>
                    </div>
                    <div style="font-size: 18px; font-weight: bold; color: #FFFFFF;">Funds Deposited & Cleared</div>
                    <div class="amount">+$${"%,.2f".format(amount)} USD</div>
                    <div style="color: #94A3B8; font-size: 14px; margin-bottom: 16px;">
                        Deposited to: <b style="color: #FFFFFF;">$accountName</b><br>
                        Wire Reference: <span style="font-family: monospace; color: #E5C378;">$reference</span><br>
                        Current Available Balance: <b style="color: #10B981;">$${"%,.2f".format(newBalance)} USD</b>
                    </div>
                    <div class="footer">FedNow Instant Settlement Protocol • FDIC Insured Tier-1 Custody</div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}
