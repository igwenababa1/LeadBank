package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayCard
import com.example.ui.components.ClayElevation
import com.example.ui.components.ClayVariant
import com.example.ui.theme.BankingTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class WelcomeFeatureItem(
    val title: String,
    val subtitle: String,
    val metric: String,
    val icon: ImageVector
)

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onQuickDemoAccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_ambient")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_pulse"
    )

    val features = listOf(
        WelcomeFeatureItem(
            title = "FedNow RTGS Wire Settlement",
            subtitle = "Direct Federal Reserve clearance in sub-8ms",
            metric = "Instant 24/7",
            icon = Icons.Default.Bolt
        ),
        WelcomeFeatureItem(
            title = "5.15% APY High-Yield Vaults",
            subtitle = "Compounded daily with FDIC insured multi-bank sweep",
            metric = "5.15% APY",
            icon = Icons.Default.TrendingUp
        ),
        WelcomeFeatureItem(
            title = "Institutional Digital Assets",
            subtitle = "Direct spot execution with MPC cold custody",
            metric = "Zero Markup",
            icon = Icons.Default.CurrencyBitcoin
        ),
        WelcomeFeatureItem(
            title = "Official Bank Cards & Titanium",
            subtitle = "Uncapped liquidity with global concierge concierge",
            metric = "VIP Black Tier",
            icon = Icons.Default.CreditCard
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BankingTheme.colors.background)
    ) {
        // Subtle Realistic Banking Background Image
        Image(
            painter = painterResource(id = R.drawable.bg_vault_gold_1790889762793),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Glass Scrim Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        if (BankingTheme.colors.isDark) {
                            listOf(
                                Color(0xFF07090E).copy(alpha = 0.82f),
                                Color(0xFF0D111A).copy(alpha = 0.90f),
                                Color(0xFF05070A).copy(alpha = 0.97f)
                            )
                        } else {
                            listOf(
                                Color(0xFFFFFFFF).copy(alpha = 0.85f),
                                Color(0xFFF8FAFC).copy(alpha = 0.92f),
                                Color(0xFFF1F5F9).copy(alpha = 0.98f)
                            )
                        }
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Brand Monogram Emblem with breathing gold glow
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(ChampagneGold.copy(alpha = pulseAlpha * 0.4f), Color.Transparent)
                        )
                    )
                    .border(
                        2.dp,
                        Brush.linearGradient(listOf(ChampagneGold, Color(0xFF9E7C30))),
                        CircleShape
                    )
                    .testTag("welcome_brand_emblem"),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_lead_logo),
                    contentDescription = "Lead Private Banking Emblem",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-header Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ChampagneGold.copy(alpha = 0.15f))
                    .border(1.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "LEAD PRIVATE BANKING • MEMBER FDIC",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.5.sp,
                    letterSpacing = 1.3.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Headline
            Text(
                text = "Private Wealth,\nSovereign Autonomy.",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
                lineHeight = 38.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Experience next-generation private banking terminal built for institutional wealth, high-frequency liquidity, and multi-bank custody.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Feature Showcase Cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                features.forEach { item ->
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = ClayVariant.SURFACE,
                        elevation = ClayElevation.LOW,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(BankingTheme.colors.backgroundElevated)
                                        .border(1.dp, ChampagneGold.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = ChampagneGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = item.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 10.5.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmeraldGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = item.metric,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // PRIMARY CALL TO ACTION: SIGN IN / ENTER LOGIN SCREEN
            ClayButton(
                onClick = onNavigateToLogin,
                variant = ClayVariant.GOLD,
                elevation = ClayElevation.HIGH,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welcome_sign_in_button")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Obsidian950,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign In to Sovereign Vault",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Obsidian950,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Obsidian950,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SECONDARY QUICK DEMO ENTRANCE (One-tap Evaluator Access)
            Button(
                onClick = onQuickDemoAccess,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BankingTheme.colors.surfaceCard,
                    contentColor = ChampagneGold
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .testTag("welcome_quick_demo_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Instant 1-Tap Client Access (Demo)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer Regulatory Clearance Notice
            Text(
                text = "Regulated by the Federal Reserve • 256-Bit Hardware Keystore • End-to-End Cryptographic Clearing",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary.copy(alpha = 0.7f),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }
}
