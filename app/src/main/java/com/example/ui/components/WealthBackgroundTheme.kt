package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

/**
 * 7 Realistic Luxury Banking Background Options for Total Aggregated Wealth
 */
data class WealthBackgroundOption(
    val id: String,
    val name: String,
    val descriptor: String,
    @DrawableRes val drawableRes: Int,
    val accentColor: Color,
    val borderColors: List<Color>
)

val REALISTIC_WEALTH_BACKGROUNDS = listOf(
    WealthBackgroundOption(
        id = "gold_vault",
        name = "Gold Bullion",
        descriptor = "24K Titanium Vault",
        drawableRes = R.drawable.bg_vault_gold_1790889762793,
        accentColor = ChampagneGold,
        borderColors = listOf(ChampagneGold.copy(alpha = 0.85f), Color(0xFF58451D), ChampagneGold.copy(alpha = 0.45f))
    ),
    WealthBackgroundOption(
        id = "swiss_alpine",
        name = "Swiss Private",
        descriptor = "Zurich Platinum Marble",
        drawableRes = R.drawable.bg_swiss_vault_1790889776583,
        accentColor = Color(0xFF60A5FA),
        borderColors = listOf(Color(0xFF93C5FD), Color(0xFF1E3A8A), Color(0xFF60A5FA).copy(alpha = 0.5f))
    ),
    WealthBackgroundOption(
        id = "emerald_reserve",
        name = "Fed Emerald",
        descriptor = "Federal Reserve Guilloche",
        drawableRes = R.drawable.bg_emerald_res_1790889786944,
        accentColor = EmeraldGreen,
        borderColors = listOf(EmeraldGreen, Color(0xFF064E3B), EmeraldGreen.copy(alpha = 0.5f))
    ),
    WealthBackgroundOption(
        id = "manhattan_night",
        name = "Manhattan",
        descriptor = "Wall Street Twilight",
        drawableRes = R.drawable.bg_wall_street_1790889800687,
        accentColor = Color(0xFFFBBF24),
        borderColors = listOf(Color(0xFFFCD34D), Color(0xFF451A03), Color(0xFFFBBF24).copy(alpha = 0.5f))
    ),
    WealthBackgroundOption(
        id = "carbon_plat",
        name = "Carbon Mesh",
        descriptor = "High-Security Titanium",
        drawableRes = R.drawable.bg_carbon_plat_1790889814222,
        accentColor = Color(0xFFE2E8F0),
        borderColors = listOf(Color(0xFFF1F5F9), Color(0xFF334155), Color(0xFF94A3B8).copy(alpha = 0.5f))
    ),
    WealthBackgroundOption(
        id = "monaco_royal",
        name = "Monaco Royal",
        descriptor = "Bespoke Amethyst Velvet",
        drawableRes = R.drawable.bg_monaco_royal_1790889827350,
        accentColor = Color(0xFFF472B6),
        borderColors = listOf(Color(0xFFF472B6), Color(0xFF4C0519), ChampagneGold.copy(alpha = 0.6f))
    ),
    WealthBackgroundOption(
        id = "quantum_btc",
        name = "Quantum BTC",
        descriptor = "Cryptographic Blockchain",
        drawableRes = R.drawable.bg_quantum_btc_1790889838521,
        accentColor = Color(0xFFF7931A),
        borderColors = listOf(Color(0xFFF7931A), Color(0xFF78350F), Color(0xFFFBBF24).copy(alpha = 0.6f))
    )
)

/**
 * Tiny, High-Efficiency Background Selector Row for Total Aggregated Wealth
 * Shows 7 realistic banking backgrounds resized to tiny preview chips
 */
@Composable
fun WealthBackgroundMiniSelector(
    selectedId: String,
    onSelectBackground: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Wallpaper,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "VAULT AMBIENCE (7 REALISTIC BANKING BACKGROUNDS)",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGold
                )
            }

            val currentOption = REALISTIC_WEALTH_BACKGROUNDS.find { it.id == selectedId } ?: REALISTIC_WEALTH_BACKGROUNDS.first()
            Text(
                text = currentOption.name,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = currentOption.accentColor
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        // Tiny Horizontal Thumbnail Carousel (Resized to tiny)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            REALISTIC_WEALTH_BACKGROUNDS.forEach { bg ->
                val isSelected = bg.id == selectedId

                Box(
                    modifier = Modifier
                        .size(width = 72.dp, height = 44.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) bg.accentColor else Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(9.dp)
                        )
                        .clickable { onSelectBackground(bg.id) }
                        .testTag("wealth_bg_chip_${bg.id}")
                ) {
                    // Tiny Resized Background Image
                    Image(
                        painter = painterResource(id = bg.drawableRes),
                        contentDescription = bg.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Subtle Dark Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = if (isSelected) 0.2f else 0.45f),
                                        Color.Black.copy(alpha = 0.75f)
                                    )
                                )
                            )
                    )

                    // Text & Selected Check
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp, vertical = 3.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(bg.accentColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(9.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = bg.name,
                            color = if (isSelected) Color.White else Color(0xFFD1D5DB),
                            fontSize = 8.5.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
