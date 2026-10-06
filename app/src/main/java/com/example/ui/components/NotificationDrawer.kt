package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertDispatchLogEntity
import com.example.data.model.AlertType
import com.example.data.model.CachedTransactionEntity
import com.example.data.model.LinkedAccountMetadataEntity
import com.example.data.model.NotificationAlert
import com.example.ui.theme.BankingTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class DrawerSectionTab {
    MULTI_CHANNEL_ALERTS,
    ROOM_DATABASE_CACHE,
    ACTIVITY_FEED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDrawer(
    notifications: List<NotificationAlert>,
    alertLogs: List<AlertDispatchLogEntity> = emptyList(),
    cachedTransactions: List<CachedTransactionEntity> = emptyList(),
    linkedAccountsMetadata: List<LinkedAccountMetadataEntity> = emptyList(),
    onTriggerTestApproval: () -> Unit = {},
    onTriggerTestSecurity: () -> Unit = {},
    onTriggerTestDeposit: () -> Unit = {},
    onSyncRoomCache: () -> Unit = {},
    onClearAlertLogs: () -> Unit = {},
    sheetState: SheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(DrawerSectionTab.MULTI_CHANNEL_ALERTS) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BankingTheme.colors.backgroundElevated,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BankingTheme.colors.border)
            )
        },
        modifier = modifier.testTag("notification_drawer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "COMMUNICATIONS & ROOM CACHE HUB",
                        style = MaterialTheme.typography.labelSmall,
                        color = ChampagneGold,
                        letterSpacing = 1.2.sp,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-Time Alerts & Local Data",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SurfaceCard)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceCard)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DrawerSectionTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    val label = when (tab) {
                        DrawerSectionTab.MULTI_CHANNEL_ALERTS -> "Alerts (${alertLogs.size})"
                        DrawerSectionTab.ROOM_DATABASE_CACHE -> "Room Cache (${cachedTransactions.size})"
                        DrawerSectionTab.ACTIVITY_FEED -> "Feed"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) ChampagneGold else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) Obsidian950 else TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTab) {
                DrawerSectionTab.MULTI_CHANNEL_ALERTS -> {
                    // TAB 1: RESEND, TWILIO, FIREBASE MULTI-CHANNEL ALERTS
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 1. Pipeline Status Strip
                        item {
                            ClayCard(
                                modifier = Modifier.fillMaxWidth(),
                                variant = ClayVariant.SURFACE,
                                elevation = ClayElevation.LOW,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "MULTI-CHANNEL DELIVERY PIPELINES",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        letterSpacing = 1.sp,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Resend Channel Badge
                                        ChannelStatusCard(
                                            modifier = Modifier.weight(1f),
                                            title = "Resend",
                                            subtitle = "Email / SMTP",
                                            icon = Icons.Default.Email,
                                            tint = Color(0xFF10B981)
                                        )
                                        // Twilio Channel Badge
                                        ChannelStatusCard(
                                            modifier = Modifier.weight(1f),
                                            title = "Twilio",
                                            subtitle = "Cellular SMS",
                                            icon = Icons.Default.Sms,
                                            tint = Color(0xFF06B6D4)
                                        )
                                        // FCM Push Badge
                                        ChannelStatusCard(
                                            modifier = Modifier.weight(1f),
                                            title = "Firebase",
                                            subtitle = "FCM Push",
                                            icon = Icons.Default.NotificationsActive,
                                            tint = ChampagneGold
                                        )
                                    }
                                }
                            }
                        }

                        // 2. Interactive Test Alert Trigger Strip
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SurfaceCard)
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = "TEST REAL-TIME DISPATCH",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ClayButton(
                                        onClick = onTriggerTestApproval,
                                        variant = ClayVariant.GOLD,
                                        elevation = ClayElevation.LOW,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("test_wire_approval_btn")
                                    ) {
                                        Text("Wire Approval", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Obsidian950)
                                    }
                                    ClayButton(
                                        onClick = onTriggerTestSecurity,
                                        variant = ClayVariant.SURFACE,
                                        elevation = ClayElevation.LOW,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("test_security_alert_btn")
                                    ) {
                                        Text("Security Alert", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }
                                    ClayButton(
                                        onClick = onTriggerTestDeposit,
                                        variant = ClayVariant.EMERALD,
                                        elevation = ClayElevation.LOW,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("test_deposit_alert_btn")
                                    ) {
                                        Text("Deposit Notice", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Obsidian950)
                                    }
                                }
                            }
                        }

                        // 3. Live Alert Logs Feed
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "DISPATCH AUDIT TRAIL (${alertLogs.size})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.sp
                                )
                                if (alertLogs.isNotEmpty()) {
                                    Text(
                                        text = "Clear Logs",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.clickable { onClearAlertLogs() }
                                    )
                                }
                            }
                        }

                        if (alertLogs.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SurfaceCard)
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No alerts dispatched yet. Tap any test button above to send real-time alerts.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        } else {
                            items(alertLogs) { log ->
                                val (icon, badgeColor) = when (log.channel) {
                                    "EMAIL_RESEND" -> Pair(Icons.Default.Email, Color(0xFF10B981))
                                    "SMS_TWILIO" -> Pair(Icons.Default.Sms, Color(0xFF06B6D4))
                                    else -> Pair(Icons.Default.NotificationsActive, ChampagneGold)
                                }
                                val timeFormatted = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(log.timestamp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SurfaceCard)
                                        .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(badgeColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = badgeColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = log.eventType.replace("_", " "),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = badgeColor,
                                                fontSize = 10.sp
                                            )
                                            Text(
                                                text = timeFormatted,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = log.subject,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = log.bodySummary,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "To: ${log.recipient.take(24)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary,
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            ClayBadge(
                                                text = log.status,
                                                variant = if (log.status == "DELIVERED") ClayVariant.EMERALD else ClayVariant.SURFACE
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                DrawerSectionTab.ROOM_DATABASE_CACHE -> {
                    // TAB 2: ROOM DATABASE SCHEMA INSPECTOR & LOCAL CACHE
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            ClayCard(
                                modifier = Modifier.fillMaxWidth(),
                                variant = ClayVariant.OBSIDIAN,
                                elevation = ClayElevation.LOW,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "ROOM DATABASE PERSISTENCE",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ChampagneGold,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                            Text(
                                                text = "SQLite Schema: Version 4",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }

                                        ClayButton(
                                            onClick = onSyncRoomCache,
                                            variant = ClayVariant.GOLD,
                                            elevation = ClayElevation.LOW,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("sync_room_cache_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Sync",
                                                tint = Obsidian950,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Sync Cache", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Obsidian950)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        StatCard(
                                            modifier = Modifier.weight(1f),
                                            label = "Cached Txs",
                                            value = "${cachedTransactions.size}",
                                            table = "cached_transactions"
                                        )
                                        StatCard(
                                            modifier = Modifier.weight(1f),
                                            label = "Linked Metadata",
                                            value = "${linkedAccountsMetadata.size}",
                                            table = "linked_accounts_metadata"
                                        )
                                    }
                                }
                            }
                        }

                        // Linked Bank Account Metadata Section
                        item {
                            Text(
                                text = "LINKED BANK ACCOUNT METADATA (LOCAL ROOM)",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        items(linkedAccountsMetadata) { meta ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SurfaceCard)
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalance,
                                            contentDescription = null,
                                            tint = ChampagneGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = meta.accountName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 13.sp
                                        )
                                    }
                                    ClayBadge(
                                        text = meta.verificationTier.replace("_", " "),
                                        variant = ClayVariant.EMERALD
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${meta.fullAccountNumberMasked} • Routing: ${meta.routingNumber} • Transit: ${meta.wireRoutingTransit}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Available: $${"%,.2f".format(meta.availableBalance)} USD",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Limit: $${"%,.0f".format(meta.dailyTransferLimit)}/day",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // Cached Transactions Section
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "CACHED TRANSACTION LEDGER (OFFLINE PERSISTENCE)",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        items(cachedTransactions.take(8)) { tx ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceCard)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = tx.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 12.5.sp
                                    )
                                    Text(
                                        text = "Ref: ${tx.referenceNumber} • Auth: ${tx.authChannelUsed}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    if (tx.requiresApproval) {
                                        Text(
                                            text = "Approval Code: ${tx.approvalCode ?: "PENDING"}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ChampagneGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = (if (tx.amount >= 0) "+" else "") + "$${"%,.2f".format(tx.amount)}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (tx.amount >= 0) EmeraldGreen else Color(0xFFF43F5E),
                                        fontSize = 13.sp
                                    )
                                    ClayBadge(
                                        text = tx.status,
                                        variant = if (tx.status == "COMPLETED" || tx.status == "SETTLED") ClayVariant.EMERALD else ClayVariant.GOLD
                                    )
                                }
                            }
                        }
                    }
                }

                DrawerSectionTab.ACTIVITY_FEED -> {
                    // TAB 3: STANDARD NOTIFICATION FEED
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(notifications) { alert ->
                            val (icon, tint) = when (alert.type) {
                                AlertType.SECURITY -> Pair(Icons.Default.Security, ChampagneGold)
                                AlertType.TRANSACTION -> Pair(Icons.Default.SwapHoriz, EmeraldGreen)
                                AlertType.WEALTH -> Pair(Icons.Default.TrendingUp, EmeraldGreen)
                                AlertType.SYSTEM -> Pair(Icons.Default.Notifications, TextSecondary)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SurfaceCard)
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                                    .padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(tint.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = tint,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = alert.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = alert.timeAgo,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = alert.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelStatusCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.dp, tint.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = tint,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    table: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = ChampagneGold,
                fontSize = 18.sp
            )
            Text(
                text = table,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 8.5.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
