package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CardEntity
import com.example.data.model.CardNetwork
import com.example.data.model.CardTheme
import com.example.ui.theme.CardMetalChampagne
import com.example.ui.theme.CardMetalEmerald
import com.example.ui.theme.CardMetalObsidian
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

/**
 * Highly realistic, interactive 3D metal bank card with:
 * - Real professional bank card photographic background textures
 * - Signature "Flame" EMV smart chip with curved isolation trenches & dynamic gold sheen
 * - 3D stamped embossed card numbers and foil typography
 * - Optical Variable Device (OVD) rainbow security hologram
 * - High-coercivity magnetic stripe, tamper-evident signature panel & security CVV
 * - Continuous ambient metallic light glare simulation
 */
@Composable
fun Card3DView(
    card: CardEntity,
    isFlipped: Boolean,
    showDetails: Boolean,
    onFlipClick: () -> Unit,
    onToggleDetails: () -> Unit,
    onToggleFreeze: () -> Unit,
    onCopyNumber: () -> Unit,
    onSimulatePurchase: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "cardFlipAnimation"
    )

    // Dynamic ambient light glare that sweeps across the card face
    val infiniteTransition = rememberInfiniteTransition(label = "cardShimmer")
    val glareOffset by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glareProgress"
    )

    fun triggerHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30)
            }
        } catch (_: Exception) {}
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D Card Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.586f) // Standard ISO/IEC 7810 ID-1 ratio (85.60 mm × 53.98 mm)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 16f * density
                }
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color(0xCC000000),
                    ambientColor = Color(0x88000000)
                )
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    triggerHaptic()
                    onFlipClick()
                }
                .testTag("interactive_3d_card")
        ) {
            if (rotation <= 90f) {
                CardFrontContent(
                    card = card,
                    showDetails = showDetails,
                    glareOffset = glareOffset
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                ) {
                    CardBackContent(
                        card = card,
                        showDetails = showDetails
                    )
                }
            }

            // Frosted Frozen Ice Shield Overlay
            if (card.isFrozen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xB807101E))
                        .border(1.5.dp, Color(0x9906B6D4), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(0x3306B6D4))
                                .border(1.dp, Color(0xFF67E8F9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AcUnit,
                                contentDescription = "Card Frozen",
                                tint = Color(0xFF67E8F9),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "CARD LOCKED BY LEAD SHIELD",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = Color(0xFFE0F2FE)
                        )
                        Text(
                            text = "All POS, ATM & Online network authorizations suspended",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF93C5FD),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Card Control Micro-Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CardActionPill(
                icon = Icons.Default.Repeat,
                label = if (isFlipped) "Show Front" else "Flip Card",
                onClick = {
                    triggerHaptic()
                    onFlipClick()
                },
                testTag = "card_action_flip"
            )

            CardActionPill(
                icon = if (showDetails) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                label = if (showDetails) "Hide Info" else "Reveal Details",
                onClick = {
                    triggerHaptic()
                    onToggleDetails()
                },
                testTag = "card_action_details"
            )

            CardActionPill(
                icon = if (card.isFrozen) Icons.Default.LockOpen else Icons.Default.Lock,
                label = if (card.isFrozen) "Unlock" else "Lock Card",
                iconTint = if (card.isFrozen) EmeraldGreen else Color(0xFFF43F5E),
                onClick = {
                    triggerHaptic()
                    onToggleFreeze()
                },
                testTag = "card_action_freeze"
            )

            CardActionPill(
                icon = Icons.Default.ContentCopy,
                label = "Copy No.",
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Card Number", card.cardNumber)
                    clipboard.setPrimaryClip(clip)
                    triggerHaptic()
                    onCopyNumber()
                },
                testTag = "card_action_copy"
            )

            if (onSimulatePurchase != null) {
                CardActionPill(
                    icon = Icons.Default.ShoppingCart,
                    label = "Test Charge",
                    iconTint = ChampagneGold,
                    onClick = {
                        triggerHaptic()
                        onSimulatePurchase()
                    },
                    testTag = "card_action_test_charge"
                )
            }
        }
    }
}

/**
 * Returns the realistic professional bank card background drawable ID for each theme.
 */
private fun getCardBackgroundDrawable(theme: CardTheme): Int? {
    return when (theme) {
        CardTheme.OBSIDIAN -> R.drawable.img_card_bg_black_titanium_1790940817241
        CardTheme.CHASE_SAPPHIRE -> R.drawable.img_card_bg_sapphire_royal_1790940828590
        CardTheme.AMEX_PLATINUM -> R.drawable.bg_swiss_vault_1790889776583
        CardTheme.GOLD -> R.drawable.bg_vault_gold_1790889762793
        CardTheme.EMERALD -> R.drawable.bg_emerald_res_1790889786944
        CardTheme.CITI_CUSTOM -> R.drawable.bg_wall_street_1790889800687
        CardTheme.BARCLAYS_BLACK -> R.drawable.bg_carbon_plat_1790889814222
        CardTheme.CAPITAL_ONE_VENTURE -> R.drawable.bg_monaco_royal_1790889827350
        CardTheme.APPLE_TITANIUM -> null // Pure aerospace brushed titanium shader
    }
}

@Composable
private fun CardFrontContent(
    card: CardEntity,
    showDetails: Boolean,
    glareOffset: Float
) {
    val bgDrawableId = getCardBackgroundDrawable(card.theme)
    val isLightCard = card.theme == CardTheme.APPLE_TITANIUM || card.theme == CardTheme.AMEX_PLATINUM
    val primaryTextColor = if (isLightCard) Color(0xFF111827) else Color(0xFFF8FAFC)
    val secondaryTextColor = if (isLightCard) Color(0xFF4B5563) else Color(0xFF94A3B8)
    val accentLogoColor = if (isLightCard) Color(0xFF1E293B) else ChampagneGold
    val isPlatinumChip = card.theme == CardTheme.AMEX_PLATINUM || card.theme == CardTheme.APPLE_TITANIUM

    // Base fallback gradient if drawable is loading or for titanium
    val baseBrush = when (card.theme) {
        CardTheme.APPLE_TITANIUM -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFE2E8F0),
                Color(0xFFCBD5E1),
                Color(0xFF94A3B8),
                Color(0xFFE2E8F0)
            )
        )
        CardTheme.OBSIDIAN -> Brush.linearGradient(
            colors = listOf(Color(0xFF0F172A), Color(0xFF020617), Color(0xFF1E293B))
        )
        CardTheme.CHASE_SAPPHIRE -> Brush.linearGradient(
            colors = listOf(Color(0xFF071938), Color(0xFF0F2E5C), Color(0xFF030D1E))
        )
        CardTheme.GOLD -> Brush.linearGradient(
            colors = listOf(Color(0xFF4A3B18), Color(0xFF8C6D27), Color(0xFF2E240D))
        )
        CardTheme.EMERALD -> Brush.linearGradient(
            colors = listOf(Color(0xFF062B21), Color(0xFF0E4A3B), Color(0xFF031A14))
        )
        else -> Brush.linearGradient(
            colors = listOf(CardMetalObsidian, Color(0xFF1E2535), Color(0xFF0A0E17))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(baseBrush)
    ) {
        // 1. Professional Real Bank Card Background Image
        if (bgDrawableId != null) {
            Image(
                painter = painterResource(id = bgDrawableId),
                contentDescription = "${card.cardProductName} Background Texture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Luxury Darkening / Tinting Filter for Optimum Contrast & Branding Legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.20f),
                                Color.Black.copy(alpha = 0.50f)
                            )
                        )
                    )
            )
        }

        // 2. High-Precision Guilloché Security Geometry & Micro-Grid
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Micro-engraved concentric security rings behind the chip area
            val centerX = w * 0.25f
            val centerY = h * 0.45f
            for (r in 1..6) {
                drawCircle(
                    color = if (isLightCard) Color(0x0A000000) else Color(0x0CFFD700),
                    radius = r * 18f,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 0.8f)
                )
            }

            // Fine guilloché wave ribbon across the lower third
            val path = Path()
            val waveY = h * 0.65f
            path.moveTo(0f, waveY)
            var x = 0f
            while (x <= w) {
                val y = waveY + sin(x * 0.03f) * 6f + cos(x * 0.015f) * 4f
                path.lineTo(x, y)
                x += 8f
            }
            drawPath(
                path = path,
                color = if (isLightCard) Color(0x12000000) else Color(0x14FFFFFF),
                style = Stroke(width = 1f)
            )
        }

        // 3. Dynamic Specular Light Glare Sweep across the card surface
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val glareX = glareOffset * w * 1.5f - w * 0.25f

            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.03f),
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.03f),
                        Color.Transparent
                    ),
                    start = Offset(glareX, 0f),
                    end = Offset(glareX + w * 0.4f, h)
                ),
                size = Size(w, h)
            )
        }

        // 4. Solid Metal Card Core Perimeter Bevel (Amex/Apple heavy metal edge)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            if (isLightCard) Color(0x88000000) else Color(0x99FFD700),
                            Color(0x33FFFFFF),
                            if (isLightCard) Color(0x44000000) else Color(0x44FFD700),
                            Color(0x66FFFFFF)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        )

        // 5. Card Front Typography and Elements Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP ROW: Bank Brand & Contactless
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    EmbossedCardText(
                        text = card.bankName.uppercase(),
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.4.sp
                        ),
                        color = accentLogoColor,
                        isLightCard = isLightCard
                    )
                    Text(
                        text = card.cardProductName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.4.sp,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = secondaryTextColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (card.isOfficialLinkedBankCard) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isLightCard) Color(0x22000000) else Color(0x3310B981))
                                .border(
                                    0.8.dp,
                                    if (isLightCard) Color(0x44000000) else EmeraldGreen.copy(alpha = 0.6f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OFFICIAL LINKED",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = if (isLightCard) Color(0xFF047857) else EmeraldGreen
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Authentic Contactless Wave Symbol
                    ContactlessWaveSymbol(
                        color = accentLogoColor.copy(alpha = 0.90f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // MIDDLE ROW: Signature "Flame" EMV Smart Chip & OVD Security Hologram
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Realistic Flame EMV Smart Chip
                RealisticFlameEmvChip(
                    isPlatinum = isPlatinumChip,
                    glareOffset = glareOffset,
                    modifier = Modifier.size(width = 50.dp, height = 38.dp)
                )

                // Realistic Holographic Security Seal (Visa Dove / Mastercard / Bank Crest)
                RealisticHologramSeal(
                    isLightCard = isLightCard,
                    modifier = Modifier.size(width = 46.dp, height = 32.dp)
                )
            }

            // BOTTOM ROW: 3D Embossed Card Number, Cardholder Name & Expiration Date
            Column(modifier = Modifier.fillMaxWidth()) {
                val formattedNumber = if (showDetails) {
                    card.cardNumber.chunked(4).joinToString("  ")
                } else {
                    "••••  ••••  ••••  " + card.cardNumber.takeLast(4)
                }

                // 3D Stamped Embossed 16-Digit Card Number
                EmbossedCardText(
                    text = formattedNumber,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.8.sp
                    ),
                    color = primaryTextColor,
                    isLightCard = isLightCard
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "CARDHOLDER",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp,
                            letterSpacing = 1.2.sp,
                            color = secondaryTextColor,
                            fontWeight = FontWeight.Bold
                        )
                        EmbossedCardText(
                            text = card.cardholderName.uppercase(),
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            ),
                            color = primaryTextColor,
                            isLightCard = isLightCard
                        )
                    }

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "GOOD THRU",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp,
                            letterSpacing = 1.2.sp,
                            color = secondaryTextColor,
                            fontWeight = FontWeight.Bold
                        )
                        EmbossedCardText(
                            text = card.expiryDate,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = primaryTextColor,
                            isLightCard = isLightCard
                        )
                    }

                    // Official Card Network Badge (Visa Infinite / Mastercard World Elite / Amex)
                    NetworkLogoBadge(network = card.network)
                }
            }
        }
    }
}

/**
 * Signature "Flame" EMV Chip:
 * Luxury metal credit cards feature gold contact pads contoured in curved "flame" patterns.
 * This composable renders the authentic multi-stop gold/platinum foil, chamfered bevel,
 * engraved isolation flame channels, micro-vias, and a dynamic reflection highlight.
 */
@Composable
fun RealisticFlameEmvChip(
    isPlatinum: Boolean = false,
    glareOffset: Float = 0f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(6.dp), spotColor = Color(0x88000000))
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    if (isPlatinum) {
                        listOf(Color(0xFFE2E8F0), Color(0xFF64748B), Color(0xFFF1F5F9))
                    } else {
                        listOf(Color(0xFFF7ECD1), Color(0xFF8F6E23), Color(0xFFFFEDB3))
                    }
                ),
                shape = RoundedCornerShape(6.dp)
            )
            .background(
                Brush.linearGradient(
                    if (isPlatinum) {
                        listOf(
                            Color(0xFFCBD5E1),
                            Color(0xFF94A3B8),
                            Color(0xFFF8FAFC),
                            Color(0xFF64748B),
                            Color(0xFFE2E8F0)
                        )
                    } else {
                        listOf(
                            Color(0xFFE5C378),
                            Color(0xFFC79E48),
                            Color(0xFFF7ECD1),
                            Color(0xFFAD832B),
                            Color(0xFFE5C378)
                        )
                    }
                )
            )
    ) {
        // Detailed Flame Contact Pad Geometry Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val trenchDark = if (isPlatinum) Color(0xFF334155).copy(alpha = 0.90f) else Color(0xFF4A3408).copy(alpha = 0.95f)
            val trenchLight = if (isPlatinum) Color(0xFFFFFFFF).copy(alpha = 0.50f) else Color(0xFFFFF7DC).copy(alpha = 0.60f)

            // Function to draw an engraved trench with depth (dark stroke + offset light highlight)
            fun drawEngravedPath(path: Path, strokeWidth: Float = 1.5f) {
                // Highlight offset line
                drawPath(
                    path = path,
                    color = trenchLight,
                    style = Stroke(width = strokeWidth + 0.8f, cap = StrokeCap.Round)
                )
                // Dark engraved line
                drawPath(
                    path = path,
                    color = trenchDark,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // 1. Central Flame Stem & Teardrop Core
            val centerFlamePath = Path().apply {
                // Lower center vertical stem
                moveTo(w * 0.5f, h * 0.92f)
                lineTo(w * 0.5f, h * 0.68f)

                // Central teardrop core perimeter
                cubicTo(
                    w * 0.38f, h * 0.65f,
                    w * 0.38f, h * 0.42f,
                    w * 0.50f, h * 0.30f
                )
                cubicTo(
                    w * 0.62f, h * 0.42f,
                    w * 0.62f, h * 0.65f,
                    w * 0.50f, h * 0.68f
                )

                // Upper vertical tip
                moveTo(w * 0.5f, h * 0.30f)
                lineTo(w * 0.5f, h * 0.08f)
            }
            drawEngravedPath(centerFlamePath)

            // 2. Upper Left Flame Tongue
            val upperLeftFlame = Path().apply {
                moveTo(w * 0.42f, h * 0.40f)
                cubicTo(
                    w * 0.30f, h * 0.36f,
                    w * 0.22f, h * 0.22f,
                    w * 0.06f, h * 0.25f
                )
            }
            drawEngravedPath(upperLeftFlame)

            // 3. Upper Right Flame Tongue
            val upperRightFlame = Path().apply {
                moveTo(w * 0.58f, h * 0.40f)
                cubicTo(
                    w * 0.70f, h * 0.36f,
                    w * 0.78f, h * 0.22f,
                    w * 0.94f, h * 0.25f
                )
            }
            drawEngravedPath(upperRightFlame)

            // 4. Middle Lateral Channels
            val midLeftChannel = Path().apply {
                moveTo(w * 0.38f, h * 0.54f)
                lineTo(w * 0.06f, h * 0.54f)
            }
            drawEngravedPath(midLeftChannel)

            val midRightChannel = Path().apply {
                moveTo(w * 0.62f, h * 0.54f)
                lineTo(w * 0.94f, h * 0.54f)
            }
            drawEngravedPath(midRightChannel)

            // 5. Lower Left Flame Lobe
            val lowerLeftFlame = Path().apply {
                moveTo(w * 0.40f, h * 0.64f)
                cubicTo(
                    w * 0.28f, h * 0.70f,
                    w * 0.20f, h * 0.80f,
                    w * 0.06f, h * 0.78f
                )
            }
            drawEngravedPath(lowerLeftFlame)

            // 6. Lower Right Flame Lobe
            val lowerRightFlame = Path().apply {
                moveTo(w * 0.60f, h * 0.64f)
                cubicTo(
                    w * 0.72f, h * 0.70f,
                    w * 0.80f, h * 0.80f,
                    w * 0.94f, h * 0.78f
                )
            }
            drawEngravedPath(lowerRightFlame)

            // 7. Micro-via contact dots
            val viaColor = if (isPlatinum) Color(0xFF475569) else Color(0xFF684910)
            drawCircle(color = viaColor, radius = 1.3f, center = Offset(w * 0.24f, h * 0.38f))
            drawCircle(color = viaColor, radius = 1.3f, center = Offset(w * 0.76f, h * 0.38f))
            drawCircle(color = viaColor, radius = 1.3f, center = Offset(w * 0.24f, h * 0.66f))
            drawCircle(color = viaColor, radius = 1.3f, center = Offset(w * 0.76f, h * 0.66f))
            drawCircle(color = viaColor, radius = 1.5f, center = Offset(w * 0.50f, h * 0.50f))
        }

        // Ambient Glare Reflection on the Chip Face
        Canvas(modifier = Modifier.fillMaxSize()) {
            val chipGlareX = ((glareOffset * 2f) % 2f) * size.width
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    start = Offset(chipGlareX - 15f, 0f),
                    end = Offset(chipGlareX + 15f, size.height)
                )
            )
        }
    }
}

/**
 * Optical Variable Device (OVD) Rainbow Hologram Security Seal
 */
@Composable
fun RealisticHologramSeal(
    isLightCard: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0x5506B6D4), // Cyan
                        Color(0x55EC4899), // Magenta
                        Color(0x55EAB308), // Gold
                        Color(0x558B5CF6)  // Violet
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(120f, 100f)
                )
            )
            .border(
                width = 0.8.dp,
                brush = Brush.linearGradient(
                    listOf(Color(0x66FFFFFF), Color(0x33000000), Color(0x66FFFFFF))
                ),
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Concentric security guilloché rings
            drawCircle(
                color = Color(0x33FFFFFF),
                radius = h * 0.38f,
                center = Offset(w * 0.5f, h * 0.5f),
                style = Stroke(width = 0.8f)
            )
            drawCircle(
                color = Color(0x22FFFFFF),
                radius = h * 0.25f,
                center = Offset(w * 0.5f, h * 0.5f),
                style = Stroke(width = 0.8f)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LEAD",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = if (isLightCard) Color(0xBB000000) else Color(0xCCFFFFFF)
            )
            Text(
                text = "AUTHENTIC",
                fontSize = 5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = if (isLightCard) Color(0x88000000) else Color(0x99FFFFFF)
            )
        }
    }
}

/**
 * 4-Wave Contactless NFC Metallic Waves
 */
@Composable
fun ContactlessWaveSymbol(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.15f, h * 0.5f)

        for (i in 1..4) {
            val r = i * (w * 0.20f)
            drawArc(
                color = color,
                startAngle = -42f,
                sweepAngle = 84f,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2f, r * 2f),
                style = Stroke(width = 1.6f, cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * 3D Stamped Embossed Metallic Foil Text Effect
 */
@Composable
fun EmbossedCardText(
    text: String,
    style: TextStyle,
    color: Color,
    isLightCard: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // 1. Lower-Right Engraving Shadow
        Text(
            text = text,
            style = style,
            color = if (isLightCard) Color(0x44000000) else Color(0xCC000000),
            modifier = Modifier.graphicsLayer {
                translationX = 1.2f
                translationY = 1.4f
            }
        )
        // 2. Upper-Left Metallic Foil Specular Reflection
        Text(
            text = text,
            style = style,
            color = if (isLightCard) Color(0x99FFFFFF) else Color(0x77FFF2C2),
            modifier = Modifier.graphicsLayer {
                translationX = -0.7f
                translationY = -0.7f
            }
        )
        // 3. Main Embossed Text Face
        Text(
            text = text,
            style = style,
            color = color
        )
    }
}

/**
 * Authentic Network Brand Badges
 */
@Composable
private fun NetworkLogoBadge(network: CardNetwork) {
    when (network) {
        CardNetwork.VISA_INFINITE -> {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "VISA",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 1.sp,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF1A1F71))
                        .border(0.5.dp, Color(0xFFF7B600), RoundedCornerShape(3.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Text(
                    text = "INFINITE",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = Color(0xFFD4AF37)
                )
            }
        }
        CardNetwork.MASTERCARD_WORLD_ELITE -> {
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEB001B))
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer { translationX = -18f }
                            .clip(CircleShape)
                            .background(Color(0xFFF79E1B).copy(alpha = 0.92f))
                    )
                }
                Text(
                    text = "world elite",
                    fontSize = 7.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Color(0xFFE2E8F0)
                )
            }
        }
        CardNetwork.AMEX -> {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF007BC1))
                    .border(0.8.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "AMEX",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Realistic Back of Bank Card:
 * - High-coercivity 3-track Magnetic Stripe with gloss sheen
 * - Tamper-evident white signature panel with microprinted security advisory
 * - Handwritten signature cursive in ink
 * - High-contrast CVV security block
 * - Holographic magnetic security foil
 * - Federal depository disclosures and 24/7 global concierge hotline
 */
@Composable
private fun CardBackContent(card: CardEntity, showDetails: Boolean) {
    val bgDrawableId = getCardBackgroundDrawable(card.theme)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CardMetalObsidian)
            .border(1.5.dp, SurfaceBorder, RoundedCornerShape(16.dp))
    ) {
        // Optional dark texture on back
        if (bgDrawableId != null) {
            Image(
                painter = painterResource(id = bgDrawableId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.25f
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. 3-Track Magnetic Stripe with subtle gloss reflection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .background(Color(0xFF080B10))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Fine magnetic read tracks
                    drawLine(
                        color = Color(0x18FFFFFF),
                        start = Offset(0f, size.height * 0.33f),
                        end = Offset(size.width, size.height * 0.33f),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = Color(0x18FFFFFF),
                        start = Offset(0f, size.height * 0.66f),
                        end = Offset(size.width, size.height * 0.66f),
                        strokeWidth = 1f
                    )
                    // High-gloss reflection strip
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0x15FFFFFF), Color.Transparent)
                        ),
                        size = size
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Tamper-Evident Signature Panel + CVV Block + Holographic Foil
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Signature Strip with fine security microprint lines
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFE2E8F0))
                        .border(0.6.dp, Color(0xFF94A3B8), RoundedCornerShape(3.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Security hatched microprint background
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val step = 8f
                        var x = 0f
                        while (x <= size.width + size.height) {
                            drawLine(
                                color = Color(0x15000000),
                                start = Offset(x, 0f),
                                end = Offset(x - size.height, size.height),
                                strokeWidth = 0.8f
                            )
                            x += step
                        }
                    }

                    // Authentic handwritten signature
                    val formattedName = card.cardholderName
                        .lowercase()
                        .split(" ")
                        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

                    Text(
                        text = formattedName,
                        fontFamily = FontFamily.Cursive,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // CVV Security Box
                Box(
                    modifier = Modifier
                        .width(54.dp)
                        .height(38.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, ChampagneGold.copy(alpha = 0.6f), RoundedCornerShape(3.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SECURITY CODE",
                            fontSize = 6.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (showDetails) card.cvv else "•••",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            color = ChampagneGold,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Holographic Security Foil Strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0x4406B6D4),
                                Color(0x44EC4899),
                                Color(0x44EAB308),
                                Color(0x448B5CF6)
                            )
                        )
                    )
            ) {
                Text(
                    text = "VALID • SECURE • AUTHORIZED TRANSACTION TOKEN • FDIC INSURED",
                    fontSize = 6.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 4. Regulatory Disclosures & Global Concierge Hotline
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "${card.bankName.uppercase()} • CHARTERED PRIVATE INSTITUTION • MEMBER FDIC",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "This metal card remains the property of the issuer and must be surrendered upon demand. 24/7 Global Concierge: +1 (800) 555-LEAD • lead.bank/concierge",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 6.5.sp,
                    color = TextSecondary.copy(alpha = 0.7f),
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

@Composable
private fun CardActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String,
    iconTint: Color = ChampagneGold
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceCard)
                .border(1.dp, SurfaceBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
