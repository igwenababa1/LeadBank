package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VpnKey
import com.example.ui.components.BankingBackgroundType
import com.example.ui.components.ClayBadge
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayCard
import com.example.ui.components.ClayElevation
import com.example.ui.components.ClayIconButton
import com.example.ui.components.ClaySwitch
import com.example.ui.components.ClayVariant
import com.example.ui.components.claymorphic
import com.example.ui.components.bankingBackground
import com.example.ui.theme.BankingTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FaqItem
import com.example.data.model.PlanTierInfo
import com.example.data.model.TestimonialItem
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SecurityScreen(
    planTiers: List<PlanTierInfo>,
    selectedTierId: String,
    biometricsActive: Boolean,
    fraudShieldActive: Boolean,
    isDarkMode: Boolean = true,
    onToggleBiometrics: () -> Unit,
    onToggleFraudShield: () -> Unit,
    onToggleTheme: () -> Unit = {},
    onOpenExportStatement: () -> Unit = {},
    onSelectTier: (String) -> Unit,
    onOpenCreateCard: () -> Unit,
    onOpenAdminConsole: () -> Unit = {},
    onOpenAlertCenter: () -> Unit = {},
    onTriggerTestApproval: () -> Unit = {},
    onTriggerTestSecurity: () -> Unit = {},
    onTriggerTestDeposit: () -> Unit = {},
    onLockVault: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val faqs = listOf(
        FaqItem(
            "How does the $5,000,000 FDIC sweep coverage operate?",
            "Lead partners with an elite network of program banks. When deposits exceed standard $250,000 limits, excess funds are automatically swept across participating FDIC-insured partner banks, maintaining full single-account liquidity while securing up to $5,000,000 in aggregate federal protection."
        ),
        FaqItem(
            "What are the daily wire transfer limits and cutoff times?",
            "FedNow and SEPA Instant transfers settle 24/7/365 with zero latency. Outgoing SWIFT international wires submitted before 17:00 ET clear same-business-day with zero foreign exchange spread."
        ),
        FaqItem(
            "How does the real-time AI Fraud Shield protect my cards?",
            "Every authorization is evaluated in under 12 milliseconds using localized behavioral signatures and zero-trust heuristic tokens. If anomalous activity is suspected, card tokenization shifts instantly and our private banking concierge verifies with you via secure push."
        ),
        FaqItem(
            "How can I receive my 18g Precision Obsidian Metal card?",
            "Obsidian Metal and Tungsten cards are engraved with laser precision and dispatched via complimentary armored courier within 48 hours of account verification."
        )
    )

    val testimonials = listOf(
        TestimonialItem(
            "Lead replaced three disparate banking apps with one razor-sharp, zero-friction interface. The FedNow integration and 5.15% vault yields are unrivaled.",
            "Elena Rostova",
            "General Partner",
            "Benchmark Capital"
        ),
        TestimonialItem(
            "The obsidian card craftsmanship and instantaneous international wires make this the definitive banking solution for modern founders.",
            "Marcus Chen",
            "Co-Founder & CEO",
            "Valence Systems"
        )
    )

    var expandedFaqIndex by remember { mutableIntStateOf(-1) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .bankingBackground(BankingBackgroundType.SECURITY, BankingTheme.colors.isDark)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "DEFENSE & INFRASTRUCTURE",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.5.sp,
                    color = ChampagneGold,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Security & Concierge",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Live Real-Time Fraud Defense Status Card
        item {
            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("security_defense_card"),
                variant = ClayVariant.SURFACE,
                elevation = ClayElevation.MEDIUM,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .claymorphic(
                                        variant = if (fraudShieldActive) ClayVariant.EMERALD else ClayVariant.CRIMSON,
                                        elevation = ClayElevation.LOW,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (fraudShieldActive) EmeraldGreen else Color(0xFFF43F5E),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "AURA SENTINEL AI SHIELD",
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 1.2.sp,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = if (fraudShieldActive) "Active Protection (0 Anomalies)" else "Protection Paused",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        ClaySwitch(
                            checked = fraudShieldActive,
                            onCheckedChange = { onToggleFraudShield() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = ClayVariant.OBSIDIAN,
                        elevation = ClayElevation.LOW,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Encryption", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                Text("AES-256 GCM", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Zero Liability", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                Text("100% Guaranteed", style = MaterialTheme.typography.titleSmall, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Biometrics", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                Text(if (biometricsActive) "Enforced" else "Disabled", style = MaterialTheme.typography.titleSmall, color = ChampagneGold, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Real-Time Alert Engine (Resend Email • Twilio SMS • Firebase Cloud Messaging)
        item {
            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("security_alert_engine_card"),
                variant = ClayVariant.OBSIDIAN,
                elevation = ClayElevation.MEDIUM,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .claymorphic(variant = ClayVariant.GOLD, elevation = ClayElevation.LOW, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = Obsidian950,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "ALERT & NOTIFICATION ENGINE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ChampagneGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    text = "Resend • Twilio • FCM",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        ClayButton(
                            onClick = onOpenAlertCenter,
                            variant = ClayVariant.SURFACE,
                            elevation = ClayElevation.LOW,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("open_alert_center_btn")
                        ) {
                            Text("Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChampagneGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Real-time institutional dispatches for transaction approvals, biometric security modifications, and deposit settlements across 3 encrypted channels.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Delivery Channels Status Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.12f))
                                .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Resend", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 10.5.sp)
                                }
                                Text("HTML Email", style = MaterialTheme.typography.labelSmall, color = Color(0xFF10B981), fontSize = 9.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF06B6D4).copy(alpha = 0.12f))
                                .border(1.dp, Color(0xFF06B6D4).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Twilio", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 10.5.sp)
                                }
                                Text("Cellular SMS", style = MaterialTheme.typography.labelSmall, color = Color(0xFF06B6D4), fontSize = 9.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ChampagneGold.copy(alpha = 0.12f))
                                .border(1.dp, ChampagneGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Firebase", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 10.5.sp)
                                }
                                Text("FCM Push", style = MaterialTheme.typography.labelSmall, color = ChampagneGold, fontSize = 9.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Test Dispatch Action Buttons
                    Text(
                        text = "INSTANT DISPATCH TEST",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ClayButton(
                            onClick = onTriggerTestApproval,
                            variant = ClayVariant.GOLD,
                            elevation = ClayElevation.LOW,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Wire Approval", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Obsidian950)
                        }
                        ClayButton(
                            onClick = onTriggerTestSecurity,
                            variant = ClayVariant.SURFACE,
                            elevation = ClayElevation.LOW,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Security Alert", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        ClayButton(
                            onClick = onTriggerTestDeposit,
                            variant = ClayVariant.EMERALD,
                            elevation = ClayElevation.LOW,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Deposit Notice", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Obsidian950)
                        }
                    }
                }
            }
        }

        // Biometric & Hardware Controls
        item {
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                variant = ClayVariant.SURFACE,
                elevation = ClayElevation.LOW,
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "ACCESS & CREDENTIAL CONTROLS",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.sp,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .claymorphic(variant = ClayVariant.SURFACE, elevation = ClayElevation.LOW, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Biometric Passkey (FaceID / TouchID)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Prompt on app launch and wire authorization",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        ClaySwitch(
                            checked = biometricsActive,
                            onCheckedChange = { onToggleBiometrics() }
                        )
                    }

                    // Theme Mode Switch Row (Obsidian Dark vs Alabaster Light)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .claymorphic(variant = ClayVariant.SURFACE, elevation = ClayElevation.LOW, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isDarkMode) "Obsidian Dark Mode" else "Alabaster Light Mode",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "High-contrast financial interface theme",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        ClaySwitch(
                            checked = isDarkMode,
                            onCheckedChange = { onToggleTheme() }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .claymorphic(variant = ClayVariant.SURFACE, elevation = ClayElevation.LOW, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Instant Disposable Burner Card",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Card number regenerates after each transaction",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        ClayButton(
                            onClick = onOpenCreateCard,
                            variant = ClayVariant.GOLD,
                            elevation = ClayElevation.LOW,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Create", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Obsidian950, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    // Lock Vault Now (Biometric Protection Verification)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .claymorphic(variant = ClayVariant.CRIMSON, elevation = ClayElevation.LOW, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFFF43F5E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Lock Vault Immediately",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Arm biometric lock and return to login page",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        ClayButton(
                            onClick = onLockVault,
                            variant = ClayVariant.SURFACE,
                            elevation = ClayElevation.LOW,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("lock_vault_security_btn")
                        ) {
                            Text("Lock", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF43F5E), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }

        // Superior Admin Back-Office Console Access
        item {
            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("security_admin_console_card"),
                variant = ClayVariant.OBSIDIAN,
                elevation = ClayElevation.HIGH,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .claymorphic(variant = ClayVariant.SURFACE, elevation = ClayElevation.LOW, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "SUPERIOR ADMIN BACK-OFFICE",
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 1.2.sp,
                                    color = ChampagneGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Central Banking Operations Desk",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        ClayBadge(
                            text = "LEVEL 5",
                            variant = ClayVariant.EMERALD
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Operate core banking ledgers, execute central bank wire injection, arbitrate disputes, override card controls, manage customer care tickets, and inspect cryptographic audit trails.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ClayButton(
                        onClick = onOpenAdminConsole,
                        variant = ClayVariant.GOLD,
                        elevation = ClayElevation.MEDIUM,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("security_launch_admin_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Obsidian950
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Launch Superior Admin Portal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Obsidian950
                            )
                        }
                    }
                }
            }
        }

        // Active Hardware Sessions
        item {
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                variant = ClayVariant.SURFACE,
                elevation = ClayElevation.LOW,
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ACTIVE DEVICE SESSIONS",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.sp,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )

                    SessionItem(
                        icon = Icons.Default.PhoneIphone,
                        deviceName = "iPhone 16 Pro Max",
                        location = "San Francisco, CA • This Device",
                        isActive = true
                    )

                    SessionItem(
                        icon = Icons.Default.Laptop,
                        deviceName = "MacBook Pro 16\" (M3 Max)",
                        location = "San Francisco, CA • Active 14m ago",
                        isActive = false
                    )

                    SessionItem(
                        icon = Icons.Default.DeviceHub,
                        deviceName = "iPad Pro 13\" (M4)",
                        location = "Zurich, Switzerland • Active 2d ago",
                        isActive = false
                    )
                }
            }
        }

        // Certified Financial Statements & Audit Export Card
        item {
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                variant = ClayVariant.SURFACE,
                elevation = ClayElevation.LOW,
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .claymorphic(variant = ClayVariant.SURFACE, elevation = ClayElevation.LOW, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Certified Financial Statements",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Audited PDF Statements & CSV Spreadsheets",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Generate certified statements with FDIC sweep coverage schedule, verified balance history, and cryptographic SHA-256 ledger seals.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ClayButton(
                        onClick = onOpenExportStatement,
                        variant = ClayVariant.GOLD,
                        elevation = ClayElevation.MEDIUM,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("security_export_statement_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Obsidian950
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Open Statement & Export Center",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Obsidian950
                            )
                        }
                    }
                }
            }
        }

        // Membership Tiers Comparison
        item {
            Column {
                Text(
                    text = "MEMBERSHIP TIERS & PRIVILEGES",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.2.sp,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    planTiers.forEach { tier ->
                        val isSelected = tier.id == selectedTierId
                        ClayCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("plan_tier_${tier.id}"),
                            variant = if (isSelected) ClayVariant.GOLD else ClayVariant.SURFACE,
                            elevation = if (isSelected) ClayElevation.MEDIUM else ClayElevation.LOW,
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = tier.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Obsidian950 else TextPrimary
                                        )
                                        Text(
                                            text = tier.cardMaterial,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) Obsidian950.copy(alpha = 0.7f) else TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }

                                    Text(
                                        text = tier.priceMonthly,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Obsidian950 else TextPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = tier.tagline,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) Obsidian950.copy(alpha = 0.8f) else TextSecondary,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Features list
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    tier.features.forEach { feat ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = if (isSelected) Obsidian950 else EmeraldGreen,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = feat,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isSelected) Obsidian950 else TextPrimary,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                ClayButton(
                                    onClick = { onSelectTier(tier.id) },
                                    variant = if (isSelected) ClayVariant.OBSIDIAN else ClayVariant.GOLD,
                                    elevation = ClayElevation.LOW,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isSelected) "Active Tier" else "Select ${tier.name}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Obsidian950,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Testimonials Section
        item {
            Column {
                Text(
                    text = "PRIVATE CLIENT TESTIMONIALS",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.2.sp,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    testimonials.forEach { t ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceCard)
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Row {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = ChampagneGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "\"${t.quote}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "${t.author} • ${t.role}, ${t.company}",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // FAQ Accordion
        item {
            Column {
                Text(
                    text = "FREQUENTLY ASKED QUESTIONS",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.2.sp,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    faqs.forEachIndexed { index, faq ->
                        val isExpanded = expandedFaqIndex == index
                        ClayCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedFaqIndex = if (isExpanded) -1 else index
                                },
                            variant = ClayVariant.SURFACE,
                            elevation = ClayElevation.LOW,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = faq.question,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f),
                                        fontSize = 13.sp
                                    )
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = faq.answer,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Institutional Legal Footer & Disclosures
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF090C12))
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(18.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "LEAD PRIVATE BANKING N.A.",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 2.sp,
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = "Banking services provided by Lead Bank and partner institutions, Members FDIC. The Lead Metal Debit and Charge Cards are issued pursuant to licenses from Visa U.S.A. Inc. and Mastercard International Incorporated. Deposits are FDIC-insured up to $5,000,000 via our sweep deposit allocation program.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
                Text(
                    text = "SOC-2 Type II Certified • PCI-DSS Level 1 Compliant • 256-Bit TLS End-to-End Encryption • © 2026 Lead Capital Technologies Inc.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary.copy(alpha = 0.5f),
                    fontSize = 9.sp
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun SessionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    deviceName: String,
    location: String,
    isActive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(SurfaceCardElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isActive) EmeraldGreen else TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = deviceName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 13.sp
                )
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        if (isActive) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(EmeraldGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "ONLINE",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                )
            }
        }
    }
}
