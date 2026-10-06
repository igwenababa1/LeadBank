package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.data.model.AdminAuditLogEntity
import com.example.data.model.AdminDashboardTab
import com.example.data.model.AdminTicket
import com.example.data.model.AdminTicketPriority
import com.example.data.model.AdminTicketStatus
import com.example.data.model.AlertType
import com.example.data.model.CardEntity
import com.example.data.model.CustomerEmailEntity
import com.example.data.model.SystemGatewayStatus
import com.example.data.model.TransactionEntity
import com.example.ui.theme.BankingTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    accounts: List<AccountEntity>,
    cards: List<CardEntity>,
    allTransactions: List<TransactionEntity>,
    tickets: List<AdminTicket>,
    immutableAuditLogs: List<AdminAuditLogEntity>,
    customerEmails: List<CustomerEmailEntity>,
    gateways: List<SystemGatewayStatus>,
    selectedTab: AdminDashboardTab,
    isUnlocked: Boolean,
    operatorEmail: String = "Igwenababa@gmail.com",
    onTabSelect: (AdminDashboardTab) -> Unit,
    onAuthenticate: (email: String, pass: String) -> Boolean,
    onLock: () -> Unit,
    onAdjustBalance: (accountId: String, amount: Double, isCredit: Boolean, reasonCode: String, memo: String) -> Unit,
    onInjectWire: (accountId: String, senderName: String, senderBank: String, routingNumber: String, amount: Double, wireType: String, memo: String) -> Unit,
    onResolveDispute: (txId: String, approveRefund: Boolean, targetAccountId: String?, note: String) -> Unit,
    onOverrideCard: (cardId: String, isFrozen: Boolean, limit: Double) -> Unit,
    onDeleteTransaction: (txId: String) -> Unit,
    onReplyTicket: (ticketId: String, replyMessage: String, newStatus: AdminTicketStatus) -> Unit,
    onCreditCourtesyCompensation: (ticketId: String, accountId: String, amount: Double, reason: String) -> Unit,
    onBroadcastAlert: (title: String, message: String, priority: AlertType) -> Unit,
    onDispatchCustomerEmail: (recipientEmail: String, recipientName: String, subject: String, templateType: String, headline: String, bodyText: String, amount: Double?) -> Unit,
    onUpdateAccountConfig: (accountId: String, name: String, apy: Double, routing: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var emailInput by remember { mutableStateOf("Igwenababa@gmail.com") }
    var passwordInput by remember { mutableStateOf("Igwe1992@") }
    var passError by remember { mutableStateOf(false) }

    // Active modals
    var isAdjustModalOpen by remember { mutableStateOf(false) }
    var adjustTargetAccount by remember { mutableStateOf<AccountEntity?>(null) }
    var isWireModalOpen by remember { mutableStateOf(false) }
    var isBroadcastModalOpen by remember { mutableStateOf(false) }
    var isConfigModalOpen by remember { mutableStateOf(false) }
    var configTargetAccount by remember { mutableStateOf<AccountEntity?>(null) }
    var selectedEmailForDetail by remember { mutableStateOf<CustomerEmailEntity?>(null) }

    val adjustSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val wireSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val broadcastSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val configSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val emailDetailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (!isUnlocked) {
        // High-Security Back-Office Gate Lock with Email & Password
        AdminSecurityLockView(
            email = emailInput,
            password = passwordInput,
            passError = passError,
            onEmailChange = {
                emailInput = it
                passError = false
            },
            onPasswordChange = {
                passwordInput = it
                passError = false
            },
            onAttemptUnlock = {
                val success = onAuthenticate(emailInput, passwordInput)
                if (!success) passError = true
            },
            onAutoFillCredentials = {
                emailInput = "Igwenababa@gmail.com"
                passwordInput = "Igwe1992@"
                val success = onAuthenticate("Igwenababa@gmail.com", "Igwe1992@")
                if (!success) passError = true
            },
            modifier = modifier
        )
        return
    }

    val totalSystemWealth = accounts.sumOf { it.balance }
    val disputedCount = allTransactions.count { it.isDisputed }
    val openTicketsCount = tickets.count { it.status == AdminTicketStatus.OPEN || it.status == AdminTicketStatus.IN_REVIEW }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BankingTheme.colors.background)
            .testTag("superior_admin_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Superior Admin Executive Banner & Operational Status
        item {
            AdminExecutiveHeader(
                operatorId = operatorEmail,
                clearance = "LEVEL 5 - EXECUTIVE ACCESS",
                totalSystemWealth = totalSystemWealth,
                disputedCount = disputedCount,
                openTicketsCount = openTicketsCount,
                auditLogsCount = immutableAuditLogs.size,
                onLock = onLock,
                onOpenBroadcast = { isBroadcastModalOpen = true }
            )
        }

        // 2. Desk Navigation Tab Bar
        item {
            AdminDeskTabRow(
                selectedTab = selectedTab,
                onTabSelect = onTabSelect
            )
        }

        // 3. Tab Content
        when (selectedTab) {
            AdminDashboardTab.ACCOUNTS -> {
                item {
                    AdminAccountsDesk(
                        accounts = accounts,
                        onOpenAdjust = { acc ->
                            adjustTargetAccount = acc
                            isAdjustModalOpen = true
                        },
                        onOpenConfig = { acc ->
                            configTargetAccount = acc
                            isConfigModalOpen = true
                        }
                    )
                }
            }

            AdminDashboardTab.CONFIG -> {
                item {
                    AdminAccountConfigDesk(
                        accounts = accounts,
                        onOpenConfig = { acc ->
                            configTargetAccount = acc
                            isConfigModalOpen = true
                        }
                    )
                }
            }

            AdminDashboardTab.WIRES -> {
                item {
                    AdminWireInjectionDesk(
                        accounts = accounts,
                        recentTransactions = allTransactions.filter { it.category.name == "TRANSFER" || it.title.contains("Wire", ignoreCase = true) }.take(6),
                        onOpenWireModal = { isWireModalOpen = true }
                    )
                }
            }

            AdminDashboardTab.EMAILS -> {
                item {
                    AdminCustomerEmailsDesk(
                        customerEmails = customerEmails,
                        onDispatchEmail = onDispatchCustomerEmail,
                        onSelectEmail = { selectedEmailForDetail = it }
                    )
                }
            }

            AdminDashboardTab.DISPUTES -> {
                item {
                    AdminDisputesDesk(
                        accounts = accounts,
                        allTransactions = allTransactions,
                        onResolveDispute = onResolveDispute,
                        onDeleteTransaction = onDeleteTransaction
                    )
                }
            }

            AdminDashboardTab.CARDS -> {
                item {
                    AdminCardsDesk(
                        cards = cards,
                        onOverrideCard = onOverrideCard
                    )
                }
            }

            AdminDashboardTab.SUPPORT -> {
                item {
                    AdminCustomerCareDesk(
                        tickets = tickets,
                        accounts = accounts,
                        onSelectTicket = { },
                        onReplyTicket = onReplyTicket,
                        onCreditCourtesyCompensation = onCreditCourtesyCompensation
                    )
                }
            }

            AdminDashboardTab.TELEMETRY -> {
                item {
                    AdminTelemetryDesk(
                        gateways = gateways
                    )
                }
            }

            AdminDashboardTab.AUDIT -> {
                item {
                    AdminImmutableAuditLogDesk(
                        auditLogs = immutableAuditLogs
                    )
                }
            }
        }
    }

    // Modal: Balance Adjustment Form
    if (isAdjustModalOpen && adjustTargetAccount != null) {
        ModalBottomSheet(
            onDismissRequest = { isAdjustModalOpen = false },
            sheetState = adjustSheetState,
            containerColor = BankingTheme.colors.surfaceCardElevated
        ) {
            AdminBalanceAdjustSheet(
                account = adjustTargetAccount!!,
                onDismiss = { isAdjustModalOpen = false },
                onConfirmAdjust = { amount, isCredit, reason, memo ->
                    onAdjustBalance(adjustTargetAccount!!.id, amount, isCredit, reason, memo)
                    isAdjustModalOpen = false
                }
            )
        }
    }

    // Modal: Account Configuration Edit Form
    if (isConfigModalOpen && configTargetAccount != null) {
        ModalBottomSheet(
            onDismissRequest = { isConfigModalOpen = false },
            sheetState = configSheetState,
            containerColor = BankingTheme.colors.surfaceCardElevated
        ) {
            AdminAccountConfigSheet(
                account = configTargetAccount!!,
                onDismiss = { isConfigModalOpen = false },
                onConfirmConfig = { name, apy, routing ->
                    onUpdateAccountConfig(configTargetAccount!!.id, name, apy, routing)
                    isConfigModalOpen = false
                }
            )
        }
    }

    // Modal: Central Bank Wire Injection Form
    if (isWireModalOpen) {
        ModalBottomSheet(
            onDismissRequest = { isWireModalOpen = false },
            sheetState = wireSheetState,
            containerColor = BankingTheme.colors.surfaceCardElevated
        ) {
            AdminWireInjectSheet(
                accounts = accounts,
                onDismiss = { isWireModalOpen = false },
                onConfirmWire = { accId, sender, bank, routing, amount, rail, memo ->
                    onInjectWire(accId, sender, bank, routing, amount, rail, memo)
                    isWireModalOpen = false
                }
            )
        }
    }

    // Modal: Broadcast Official Memo
    if (isBroadcastModalOpen) {
        ModalBottomSheet(
            onDismissRequest = { isBroadcastModalOpen = false },
            sheetState = broadcastSheetState,
            containerColor = BankingTheme.colors.surfaceCardElevated
        ) {
            AdminBroadcastSheet(
                onDismiss = { isBroadcastModalOpen = false },
                onBroadcast = { title, message, priority ->
                    onBroadcastAlert(title, message, priority)
                    isBroadcastModalOpen = false
                }
            )
        }
    }

    // Modal: Email Detail & Letterhead
    if (selectedEmailForDetail != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedEmailForDetail = null },
            sheetState = emailDetailSheetState,
            containerColor = BankingTheme.colors.surfaceCardElevated
        ) {
            AdminEmailDetailSheet(
                email = selectedEmailForDetail!!,
                onDismiss = { selectedEmailForDetail = null }
            )
        }
    }
}

// -------------------------------------------------------------
// SUB-COMPONENTS: HEADER & AUTHENTICATION LOCK VIEW
// -------------------------------------------------------------

@Composable
fun AdminSecurityLockView(
    email: String,
    password: String,
    passError: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onAttemptUnlock: () -> Unit,
    onAutoFillCredentials: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BankingTheme.colors.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(BankingTheme.colors.surfaceCard)
                .border(1.2.dp, BankingTheme.colors.border, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6366F1).copy(alpha = 0.15f))
                    .border(1.5.dp, Color(0xFF818CF8), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Secured Vault",
                    tint = Color(0xFFA5B4FC),
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "SUPERIOR ADMIN CONSOLE",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = BankingTheme.colors.textPrimary,
                fontSize = 18.sp,
                letterSpacing = 1.sp
            )

            Text(
                text = "Level 5 Executive Back-Office & Compliance Gate",
                style = MaterialTheme.typography.bodySmall,
                color = BankingTheme.colors.textSecondary,
                fontSize = 11.5.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Admin Email Address", fontSize = 12.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = BankingTheme.colors.primaryAccent, modifier = Modifier.size(18.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BankingTheme.colors.primaryAccent,
                    unfocusedBorderColor = BankingTheme.colors.border
                ),
                modifier = Modifier.fillMaxWidth().testTag("admin_email_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Master Clearance Password", fontSize = 12.sp) },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passError,
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = BankingTheme.colors.primaryAccent, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility",
                            tint = BankingTheme.colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BankingTheme.colors.primaryAccent,
                    unfocusedBorderColor = BankingTheme.colors.border,
                    errorBorderColor = Color(0xFFEF4444)
                ),
                modifier = Modifier.fillMaxWidth().testTag("admin_password_input")
            )

            if (passError) {
                Text(
                    text = "Authentication failed. Check credentials (Igwenababa@gmail.com / Igwe1992@).",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAttemptUnlock,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BankingTheme.colors.primaryAccent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("admin_unlock_btn")
            ) {
                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Authorize Level 5 Access", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onAutoFillCredentials,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("admin_autofill_credentials_btn")
            ) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BankingTheme.colors.primaryAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Fill Credentials: Igwenababa@gmail.com", color = BankingTheme.colors.primaryAccent, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun AdminExecutiveHeader(
    operatorId: String,
    clearance: String,
    totalSystemWealth: Double,
    disputedCount: Int,
    openTicketsCount: Int,
    auditLogsCount: Int,
    onLock: () -> Unit,
    onOpenBroadcast: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = if (BankingTheme.colors.isDark) {
                        listOf(
                            Color(0xFF1E1B4B).copy(alpha = 0.7f),
                            Color(0xFF0F172A).copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    } else {
                        listOf(
                            Color(0xFFEDE9FE).copy(alpha = 0.8f),
                            Color(0xFFF1F5F9).copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    }
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top status pill & lock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = clearance,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE5C378),
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Broadcast button
                IconButton(
                    onClick = onOpenBroadcast,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(BankingTheme.colors.surfaceCard)
                        .border(1.dp, BankingTheme.colors.border, CircleShape)
                        .testTag("admin_broadcast_memo_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAlert,
                        contentDescription = "Broadcast Memo",
                        tint = BankingTheme.colors.primaryAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Lock button
                IconButton(
                    onClick = onLock,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(BankingTheme.colors.surfaceCard)
                        .border(1.dp, BankingTheme.colors.border, CircleShape)
                        .testTag("admin_lock_console_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock Console",
                        tint = BankingTheme.colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "SUPERIOR BANKING OPERATIONS",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = BankingTheme.colors.textPrimary,
            fontSize = 19.sp,
            letterSpacing = 0.5.sp
        )
        Text(
            text = "Officer: $operatorId • Immutable Audit Active",
            style = MaterialTheme.typography.bodySmall,
            color = BankingTheme.colors.textSecondary,
            fontSize = 11.5.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // System Metrics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminMetricCard(
                title = "Total Core Deposits",
                value = "$${"%,.2f".format(totalSystemWealth)}",
                badge = "Fed Insured",
                badgeColor = Color(0xFF10B981),
                modifier = Modifier.weight(1.3f)
            )

            AdminMetricCard(
                title = "Immutable Logs",
                value = "$auditLogsCount Verified",
                badge = "SHA-256",
                badgeColor = Color(0xFFE5C378),
                modifier = Modifier.weight(1.1f)
            )

            AdminMetricCard(
                title = "Open Tickets",
                value = "$openTicketsCount Queue",
                badge = "VIP Desk",
                badgeColor = Color(0xFF6366F1),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    badge: String,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BankingTheme.colors.surfaceCard)
            .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = BankingTheme.colors.textSecondary,
                    fontSize = 10.sp,
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = badgeColor,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BankingTheme.colors.textPrimary,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AdminDeskTabRow(
    selectedTab: AdminDashboardTab,
    onTabSelect: (AdminDashboardTab) -> Unit
) {
    val tabs = AdminDashboardTab.values()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { tab ->
            val isSelected = selectedTab == tab
            FilterChip(
                selected = isSelected,
                onClick = { onTabSelect(tab) },
                label = {
                    Text(
                        text = tab.label,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                leadingIcon = {
                    val icon = when (tab) {
                        AdminDashboardTab.ACCOUNTS -> Icons.Default.AccountBalance
                        AdminDashboardTab.CONFIG -> Icons.Default.Settings
                        AdminDashboardTab.WIRES -> Icons.Default.SwapHoriz
                        AdminDashboardTab.EMAILS -> Icons.Default.Email
                        AdminDashboardTab.DISPUTES -> Icons.Default.Gavel
                        AdminDashboardTab.CARDS -> Icons.Default.CreditCard
                        AdminDashboardTab.SUPPORT -> Icons.Default.HelpOutline
                        AdminDashboardTab.TELEMETRY -> Icons.Default.Memory
                        AdminDashboardTab.AUDIT -> Icons.Default.VerifiedUser
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BankingTheme.colors.primaryAccent,
                    selectedLabelColor = Color.Black,
                    selectedLeadingIconColor = Color.Black,
                    containerColor = BankingTheme.colors.surfaceCard,
                    labelColor = BankingTheme.colors.textSecondary,
                    iconColor = BankingTheme.colors.textSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = BankingTheme.colors.border,
                    selectedBorderColor = BankingTheme.colors.primaryAccent
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("admin_tab_${tab.name.lowercase()}")
            )
        }
    }
}

// -------------------------------------------------------------
// DESK 1: ACCOUNTS & LEDGERS
// -------------------------------------------------------------

@Composable
fun AdminAccountsDesk(
    accounts: List<AccountEntity>,
    onOpenAdjust: (AccountEntity) -> Unit,
    onOpenConfig: (AccountEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Core Client Accounts Ledger",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BankingTheme.colors.textPrimary,
                fontSize = 15.sp
            )
            Text(
                text = "${accounts.size} Active Depository Accounts",
                style = MaterialTheme.typography.labelSmall,
                color = BankingTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        accounts.forEach { acc ->
            AdminAccountRowCard(
                account = acc,
                onAdjustClick = { onOpenAdjust(acc) },
                onConfigClick = { onOpenConfig(acc) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun AdminAccountRowCard(
    account: AccountEntity,
    onAdjustClick: () -> Unit,
    onConfigClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BankingTheme.colors.surfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BankingTheme.colors.textPrimary,
                        fontSize = 14.5.sp
                    )
                    Text(
                        text = "Acct: ${account.accountNumber} • Routing: ${account.routingNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BankingTheme.colors.textSecondary,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BankingTheme.colors.primaryAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${account.apy}% APY",
                        color = BankingTheme.colors.primaryAccent,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Real-Time Ledger Balance",
                        style = MaterialTheme.typography.labelSmall,
                        color = BankingTheme.colors.textMuted,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "$${"%,.2f".format(account.balance)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = BankingTheme.colors.textPrimary,
                        fontSize = 18.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onConfigClick,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("admin_config_btn_${account.id}")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Config", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onAdjustClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BankingTheme.colors.primaryAccent,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("admin_adjust_account_${account.id}")
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Credit / Adjust", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DESK 2: CONFIGURATIONS & APY RATES
// -------------------------------------------------------------

@Composable
fun AdminAccountConfigDesk(
    accounts: List<AccountEntity>,
    onOpenConfig: (AccountEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BankingTheme.colors.surfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.2.dp, Color(0xFFE5C378).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5C378).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = Color(0xFFE5C378),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Central Bank Interest & APY Configurations",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BankingTheme.colors.textPrimary,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Adjust interest yields, account naming, and routing codes across all depository vaults in real time",
                            style = MaterialTheme.typography.bodySmall,
                            color = BankingTheme.colors.textSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        accounts.forEach { acc ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BankingTheme.colors.surfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = acc.name,
                            fontWeight = FontWeight.Bold,
                            color = BankingTheme.colors.textPrimary,
                            fontSize = 13.5.sp
                        )
                        Text(
                            text = "Routing: ${acc.routingNumber} • APY Rate: ${acc.apy}%",
                            color = BankingTheme.colors.textSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = { onOpenConfig(acc) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BankingTheme.colors.primaryAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Edit Config", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DESK 3: WIRE & SETTLEMENT INJECTION
// -------------------------------------------------------------

@Composable
fun AdminWireInjectionDesk(
    accounts: List<AccountEntity>,
    recentTransactions: List<TransactionEntity>,
    onOpenWireModal: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        // Wire Injection CTA Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BankingTheme.colors.surfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.2.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Central Bank Wire Simulator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BankingTheme.colors.textPrimary,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Inject live Fedwire, SWIFT, or FedNow funds directly into client accounts",
                            style = MaterialTheme.typography.bodySmall,
                            color = BankingTheme.colors.textSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onOpenWireModal,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF38BDF8),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("admin_open_wire_injector_btn")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Inject Incoming Wire Transfer", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Recent Central Wire & Transfer Inflows",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = BankingTheme.colors.textPrimary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (recentTransactions.isEmpty()) {
            Text(
                text = "No wire transfers recorded yet. Inject one above to test.",
                color = BankingTheme.colors.textSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            recentTransactions.forEach { tx ->
                AdminWireTransactionRow(tx = tx)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
fun AdminWireTransactionRow(tx: TransactionEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BankingTheme.colors.surfaceCard)
            .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = tx.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = BankingTheme.colors.textPrimary,
                    fontSize = 13.sp
                )
                Text(
                    text = "Ref: ${tx.reference} • ${tx.merchant}",
                    style = MaterialTheme.typography.bodySmall,
                    color = BankingTheme.colors.textSecondary,
                    fontSize = 10.5.sp
                )
            }
        }

        Text(
            text = "+$${"%,.2f".format(tx.amount)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF10B981),
            fontSize = 13.5.sp
        )
    }
}

// -------------------------------------------------------------
// DESK 4: CUSTOMER COMMUNICATIONS & OFFICIAL EMAILS
// -------------------------------------------------------------

@Composable
fun AdminCustomerEmailsDesk(
    customerEmails: List<CustomerEmailEntity>,
    onDispatchEmail: (recipientEmail: String, recipientName: String, subject: String, templateType: String, headline: String, bodyText: String, amount: Double?) -> Unit,
    onSelectEmail: (CustomerEmailEntity) -> Unit
) {
    var recipientName by remember { mutableStateOf("Alexander Vance") }
    var recipientEmail by remember { mutableStateOf("alexander.vance@leadwealth.com") }
    var selectedTemplate by remember { mutableStateOf("WIRE_CONFIRMATION") }
    var subject by remember { mutableStateOf("Inbound Fedwire Deposit Confirmation ($75,000.00)") }
    var headline by remember { mutableStateOf("Funds Credited to Lead Private Checking") }
    var bodyText by remember { mutableStateOf("We have processed and credited your incoming wire settlement. The funds are immediately available for international wire dispatch or investment sweeps.") }
    var amountText by remember { mutableStateOf("75000") }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        // Composer Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BankingTheme.colors.surfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.2.dp, Color(0xFF6366F1), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6366F1).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color(0xFFA5B4FC),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Dispatch Official Customer Email Notice",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BankingTheme.colors.textPrimary,
                            fontSize = 14.5.sp
                        )
                        Text(
                            text = "Sends verified banking communications with official FDIC disclosures & audit logs",
                            style = MaterialTheme.typography.bodySmall,
                            color = BankingTheme.colors.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Template selector chips
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "WIRE_CONFIRMATION" to "Wire Inflow",
                        "SECURITY_ALERT" to "Security Notice",
                        "BALANCE_ADJUSTMENT" to "Ledger Credit",
                        "REGULATORY_DISCLOSURE" to "FDIC Disclosure"
                    ).forEach { (tpl, label) ->
                        FilterChip(
                            selected = selectedTemplate == tpl,
                            onClick = {
                                selectedTemplate = tpl
                                when (tpl) {
                                    "WIRE_CONFIRMATION" -> {
                                        subject = "Inbound Fedwire Deposit Confirmation ($75,000.00)"
                                        headline = "Funds Credited to Lead Private Checking"
                                        bodyText = "We have processed and credited your incoming wire settlement. The funds are immediately available for international wire dispatch or investment sweeps."
                                        amountText = "75000"
                                    }
                                    "SECURITY_ALERT" -> {
                                        subject = "Executive Security Notice: High-Value Authorization Approved"
                                        headline = "Card Authorization Cleared"
                                        bodyText = "Your high-value card authorization has passed our zero-trust fraud verification and has been recorded in the central ledger."
                                        amountText = "12500"
                                    }
                                    "BALANCE_ADJUSTMENT" -> {
                                        subject = "Depository Account Balance Adjustment Advisory"
                                        headline = "Official Provisional Ledger Credit"
                                        bodyText = "A back-office executive adjustment has been posted to your account in accordance with Federal Reserve Regulation E."
                                        amountText = "5000"
                                    }
                                    "REGULATORY_DISCLOSURE" -> {
                                        subject = "Annual FDIC Sweep Network Coverage Advisory ($5,000,000)"
                                        headline = "Federal Deposit Insurance Corporation Verification"
                                        bodyText = "Your aggregate depository assets remain fully covered under Lead's Multi-Bank Insured Sweep Program, securing up to $5,000,000.00 in FDIC insurance with zero counterparty lockup."
                                        amountText = "5000000"
                                    }
                                }
                            },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BankingTheme.colors.primaryAccent,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = recipientName,
                        onValueChange = { recipientName = it },
                        label = { Text("Customer Name", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = recipientEmail,
                        onValueChange = { recipientEmail = it },
                        label = { Text("Customer Email", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1.3f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Email Subject Line", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_email_subject_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bodyText,
                    onValueChange = { bodyText = it },
                    label = { Text("Email Body Content", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("admin_email_body_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull()
                        onDispatchEmail(recipientEmail, recipientName, subject, selectedTemplate, headline, bodyText, amt)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6366F1),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("admin_send_email_btn")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Official Customer Email", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Sent Communications Archive (${customerEmails.size} Dispatches)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = BankingTheme.colors.textPrimary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (customerEmails.isEmpty()) {
            Text("No customer communications sent yet.", color = BankingTheme.colors.textSecondary, fontSize = 12.sp)
        } else {
            customerEmails.forEach { email ->
                AdminCustomerEmailRow(email = email, onClick = { onSelectEmail(email) })
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
fun AdminCustomerEmailRow(
    email: CustomerEmailEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BankingTheme.colors.surfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = email.subject,
                        fontWeight = FontWeight.Bold,
                        color = BankingTheme.colors.textPrimary,
                        fontSize = 12.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "To: ${email.recipientName} • Ref: ${email.referenceCode}",
                        color = BankingTheme.colors.textSecondary,
                        fontSize = 10.5.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("DELIVERED", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------
// DESK 5: DISPUTES & COMPLIANCE
// -------------------------------------------------------------

@Composable
fun AdminDisputesDesk(
    accounts: List<AccountEntity>,
    allTransactions: List<TransactionEntity>,
    onResolveDispute: (txId: String, approveRefund: Boolean, targetAccountId: String?, note: String) -> Unit,
    onDeleteTransaction: (txId: String) -> Unit
) {
    val disputedTxs = allTransactions.filter { it.isDisputed }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Active Chargeback & Dispute Arbitration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BankingTheme.colors.textPrimary,
                fontSize = 15.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (disputedTxs.isNotEmpty()) Color(0xFFF59E0B).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${disputedTxs.size} Active",
                    color = if (disputedTxs.isNotEmpty()) Color(0xFFF59E0B) else Color(0xFF10B981),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (disputedTxs.isEmpty()) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BankingTheme.colors.surfaceCard),
                modifier = Modifier.fillMaxWidth().border(1.dp, BankingTheme.colors.border, RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Zero Active Customer Disputes",
                        fontWeight = FontWeight.Bold,
                        color = BankingTheme.colors.textPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "All transactions are fully reconciled. If a customer files a dispute from transaction details, it will appear here for back-office arbitration.",
                        textAlign = TextAlign.Center,
                        color = BankingTheme.colors.textSecondary,
                        fontSize = 11.5.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            disputedTxs.forEach { tx ->
                AdminDisputeCard(
                    tx = tx,
                    accounts = accounts,
                    onApproveRefund = { note ->
                        onResolveDispute(tx.id, true, accounts.firstOrNull()?.id, note)
                    },
                    onRejectDispute = { note ->
                        onResolveDispute(tx.id, false, null, note)
                    },
                    onExpunge = {
                        onDeleteTransaction(tx.id)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun AdminDisputeCard(
    tx: TransactionEntity,
    accounts: List<AccountEntity>,
    onApproveRefund: (String) -> Unit,
    onRejectDispute: (String) -> Unit,
    onExpunge: () -> Unit
) {
    var resolutionNote by remember { mutableStateOf("Merchant claim verified; customer credited.") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BankingTheme.colors.surfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, Color(0xFFF59E0B), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DISPUTE CASE: #${tx.id.takeLast(6).uppercase()}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B),
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "$${"%,.2f".format(kotlin.math.abs(tx.amount))}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = BankingTheme.colors.textPrimary,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${tx.title} (${tx.merchant})",
                fontWeight = FontWeight.SemiBold,
                color = BankingTheme.colors.textPrimary,
                fontSize = 13.sp
            )
            Text(
                text = "Ref: ${tx.reference} • Method: ${tx.paymentMethod}",
                color = BankingTheme.colors.textSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = resolutionNote,
                onValueChange = { resolutionNote = it },
                label = { Text("Resolution Audit Note", fontSize = 11.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BankingTheme.colors.primaryAccent,
                    unfocusedBorderColor = BankingTheme.colors.border
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onApproveRefund(resolutionNote) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("admin_approve_refund_${tx.id}")
                ) {
                    Text("Approve Refund", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { onRejectDispute(resolutionNote) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("admin_reject_dispute_${tx.id}")
                ) {
                    Text("Reject Claim", fontSize = 11.sp, color = BankingTheme.colors.textSecondary)
                }

                IconButton(
                    onClick = onExpunge,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                        .testTag("admin_expunge_tx_${tx.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Expunge Record",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DESK 6: CARDS OVERSIGHT
// -------------------------------------------------------------

@Composable
fun AdminCardsDesk(
    cards: List<CardEntity>,
    onOverrideCard: (cardId: String, isFrozen: Boolean, limit: Double) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Master Card Portfolio Controls",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = BankingTheme.colors.textPrimary,
            fontSize = 15.sp
        )
        Text(
            text = "Direct administrative overrides for card freeze states, limits, and PAN access",
            style = MaterialTheme.typography.bodySmall,
            color = BankingTheme.colors.textSecondary,
            fontSize = 11.5.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        cards.forEach { card ->
            AdminCardManagementRow(card = card, onOverrideCard = onOverrideCard)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun AdminCardManagementRow(
    card: CardEntity,
    onOverrideCard: (cardId: String, isFrozen: Boolean, limit: Double) -> Unit
) {
    var isFrozenState by remember(card.isFrozen) { mutableStateOf(card.isFrozen) }
    var currentLimit by remember(card.spendingLimit) { mutableDoubleStateOf(card.spendingLimit) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BankingTheme.colors.surfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${card.bankName} ${card.cardProductName}",
                        fontWeight = FontWeight.Bold,
                        color = BankingTheme.colors.textPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "PAN: •••• ${card.cardNumber.takeLast(4)} • Exp: ${card.expiryDate} • CVV: ${card.cvv}",
                        fontFamily = FontFamily.Monospace,
                        color = BankingTheme.colors.primaryAccent,
                        fontSize = 11.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isFrozenState) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isFrozenState) "FROZEN" else "ACTIVE",
                        color = if (isFrozenState) Color(0xFFEF4444) else Color(0xFF10B981),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Spending Limit",
                        color = BankingTheme.colors.textSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "$${"%,.0f".format(currentLimit)} / mo",
                        fontWeight = FontWeight.Bold,
                        color = BankingTheme.colors.textPrimary,
                        fontSize = 13.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isFrozenState) "Locked" else "Unlocked",
                        color = BankingTheme.colors.textSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Switch(
                        checked = !isFrozenState,
                        onCheckedChange = { active ->
                            isFrozenState = !active
                            onOverrideCard(card.id, !active, currentLimit)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = BankingTheme.colors.primaryAccent
                        ),
                        modifier = Modifier.testTag("admin_toggle_card_${card.id}")
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DESK 7: CUSTOMER CARE DESK
// -------------------------------------------------------------

@Composable
fun AdminCustomerCareDesk(
    tickets: List<AdminTicket>,
    accounts: List<AccountEntity>,
    onSelectTicket: (AdminTicket) -> Unit,
    onReplyTicket: (ticketId: String, replyMessage: String, newStatus: AdminTicketStatus) -> Unit,
    onCreditCourtesyCompensation: (ticketId: String, accountId: String, amount: Double, reason: String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Back-Office Customer Care Queue",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BankingTheme.colors.textPrimary,
                fontSize = 15.sp
            )
            Text(
                text = "${tickets.size} Total Cases",
                style = MaterialTheme.typography.labelSmall,
                color = BankingTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        tickets.forEach { ticket ->
            AdminTicketCard(
                ticket = ticket,
                accounts = accounts,
                onReply = { msg, st -> onReplyTicket(ticket.id, msg, st) },
                onCourtesyCredit = { accId, amt, rsn ->
                    onCreditCourtesyCompensation(ticket.id, accId, amt, rsn)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun AdminTicketCard(
    ticket: AdminTicket,
    accounts: List<AccountEntity>,
    onReply: (String, AdminTicketStatus) -> Unit,
    onCourtesyCredit: (String, Double, String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BankingTheme.colors.surfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(16.dp))
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (ticket.priority) {
                                    AdminTicketPriority.CRITICAL -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                    AdminTicketPriority.HIGH -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                    else -> Color(0xFF6366F1).copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = ticket.priority.label,
                            color = when (ticket.priority) {
                                AdminTicketPriority.CRITICAL -> Color(0xFFEF4444)
                                AdminTicketPriority.HIGH -> Color(0xFFF59E0B)
                                else -> Color(0xFF6366F1)
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = ticket.category,
                        color = BankingTheme.colors.textSecondary,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (ticket.status == AdminTicketStatus.RESOLVED) Color(0xFF10B981).copy(alpha = 0.2f)
                            else BankingTheme.colors.primaryAccent.copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = ticket.status.label,
                        color = if (ticket.status == AdminTicketStatus.RESOLVED) Color(0xFF10B981) else BankingTheme.colors.primaryAccent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${ticket.id}: ${ticket.subject}",
                fontWeight = FontWeight.Bold,
                color = BankingTheme.colors.textPrimary,
                fontSize = 13.5.sp
            )

            Text(
                text = "Client: ${ticket.customerName} • Account: ${ticket.accountNumber} • Assigned: ${ticket.assignedAgent}",
                color = BankingTheme.colors.textSecondary,
                fontSize = 10.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = ticket.description,
                color = BankingTheme.colors.textPrimary.copy(alpha = 0.85f),
                fontSize = 12.sp,
                maxLines = if (isExpanded) 10 else 2,
                overflow = TextOverflow.Ellipsis
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        label = { Text("Customer Care Reply", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (replyText.isNotBlank()) {
                                    onReply(replyText, AdminTicketStatus.RESOLVED)
                                    replyText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Resolve & Close", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val targetAcc = accounts.firstOrNull()?.id ?: "acc_checking"
                                onCourtesyCredit(targetAcc, 250.0, "Resolution Goodwill Credit for ${ticket.id}")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BankingTheme.colors.primaryAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+$250 Goodwill Credit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DESK 8: TELEMETRY
// -------------------------------------------------------------

@Composable
fun AdminTelemetryDesk(gateways: List<SystemGatewayStatus>) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Central Banking Gateways & Nodes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BankingTheme.colors.textPrimary,
                fontSize = 15.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "All Nodes Synchronized",
                    color = Color(0xFF10B981),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        gateways.forEach { gw ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BankingTheme.colors.surfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = gw.name, fontWeight = FontWeight.Bold, color = BankingTheme.colors.textPrimary, fontSize = 13.5.sp)
                        Text(text = "Protocol: ${gw.protocol} • Volume: ${gw.dailyVolume}", color = BankingTheme.colors.textSecondary, fontSize = 10.5.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "${gw.latencyMs}ms • ${gw.uptimePct}%", color = Color(0xFF10B981), fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DESK 9: IMMUTABLE AUDIT LOG (COMPLIANCE CHAIN)
// -------------------------------------------------------------

@Composable
fun AdminImmutableAuditLogDesk(auditLogs: List<AdminAuditLogEntity>) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredLogs = remember(auditLogs, selectedFilter) {
        if (selectedFilter == "ALL") auditLogs
        else auditLogs.filter { it.actionCategory == selectedFilter }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Immutable Compliance Audit Trail",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BankingTheme.colors.textPrimary,
                    fontSize = 15.sp
                )
                Text(
                    text = "Tamper-Evident SHA-256 Chained Ledger (${auditLogs.size} Blocks)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE5C378),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category filter chips
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "ALL" to "All Logs",
                "FINANCIAL_TRANSACTION" to "Transactions",
                "FINANCIAL_LEDGER" to "Ledger Overrides",
                "CENTRAL_WIRE" to "Wires",
                "CUSTOMER_COMMUNICATION" to "Emails",
                "CARD_SECURITY" to "Cards",
                "CONFIGURATION_EDIT" to "Configs"
            ).forEach { (cat, label) ->
                FilterChip(
                    selected = selectedFilter == cat,
                    onClick = { selectedFilter = cat },
                    label = { Text(label, fontSize = 10.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BankingTheme.colors.primaryAccent,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredLogs.isEmpty()) {
            Text("No audit records matching this filter.", color = BankingTheme.colors.textSecondary, fontSize = 12.sp)
        } else {
            filteredLogs.forEach { log ->
                AdminImmutableAuditLogRow(log = log)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AdminImmutableAuditLogRow(log: AdminAuditLogEntity) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BankingTheme.colors.surfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE5C378).copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "#${"%04d".format(log.sequenceNumber)}",
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFE5C378),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = log.actionType,
                        fontWeight = FontWeight.Bold,
                        color = BankingTheme.colors.textPrimary,
                        fontSize = 11.5.sp
                    )
                }

                Text(
                    text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(log.timestamp)),
                    fontFamily = FontFamily.Monospace,
                    color = BankingTheme.colors.textMuted,
                    fontSize = 9.5.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = log.details,
                color = BankingTheme.colors.textPrimary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Actor: ${log.operatorEmail}",
                    color = BankingTheme.colors.textSecondary,
                    fontSize = 9.5.sp
                )
                if (log.amount != null && log.amount > 0.0) {
                    Text(
                        text = "$${"%,.2f".format(log.amount)}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        fontSize = 11.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Cryptographic chain hash
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(BankingTheme.colors.background)
                    .padding(6.dp)
            ) {
                Column {
                    Text(
                        text = "SHA-256: ${log.cryptographicHash.take(24)}...",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF10B981),
                        fontSize = 9.sp
                    )
                    Text(
                        text = "Prev Hash: ${log.prevHash.take(24)}...",
                        fontFamily = FontFamily.Monospace,
                        color = BankingTheme.colors.textMuted,
                        fontSize = 8.5.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// MODALS
// -------------------------------------------------------------

@Composable
fun AdminBalanceAdjustSheet(
    account: AccountEntity,
    onDismiss: () -> Unit,
    onConfirmAdjust: (amount: Double, isCredit: Boolean, reason: String, memo: String) -> Unit
) {
    var isCredit by remember { mutableStateOf(true) }
    var amountText by remember { mutableStateOf("") }
    var selectedReason by remember { mutableStateOf("DISPUTE_PROVISIONAL_CREDIT") }
    var memoText by remember { mutableStateOf("Customer Care Executive Balance Override") }

    val reasons = listOf(
        "DISPUTE_PROVISIONAL_CREDIT",
        "WIRE_REVERSAL_ADJUSTMENT",
        "VIP_COMPENSATION_BONUS",
        "AUDIT_RECONCILIATION_CORRECTION",
        "TAX_WITHHOLDING_REMEDY"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Back-Office Ledger Adjustment",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = BankingTheme.colors.textPrimary,
            fontSize = 18.sp
        )
        Text(
            text = "Target Account: ${account.name} (•• ${account.accountNumber.takeLast(4)})",
            color = BankingTheme.colors.textSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { isCredit = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCredit) Color(0xFF10B981) else BankingTheme.colors.surfaceCard,
                    contentColor = if (isCredit) Color.Black else BankingTheme.colors.textSecondary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).testTag("admin_adjust_credit_btn")
            ) {
                Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Credit Account (+)")
            }

            Button(
                onClick = { isCredit = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isCredit) Color(0xFFEF4444) else BankingTheme.colors.surfaceCard,
                    contentColor = if (!isCredit) Color.White else BankingTheme.colors.textSecondary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).testTag("admin_adjust_debit_btn")
            ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Debit Account (-)")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
            label = { Text("Adjustment Amount ($)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().testTag("admin_adjust_amount_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = memoText,
            onValueChange = { memoText = it },
            label = { Text("Audit Trail Memo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("admin_adjust_memo_input")
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                val amt = amountText.toDoubleOrNull() ?: 0.0
                if (amt > 0.0) {
                    onConfirmAdjust(amt, isCredit, selectedReason, memoText)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCredit) Color(0xFF10B981) else Color(0xFFEF4444),
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("admin_confirm_adjust_btn")
        ) {
            Text("Commit Adjustment to Central Ledger", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun AdminAccountConfigSheet(
    account: AccountEntity,
    onDismiss: () -> Unit,
    onConfirmConfig: (name: String, apy: Double, routing: String) -> Unit
) {
    var name by remember { mutableStateOf(account.name) }
    var apyText by remember { mutableStateOf(account.apy.toString()) }
    var routing by remember { mutableStateOf(account.routingNumber) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Edit Account Configuration & Yield",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = BankingTheme.colors.textPrimary,
            fontSize = 18.sp
        )
        Text(
            text = "Account: ${account.accountNumber} • ${account.type.name}",
            color = BankingTheme.colors.textSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Account Display Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = apyText,
            onValueChange = { apyText = it.filter { ch -> ch.isDigit() || ch == '.' } },
            label = { Text("Interest Rate / APY (%)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = routing,
            onValueChange = { routing = it },
            label = { Text("Clearing Routing Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                val apy = apyText.toDoubleOrNull() ?: account.apy
                onConfirmConfig(name, apy, routing)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = BankingTheme.colors.primaryAccent,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Update Account Configuration", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun AdminWireInjectSheet(
    accounts: List<AccountEntity>,
    onDismiss: () -> Unit,
    onConfirmWire: (accId: String, sender: String, bank: String, routing: String, amount: Double, rail: String, memo: String) -> Unit
) {
    var selectedAccId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: "") }
    var senderName by remember { mutableStateOf("Morgan Stanley Wealth Management") }
    var senderBank by remember { mutableStateOf("Morgan Stanley NY / Geneva Clearing") }
    var routingNumber by remember { mutableStateOf("021000089") }
    var amountText by remember { mutableStateOf("75000") }
    var selectedRail by remember { mutableStateOf("Fedwire RTGS") }
    var memoText by remember { mutableStateOf("Escrow closing tranche wire settlement") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Inject Central Bank Wire Settlement",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = BankingTheme.colors.textPrimary,
            fontSize = 18.sp
        )
        Text(
            text = "Direct RTGS credit to client account with instant notification",
            color = BankingTheme.colors.textSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
            label = { Text("Wire Amount ($)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().testTag("admin_wire_amount_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = senderName,
            onValueChange = { senderName = it },
            label = { Text("Originating Sender / Entity") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = senderBank,
            onValueChange = { senderBank = it },
            label = { Text("Originating Bank") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = memoText,
            onValueChange = { memoText = it },
            label = { Text("Wire Reference Memo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val amt = amountText.toDoubleOrNull() ?: 0.0
                if (amt > 0.0 && selectedAccId.isNotBlank()) {
                    onConfirmWire(selectedAccId, senderName, senderBank, routingNumber, amt, selectedRail, memoText)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF38BDF8),
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("admin_confirm_wire_injection_btn")
        ) {
            Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Inject Wire Immediately", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminBroadcastSheet(
    onDismiss: () -> Unit,
    onBroadcast: (title: String, message: String, priority: AlertType) -> Unit
) {
    var title by remember { mutableStateOf("Official Federal Reserve Liquidity Memo") }
    var message by remember { mutableStateOf("All Fedwire and SWIFT settlements are operating at accelerated clearance times.") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Broadcast Official Back-Office Memo",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = BankingTheme.colors.textPrimary,
            fontSize = 18.sp
        )
        Text(
            text = "Sends a high-priority advisory directly into client notification drawer and top banner",
            color = BankingTheme.colors.textSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Memo Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("admin_broadcast_title_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Advisory Message") },
            modifier = Modifier.fillMaxWidth().testTag("admin_broadcast_msg_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotBlank() && message.isNotBlank()) {
                    onBroadcast(title, message, AlertType.SECURITY)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = BankingTheme.colors.primaryAccent,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("admin_confirm_broadcast_btn")
        ) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Dispatch Memo to Client App", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminEmailDetailSheet(
    email: CustomerEmailEntity,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LEAD PRIVATE BANK",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = BankingTheme.colors.primaryAccent,
                letterSpacing = 1.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = email.deliveryStatus,
                    color = Color(0xFF10B981),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = email.subject,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = BankingTheme.colors.textPrimary,
            fontSize = 16.sp
        )

        Text(
            text = "To: ${email.recipientName} <${email.recipientEmail}> • Ref: ${email.referenceCode}",
            color = BankingTheme.colors.textSecondary,
            fontSize = 11.5.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BankingTheme.colors.surfaceCard)
                .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = email.headline,
                    fontWeight = FontWeight.Bold,
                    color = BankingTheme.colors.textPrimary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = email.bodyText,
                    color = BankingTheme.colors.textPrimary.copy(alpha = 0.9f),
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )
                if (email.amount != null && email.amount > 0.0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Settlement Amount: $${"%,.2f".format(email.amount)}",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF10B981),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Dispatched by: ${email.dispatchedBy} • Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(email.timestamp))}",
            color = BankingTheme.colors.textMuted,
            fontSize = 10.sp
        )
    }
}
