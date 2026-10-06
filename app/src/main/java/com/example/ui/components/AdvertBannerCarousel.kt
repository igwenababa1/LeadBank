package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

data class AdvertBannerItem(
    val id: String,
    val tag: String,
    val headline: String,
    val subtitle: String,
    val ctaText: String,
    val drawableResId: Int?,
    val accentColor: Color,
    val onClickAction: () -> Unit
)

@Composable
fun AdvertBannerCarousel(
    onOpenDeposit: () -> Unit,
    onNavigateCrypto: () -> Unit,
    onUpgradeTier: () -> Unit,
    modifier: Modifier = Modifier
) {
    val banners = remember {
        listOf(
            AdvertBannerItem(
                id = "banner_yield",
                tag = "5.40% NET APY TREASURY SWEEP",
                headline = "Compound Idle Liquidity with 5.40% Yield",
                subtitle = "Automated sweep backed by short-term US Treasury bills. Protected up to $5,000,000 by participating FDIC partner banks.",
                ctaText = "Open 5.40% Vault",
                drawableResId = R.drawable.img_invest_yield_banner,
                accentColor = ChampagneGold,
                onClickAction = onOpenDeposit
            ),
            AdvertBannerItem(
                id = "banner_crypto",
                tag = "INSTITUTIONAL CRYPTO DESK",
                headline = "Spot Swaps: USD to BTC & ETH in Under 8ms",
                subtitle = "Zero execution markup, segregated MPC cold storage, and direct settlement from your private checking accounts.",
                ctaText = "Trade Crypto Desk",
                drawableResId = R.drawable.img_crypto_desk_banner,
                accentColor = Color(0xFF6366F1),
                onClickAction = onNavigateCrypto
            ),
            AdvertBannerItem(
                id = "banner_membership",
                tag = "PRIVATE RESERVE MEMBERSHIP",
                headline = "Laser-Engraved Heavy Tungsten Card",
                subtitle = "3% unlimited global rewards, dedicated 24/7 private banker on WhatsApp, and worldwide VIP airport lounge access.",
                ctaText = "Upgrade Membership",
                drawableResId = null,
                accentColor = EmeraldGreen,
                onClickAction = onUpgradeTier
            )
        )
    }

    var activeIndex by remember { mutableIntStateOf(0) }

    // Auto rotate every 6 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(6000)
            activeIndex = (activeIndex + 1) % banners.size
        }
    }

    val banner = banners[activeIndex]

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.2.dp, banner.accentColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .clickable { banner.onClickAction() }
                .testTag("advert_banner_card")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Background Image if available
                if (banner.drawableResId != null) {
                    Image(
                        painter = painterResource(id = banner.drawableResId),
                        contentDescription = banner.headline,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Dark Obsidian Gradient Scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Obsidian950.copy(alpha = 0.45f),
                                    Obsidian950.copy(alpha = 0.88f),
                                    Obsidian950
                                )
                            )
                        )
                )

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Tag Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(banner.accentColor.copy(alpha = 0.18f))
                            .border(1.dp, banner.accentColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (banner.id) {
                                "banner_yield" -> Icons.Default.TrendingUp
                                "banner_crypto" -> Icons.Default.CurrencyBitcoin
                                else -> Icons.Default.AutoAwesome
                            },
                            contentDescription = null,
                            tint = banner.accentColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = banner.tag,
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = banner.accentColor,
                            fontSize = 9.sp
                        )
                    }

                    // Headline & Subtitle
                    Column {
                        Text(
                            text = banner.headline,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = banner.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 2,
                            lineHeight = 15.sp
                        )
                    }

                    // Action Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = banner.onClickAction,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = banner.accentColor,
                                contentColor = Obsidian950
                            ),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("banner_cta_btn")
                        ) {
                            Text(
                                text = banner.ctaText,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Verified Trust Badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "FDIC Insured • Institutional",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        // Pager Indicator Dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            banners.forEachIndexed { index, _ ->
                val isSelected = index == activeIndex
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .clip(CircleShape)
                        .size(if (isSelected) 18.dp else 6.dp, 6.dp)
                        .background(if (isSelected) ChampagneGold else SurfaceBorder)
                        .clickable { activeIndex = index }
                )
            }
        }
    }
}
