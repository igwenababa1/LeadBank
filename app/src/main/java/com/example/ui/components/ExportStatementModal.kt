package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionStatus
import com.example.ui.theme.BankingTheme
import com.example.util.StatementExporter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar
import kotlin.math.abs

enum class ExportFormat {
    PDF_STATEMENT,
    CSV_SPREADSHEET
}

enum class DateRangePreset(val label: String, val daysAgo: Int) {
    LAST_30_DAYS("Last 30 Days", 30),
    LAST_90_DAYS("Last 90 Days", 90),
    YEAR_TO_DATE("Year to Date", 270),
    ALL_TIME("All Time", 3650)
}

enum class FlowFilter(val label: String) {
    ALL("All Flows"),
    CREDITS("Credits (+)"),
    DEBITS("Debits (-)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportStatementModal(
    accounts: List<AccountEntity>,
    allTransactions: List<TransactionEntity>,
    initialAccountId: String? = null,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onPostNotice: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedAccountId by remember { mutableStateOf(initialAccountId) }
    var selectedDateRange by remember { mutableStateOf(DateRangePreset.LAST_30_DAYS) }
    var selectedFlow by remember { mutableStateOf(FlowFilter.ALL) }
    var selectedStatus by remember { mutableStateOf<TransactionStatus?>(null) }
    var selectedCategory by remember { mutableStateOf<TransactionCategory?>(null) }
    var minAmountText by remember { mutableStateOf("") }
    var exportFormat by remember { mutableStateOf(ExportFormat.PDF_STATEMENT) }

    // Loading & Result States
    var isGenerating by remember { mutableStateOf(false) }
    var generationProgressText by remember { mutableStateOf("") }
    var generatedFile by remember { mutableStateOf<File?>(null) }
    var isAccountMenuOpen by remember { mutableStateOf(false) }

    val activeAccount = accounts.find { it.id == selectedAccountId }

    // Filter transactions based on selection
    val filteredTxs by remember {
        derivedStateOf {
            val now = System.currentTimeMillis()
            val cutoff = now - (selectedDateRange.daysAgo.toLong() * 24 * 60 * 60 * 1000L)
            val minAmt = minAmountText.toDoubleOrNull() ?: 0.0

            allTransactions.filter { tx ->
                val matchesDate = tx.timestamp >= cutoff
                val matchesFlow = when (selectedFlow) {
                    FlowFilter.ALL -> true
                    FlowFilter.CREDITS -> tx.amount > 0
                    FlowFilter.DEBITS -> tx.amount < 0
                }
                val matchesStatus = (selectedStatus == null || tx.status == selectedStatus)
                val matchesCategory = (selectedCategory == null || tx.category == selectedCategory)
                val matchesAmount = (minAmt <= 0.0 || abs(tx.amount) >= minAmt)

                matchesDate && matchesFlow && matchesStatus && matchesCategory && matchesAmount
            }
        }
    }

    val totalCredits by remember {
        derivedStateOf { filteredTxs.filter { it.amount > 0 }.sumOf { it.amount } }
    }
    val totalDebits by remember {
        derivedStateOf { filteredTxs.filter { it.amount < 0 }.sumOf { abs(it.amount) } }
    }

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
        modifier = modifier.testTag("export_statement_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BankingTheme.colors.primaryAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = BankingTheme.colors.primaryAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Export Statement",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BankingTheme.colors.textPrimary
                        )
                        Text(
                            text = "Audited Financial Reports & Receipts",
                            style = MaterialTheme.typography.labelSmall,
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

            Spacer(modifier = Modifier.height(18.dp))

            if (generatedFile != null) {
                // SUCCESS STATE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BankingTheme.colors.surfaceCard)
                        .border(1.dp, BankingTheme.colors.success.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(BankingTheme.colors.success.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BankingTheme.colors.success,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Statement Successfully Compiled",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BankingTheme.colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${generatedFile?.name}",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = BankingTheme.colors.primaryAccent
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Includes ${filteredTxs.size} reconciled ledger entries with digital verification hash and FDIC sweep disclosures.",
                            style = MaterialTheme.typography.bodySmall,
                            color = BankingTheme.colors.textSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val f = generatedFile ?: return@OutlinedButton
                                    StatementExporter.shareFile(
                                        context = context,
                                        file = f,
                                        mimeType = if (f.name.endsWith(".pdf")) "application/pdf" else "text/csv",
                                        title = "Share Lead Statement"
                                    )
                                },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = {
                                    val f = generatedFile ?: return@Button
                                    StatementExporter.openFile(
                                        context = context,
                                        file = f,
                                        mimeType = if (f.name.endsWith(".pdf")) "application/pdf" else "text/csv",
                                        title = "View Lead Statement"
                                    )
                                },
                                modifier = Modifier.weight(1.2f).height(46.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BankingTheme.colors.primaryAccent,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open / Print", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        TextButton(onClick = { generatedFile = null }) {
                            Text("Export Another Statement", color = BankingTheme.colors.textSecondary, fontSize = 12.sp)
                        }
                    }
                }
            } else if (isGenerating) {
                // GENERATING / LOADING STATE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BankingTheme.colors.surfaceCard)
                        .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = BankingTheme.colors.primaryAccent,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Generating Statement...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BankingTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = generationProgressText,
                            style = MaterialTheme.typography.bodySmall,
                            color = BankingTheme.colors.primaryAccent,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // FORM STATE: Configure filters and export

                // 1. Account Selector
                Text(
                    text = "SELECT ACCOUNT",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = BankingTheme.colors.textSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BankingTheme.colors.surfaceCard)
                            .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(12.dp))
                            .clickable { isAccountMenuOpen = true }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = activeAccount?.name ?: "All Accounts (Consolidated Ledger)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = BankingTheme.colors.textPrimary
                            )
                            Text(
                                text = if (activeAccount != null)
                                    "Balance: $${"%,.2f".format(activeAccount.balance)} • ${activeAccount.accountNumber}"
                                else "All checking, vaults and investment portfolios",
                                style = MaterialTheme.typography.bodySmall,
                                color = BankingTheme.colors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = BankingTheme.colors.primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = isAccountMenuOpen,
                        onDismissRequest = { isAccountMenuOpen = false },
                        modifier = Modifier.background(BankingTheme.colors.surfaceCard)
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Accounts (Consolidated)", color = BankingTheme.colors.textPrimary) },
                            onClick = {
                                selectedAccountId = null
                                isAccountMenuOpen = false
                            }
                        )
                        accounts.forEach { acc ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(acc.name, fontWeight = FontWeight.Bold, color = BankingTheme.colors.textPrimary)
                                        Text("$${"%,.2f".format(acc.balance)} • ${acc.type.badge}", fontSize = 11.sp, color = BankingTheme.colors.textSecondary)
                                    }
                                },
                                onClick = {
                                    selectedAccountId = acc.id
                                    isAccountMenuOpen = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Date Range Presets
                Text(
                    text = "STATEMENT PERIOD",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = BankingTheme.colors.textSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(DateRangePreset.values()) { preset ->
                        val isSelected = selectedDateRange == preset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) BankingTheme.colors.primaryAccent
                                    else BankingTheme.colors.surfaceCard
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) BankingTheme.colors.primaryAccent else BankingTheme.colors.border,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedDateRange = preset }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = preset.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else BankingTheme.colors.textPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Flow & Status Filters
                Text(
                    text = "TRANSACTION FLOW & STATUS",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = BankingTheme.colors.textSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FlowFilter.values().forEach { flow ->
                        val isSelected = selectedFlow == flow
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) BankingTheme.colors.surfaceCardElevated
                                    else BankingTheme.colors.surfaceCard
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) BankingTheme.colors.primaryAccent else BankingTheme.colors.border,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedFlow = flow }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = flow.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) BankingTheme.colors.primaryAccent else BankingTheme.colors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Export Format Selector (PDF vs CSV)
                Text(
                    text = "OUTPUT FORMAT",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = BankingTheme.colors.textSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // PDF Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (exportFormat == ExportFormat.PDF_STATEMENT)
                                    BankingTheme.colors.primaryAccent.copy(alpha = 0.15f)
                                else BankingTheme.colors.surfaceCard
                            )
                            .border(
                                1.2.dp,
                                if (exportFormat == ExportFormat.PDF_STATEMENT)
                                    BankingTheme.colors.primaryAccent
                                else BankingTheme.colors.border,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { exportFormat = ExportFormat.PDF_STATEMENT }
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (exportFormat == ExportFormat.PDF_STATEMENT)
                                        BankingTheme.colors.primaryAccent
                                    else BankingTheme.colors.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PDF Statement",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BankingTheme.colors.textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Official letterhead, FDIC disclosure, tables & signature",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = BankingTheme.colors.textSecondary
                            )
                        }
                    }

                    // CSV Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (exportFormat == ExportFormat.CSV_SPREADSHEET)
                                    BankingTheme.colors.primaryAccent.copy(alpha = 0.15f)
                                else BankingTheme.colors.surfaceCard
                            )
                            .border(
                                1.2.dp,
                                if (exportFormat == ExportFormat.CSV_SPREADSHEET)
                                    BankingTheme.colors.primaryAccent
                                else BankingTheme.colors.border,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { exportFormat = ExportFormat.CSV_SPREADSHEET }
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TableChart,
                                    contentDescription = null,
                                    tint = if (exportFormat == ExportFormat.CSV_SPREADSHEET)
                                        BankingTheme.colors.primaryAccent
                                    else BankingTheme.colors.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CSV Spreadsheet",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BankingTheme.colors.textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Excel, QuickBooks, Xero & accounting software",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = BankingTheme.colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Real-time Preview KPI Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BankingTheme.colors.surfaceCard)
                        .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MATCHING TRANSACTIONS",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp,
                                color = BankingTheme.colors.textSecondary
                            )
                            Text(
                                text = "${filteredTxs.size} items",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BankingTheme.colors.primaryAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Inflow", fontSize = 10.sp, color = BankingTheme.colors.textSecondary)
                                Text(
                                    "+$${"%,.2f".format(totalCredits)}",
                                    fontWeight = FontWeight.Bold,
                                    color = BankingTheme.colors.success,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Column {
                                Text("Total Outflow", fontSize = 10.sp, color = BankingTheme.colors.textSecondary)
                                Text(
                                    "-$${"%,.2f".format(totalDebits)}",
                                    fontWeight = FontWeight.Bold,
                                    color = BankingTheme.colors.textPrimary,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Column {
                                Text("Net Settlement", fontSize = 10.sp, color = BankingTheme.colors.textSecondary)
                                val net = totalCredits - totalDebits
                                Text(
                                    (if (net >= 0) "+" else "-") + "$${"%,.2f".format(abs(net))}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (net >= 0) BankingTheme.colors.success else BankingTheme.colors.error,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Action Button: Generate Statement
                Button(
                    onClick = {
                        scope.launch {
                            isGenerating = true
                            generationProgressText = "Reconciling cryptographic ledger entries..."
                            delay(400)
                            generationProgressText = "Compiling vector tables & FDIC sweep disclosures..."
                            delay(450)
                            generationProgressText = "Signing document with SHA-256 seal..."
                            delay(350)

                            val file = if (exportFormat == ExportFormat.PDF_STATEMENT) {
                                StatementExporter.generatePdfStatement(
                                    context = context,
                                    account = activeAccount,
                                    transactions = filteredTxs,
                                    dateRangeLabel = selectedDateRange.label
                                )
                            } else {
                                StatementExporter.generateCsvStatement(
                                    context = context,
                                    account = activeAccount,
                                    transactions = filteredTxs
                                )
                            }

                            isGenerating = false
                            generatedFile = file
                            onPostNotice("Statement compiled: ${file.name}")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("execute_statement_export_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BankingTheme.colors.primaryAccent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (exportFormat == ExportFormat.PDF_STATEMENT)
                            "Download Official PDF Statement"
                        else "Download CSV Spreadsheet",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
