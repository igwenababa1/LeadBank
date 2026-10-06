package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionStatus
import com.example.ui.theme.BankingTheme
import com.example.util.StatementExporter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

enum class ReceiptDisplayMode {
    ELECTRONIC_SLIP,
    OFFICIAL_CHECK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptModal(
    transaction: TransactionEntity,
    account: AccountEntity?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onPostNotice: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var displayMode by remember { mutableStateOf(ReceiptDisplayMode.ELECTRONIC_SLIP) }
    var isExporting by remember { mutableStateOf(false) }

    val formattedDate = SimpleDateFormat("MMMM dd, yyyy • hh:mm:ss a z", Locale.US).format(Date(transaction.timestamp))
    val checkDate = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(transaction.timestamp))
    val isCredit = transaction.amount > 0
    val absAmount = abs(transaction.amount)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BankingTheme.colors.backgroundElevated,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BankingTheme.colors.border)
            )
        },
        modifier = modifier.testTag("receipt_modal_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row: Title & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BankingTheme.colors.primaryAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = BankingTheme.colors.primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Official Clearance Record",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BankingTheme.colors.textPrimary
                        )
                        Text(
                            text = "Reference: ${transaction.reference}",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = BankingTheme.colors.textSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(BankingTheme.colors.surfaceCard)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = BankingTheme.colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mode Selector: Payment Voucher vs Official Check Facsimile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BankingTheme.colors.surfaceCard)
                    .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (displayMode == ReceiptDisplayMode.ELECTRONIC_SLIP)
                                BankingTheme.colors.primaryAccent
                            else Color.Transparent
                        )
                        .clickable { displayMode = ReceiptDisplayMode.ELECTRONIC_SLIP }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = if (displayMode == ReceiptDisplayMode.ELECTRONIC_SLIP)
                                Color.Black
                            else BankingTheme.colors.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Payment Voucher",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (displayMode == ReceiptDisplayMode.ELECTRONIC_SLIP)
                                Color.Black
                            else BankingTheme.colors.textSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (displayMode == ReceiptDisplayMode.OFFICIAL_CHECK)
                                BankingTheme.colors.primaryAccent
                            else Color.Transparent
                        )
                        .clickable { displayMode = ReceiptDisplayMode.OFFICIAL_CHECK }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = if (displayMode == ReceiptDisplayMode.OFFICIAL_CHECK)
                                Color.Black
                            else BankingTheme.colors.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Official Check Record",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (displayMode == ReceiptDisplayMode.OFFICIAL_CHECK)
                                Color.Black
                            else BankingTheme.colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body Display
            if (displayMode == ReceiptDisplayMode.ELECTRONIC_SLIP) {
                // Electronic Voucher Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BankingTheme.colors.surfaceCard)
                        .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        // Top bank tag & FDIC
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LEAD PRIVATE BANK, N.A.",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                color = BankingTheme.colors.primaryAccent
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = BankingTheme.colors.success,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "FDIC SWEEP CLEARANCE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    color = BankingTheme.colors.success,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Amount hero
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(BankingTheme.colors.surfaceCardElevated)
                                .border(1.dp, BankingTheme.colors.borderGlow.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "TOTAL SETTLED AMOUNT",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp,
                                color = BankingTheme.colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = (if (isCredit) "+" else "-") + "$${"%,.2f".format(absAmount)} USD",
                                style = MaterialTheme.typography.headlineMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (isCredit) BankingTheme.colors.success else BankingTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = BankingTheme.colors.success,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "FEDNOW 24/7 INSTANT SETTLEMENT",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BankingTheme.colors.success
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Ledger breakdown
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            ReceiptItemRow("Beneficiary / Merchant", transaction.merchant)
                            ReceiptItemRow("Reference ID", transaction.reference, isMonospace = true)
                            ReceiptItemRow("Settlement Timestamp", formattedDate)
                            ReceiptItemRow("Payment Instrument", transaction.paymentMethod)
                            ReceiptItemRow("Account debited", account?.name ?: "Private Checking •• 8842")
                            ReceiptItemRow("Clearing Category", transaction.category.displayName)
                            ReceiptItemRow("Authorized Location", transaction.location)

                            if (transaction.note.isNotBlank()) {
                                ReceiptItemRow("Remittance Purpose", transaction.note)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Cryptographic seal box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BankingTheme.colors.surfaceCardElevated)
                                .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = BankingTheme.colors.primaryAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "CRYPTO-AUDIT TOKEN (SHA-256)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BankingTheme.colors.primaryAccent
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "0x8f4a9b2c${transaction.reference.hashCode().toString(16)}e771029ba81c",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    color = BankingTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }
            } else {
                // Official Check Facsimile Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFFDF5)) // Warm check parchment
                        .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        // Check Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "LEAD PRIVATE BANK, N.A.",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "PRIVATE TREASURY SERVICES • SAN FRANCISCO, CA",
                                    fontSize = 7.5.sp,
                                    color = Color(0xFF475569)
                                )
                                Text(
                                    text = "Drawer: Alexander Sterling (•• 8842)",
                                    fontSize = 7.5.sp,
                                    color = Color(0xFF475569)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "CHECK NO. ${transaction.reference.filter { it.isDigit() }.takeLast(6).ifEmpty { "084920" }}",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "DATE: $checkDate",
                                    fontSize = 8.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Pay to order of + Amount box
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PAY TO THE\nORDER OF",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                lineHeight = 8.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = transaction.merchant,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color(0xFFB48328), RoundedCornerShape(4.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$${"%,.2f".format(absAmount)}",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Memo line & signature line
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "FOR: ${transaction.note.ifBlank { "Client Settlement Clearance" }}",
                                    fontSize = 8.sp,
                                    color = Color(0xFF475569)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .height(1.dp)
                                        .background(Color(0xFFCBD5E1))
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Alexander Sterling",
                                    fontFamily = FontFamily.Cursive,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .height(1.dp)
                                        .background(Color(0xFFCBD5E1))
                                )
                                Text(
                                    text = "AUTHORIZED SIGNATURE",
                                    fontSize = 6.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // MICR Line
                        Text(
                            text = "⑆121000358⑆  008492019488⑈  084920⑈",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color(0xFF0F172A),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons: Share, Print, Download PDF
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Share
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isExporting = true
                            delay(400)
                            val file = if (displayMode == ReceiptDisplayMode.ELECTRONIC_SLIP) {
                                StatementExporter.generateReceiptPdf(context, transaction, account)
                            } else {
                                StatementExporter.generateCheckRecordPdf(context, transaction, account)
                            }
                            isExporting = false
                            StatementExporter.shareFile(
                                context = context,
                                file = file,
                                mimeType = "application/pdf",
                                title = "Share Transaction Receipt ${transaction.reference}"
                            )
                            onPostNotice("Receipt generated and sent to Sharesheet.")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = BankingTheme.colors.textPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BankingTheme.colors.textPrimary
                    )
                }

                // Print
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isExporting = true
                            delay(400)
                            val file = if (displayMode == ReceiptDisplayMode.ELECTRONIC_SLIP) {
                                StatementExporter.generateReceiptPdf(context, transaction, account)
                            } else {
                                StatementExporter.generateCheckRecordPdf(context, transaction, account)
                            }
                            isExporting = false
                            StatementExporter.openFile(
                                context = context,
                                file = file,
                                mimeType = "application/pdf",
                                title = "Print Transaction Record"
                            )
                            onPostNotice("Opening document for direct printing...")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = null,
                        tint = BankingTheme.colors.textPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Print",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BankingTheme.colors.textPrimary
                    )
                }

                // Download / Open PDF
                Button(
                    onClick = {
                        scope.launch {
                            isExporting = true
                            delay(500)
                            val file = if (displayMode == ReceiptDisplayMode.ELECTRONIC_SLIP) {
                                StatementExporter.generateReceiptPdf(context, transaction, account)
                            } else {
                                StatementExporter.generateCheckRecordPdf(context, transaction, account)
                            }
                            isExporting = false
                            StatementExporter.openFile(
                                context = context,
                                file = file,
                                mimeType = "application/pdf",
                                title = "Lead Clearance Record"
                            )
                            onPostNotice("Voucher saved: ${file.name}")
                        }
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("download_official_pdf_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BankingTheme.colors.primaryAccent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save PDF",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptItemRow(
    label: String,
    value: String,
    isMonospace: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = BankingTheme.colors.textSecondary,
            fontSize = 11.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            color = BankingTheme.colors.textPrimary,
            fontSize = 11.sp
        )
    }
}
