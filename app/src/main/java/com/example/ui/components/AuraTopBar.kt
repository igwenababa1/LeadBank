package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AccountEntity
import com.example.data.model.SupabaseSyncState
import com.example.ui.theme.BankingTheme

@Composable
fun LeadTopBar(
    accounts: List<AccountEntity>,
    selectedAccountId: String?,
    unreadNotificationCount: Int,
    supabaseSyncState: SupabaseSyncState? = null,
    isDarkMode: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onOpenExportStatement: () -> Unit = {},
    onToggleNetwork: (Boolean) -> Unit = {},
    onTriggerSync: () -> Unit = {},
    onAccountSelect: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onSecurityShieldClick: () -> Unit,
    onAdminClick: () -> Unit = {},
    onLockVault: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isAccountMenuOpen by remember { mutableStateOf(false) }
    var isSyncMenuOpen by remember { mutableStateOf(false) }
    val currentAccount = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BankingTheme.colors.backgroundElevated)
            .border(
                width = 0.5.dp,
                color = BankingTheme.colors.border,
                shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TOP LEFT: Bank Crest & Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("lead_bank_logo_container")
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            if (isDarkMode) listOf(Color(0xFF2E2618), Color(0xFF0D111A))
                            else listOf(Color(0xFFFEF3C7), Color(0xFFFFFFFF))
                        )
                    )
                    .border(1.2.dp, BankingTheme.colors.primaryAccent.copy(alpha = 0.7f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_lead_logo),
                    contentDescription = "Lead Bank Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "LEAD",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        color = BankingTheme.colors.primaryAccent,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "FDIC Protected",
                        tint = BankingTheme.colors.primaryAccent,
                        modifier = Modifier.size(13.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "PRIVATE BANK",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.5.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = BankingTheme.colors.textSecondary
                    )
                    Text(text = "•", color = BankingTheme.colors.textMuted, fontSize = 8.sp)
                    Text(
                        text = "FDIC $5M",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = BankingTheme.colors.success
                    )
                    Text(text = "•", color = BankingTheme.colors.textMuted, fontSize = 8.sp)
                    Text(
                        text = "FedNow 24/7",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.5.sp,
                        color = BankingTheme.colors.textSecondary
                    )
                }
            }
        }

        // TOP RIGHT: Actions Row (Sync Pill + Account Switcher + Statement Export + Theme Toggle + Notifications)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Account Switcher Pill (3D Clay Pill)
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .claymorphic(
                            variant = ClayVariant.SURFACE,
                            elevation = ClayElevation.LOW,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { isAccountMenuOpen = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("topbar_account_selector")
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(BankingTheme.colors.success)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentAccount?.name?.replace("Lead ", "") ?: "Checking",
                        style = MaterialTheme.typography.labelSmall,
                        color = BankingTheme.colors.textPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Switch Account",
                        tint = BankingTheme.colors.textSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                }

                DropdownMenu(
                    expanded = isAccountMenuOpen,
                    onDismissRequest = { isAccountMenuOpen = false },
                    modifier = Modifier.background(BankingTheme.colors.surfaceCardElevated)
                ) {
                    accounts.forEach { acc ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = acc.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (acc.id == selectedAccountId) BankingTheme.colors.primaryAccent else BankingTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = "$${"%,.2f".format(acc.balance)} • ${acc.type.badge}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BankingTheme.colors.textSecondary
                                    )
                                }
                            },
                            onClick = {
                                onAccountSelect(acc.id)
                                isAccountMenuOpen = false
                            },
                            modifier = Modifier.testTag("account_item_${acc.id}")
                        )
                    }
                }
            }

            // Statement Export Quick Button (3D Clay Button)
            ClayIconButton(
                onClick = onOpenExportStatement,
                icon = Icons.Default.Description,
                contentDescription = "Export Statement",
                variant = ClayVariant.SURFACE,
                elevation = ClayElevation.LOW,
                size = 36.dp,
                modifier = Modifier.testTag("topbar_statement_export_btn")
            )

            // Superior Admin Portal Quick Button (3D Clay Button)
            ClayIconButton(
                onClick = onAdminClick,
                icon = Icons.Default.Shield,
                contentDescription = "Superior Admin Portal",
                variant = ClayVariant.OBSIDIAN,
                elevation = ClayElevation.LOW,
                size = 36.dp,
                modifier = Modifier.testTag("topbar_admin_console_btn")
            )

            // Theme Toggle Button (Dark / Light)
            ClayIconButton(
                onClick = onToggleTheme,
                icon = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                variant = ClayVariant.SURFACE,
                elevation = ClayElevation.LOW,
                size = 36.dp,
                modifier = Modifier.testTag("theme_toggle_btn")
            )

            // Lock Vault Button (Instant Biometric Re-authentication)
            ClayIconButton(
                onClick = onLockVault,
                icon = Icons.Default.Lock,
                contentDescription = "Lock Vault",
                variant = ClayVariant.SURFACE,
                elevation = ClayElevation.LOW,
                size = 36.dp,
                modifier = Modifier.testTag("topbar_lock_vault_btn")
            )

            // Notification Bell with Badge
            Box {
                ClayIconButton(
                    onClick = onNotificationsClick,
                    icon = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    variant = ClayVariant.SURFACE,
                    elevation = ClayElevation.LOW,
                    size = 36.dp,
                    modifier = Modifier.testTag("notification_bell_btn")
                )
                if (unreadNotificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(9.dp)
                            .claymorphic(variant = ClayVariant.GOLD, elevation = ClayElevation.LOW, shape = CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun AuraTopBar(
    accounts: List<AccountEntity>,
    selectedAccountId: String?,
    unreadNotificationCount: Int,
    onAccountSelect: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onSecurityShieldClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LeadTopBar(
        accounts = accounts,
        selectedAccountId = selectedAccountId,
        unreadNotificationCount = unreadNotificationCount,
        onAccountSelect = onAccountSelect,
        onNotificationsClick = onNotificationsClick,
        onSecurityShieldClick = onSecurityShieldClick,
        modifier = modifier
    )
}
