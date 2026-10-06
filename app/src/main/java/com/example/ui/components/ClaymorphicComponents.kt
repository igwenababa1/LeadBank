package com.example.ui.components

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BankingTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Professional Premium Claymorphism Design System for Banking:
 * Provides realistic 3D inflated soft surfaces, dual-depth lighting, tactile squishy
 * press mechanics, and volumetric ceramic/titanium clay containers across the entire app.
 */

enum class ClayVariant {
    OBSIDIAN,   // Deep luxury carbon clay with subtle gold specular sheen
    SURFACE,    // Standard matte dark elevated clay
    GOLD,       // Bullion champagne gold clay
    EMERALD,    // Sovereign reserve emerald green clay
    CRIMSON,    // Alert / debit expense clay
    CYAN,       // Electric telemetry cyan clay
    PORCELAIN,  // Pure ivory / alabaster light clay
    INSET       // Sunken / debossed clay for input containers
}

enum class ClayElevation(val dpValue: Dp) {
    LOW(8.dp),
    MEDIUM(16.dp),
    HIGH(24.dp),
    MASSIVE(32.dp)
}

/**
 * Tactile spring press modifier that produces squishy clay-like physical displacement.
 */
fun Modifier.clayPressEffect(
    isClickable: Boolean = true,
    targetScale: Float = 0.965f,
    onClick: (() -> Unit)? = null
): Modifier = if (!isClickable && onClick == null) this else this.composed {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) targetScale else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "clayScale"
    )

    fun hapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                v?.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(onClick) {
            awaitEachGesture {
                awaitFirstDown()
                isPressed = true
                hapticFeedback()
                val up = waitForUpOrCancellation()
                isPressed = false
                if (up != null && onClick != null) {
                    onClick()
                }
            }
        }
}


/**
 * Volumetric 3D Clay Surface Modifier:
 * Applies:
 * 1. Deep diffused outer drop shadow.
 * 2. Convex gradient body simulating pillowy inflation.
 * 3. Inset inner specular highlight (top-left) + inset inner shadow (bottom-right).
 * 4. Dual-light bouncing rim border.
 */
fun Modifier.claymorphic(
    variant: ClayVariant = ClayVariant.OBSIDIAN,
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: ClayElevation = ClayElevation.MEDIUM,
    isDark: Boolean? = null
): Modifier = this.composed {
    val dark = isDark ?: BankingTheme.colors.isDark
    this
        .shadow(
            elevation = elevation.dpValue,
            shape = shape,
            spotColor = when (variant) {
                ClayVariant.GOLD -> Color(0x66E5C378)
                ClayVariant.EMERALD -> Color(0x6610B981)
                ClayVariant.CRIMSON -> Color(0x66F43F5E)
                ClayVariant.CYAN -> Color(0x6606B6D4)
                else -> if (dark) Color(0xDD000000) else Color(0x1F475569)
            },
            ambientColor = if (dark) Color(0xAA000000) else Color(0x1264748B)
        )
        .clip(shape)
        .background(
            brush = when (variant) {
                ClayVariant.GOLD -> Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF3DE9C),
                        Color(0xFFE5C378),
                        Color(0xFFC4983D),
                        Color(0xFF8F6E23)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(400f, 600f)
                )
                ClayVariant.EMERALD -> Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF26DE9F),
                        Color(0xFF10B981),
                        Color(0xFF0D9467),
                        Color(0xFF065F46)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(400f, 600f)
                )
                ClayVariant.CRIMSON -> Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFB7185),
                        Color(0xFFF43F5E),
                        Color(0xFFBE123C),
                        Color(0xFF881337)
                    )
                )
                ClayVariant.CYAN -> Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF22D3EE),
                        Color(0xFF06B6D4),
                        Color(0xFF0891B2),
                        Color(0xFF155E75)
                    )
                )
                ClayVariant.PORCELAIN -> Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF8FAFC),
                        Color(0xFFE2E8F0)
                    )
                )
                ClayVariant.INSET -> Brush.linearGradient(
                    colors = if (dark) {
                        listOf(Color(0xFF090D14), Color(0xFF0E1420), Color(0xFF131B2A))
                    } else {
                        listOf(Color(0xFFF1F5F9), Color(0xFFF8FAFC), Color(0xFFFFFFFF))
                    }
                )
                ClayVariant.SURFACE -> Brush.linearGradient(
                    colors = if (dark) {
                        listOf(
                            Color(0xFF27344D),
                            Color(0xFF1C263B),
                            Color(0xFF151D2F)
                        )
                    } else {
                        listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFF8FAFC),
                            Color(0xFFEEF2F6)
                        )
                    },
                    start = Offset(0f, 0f),
                    end = Offset(500f, 500f)
                )
                ClayVariant.OBSIDIAN -> Brush.linearGradient(
                    colors = if (dark) {
                        listOf(
                            Color(0xFF26324A),
                            Color(0xFF1B2436),
                            Color(0xFF121825),
                            Color(0xFF0A0E17)
                        )
                    } else {
                        listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFF8FAFC),
                            Color(0xFFE5EAF2)
                        )
                    },
                    start = Offset(0f, 0f),
                    end = Offset(600f, 800f)
                )
            }
        )
        .drawWithContent {
            drawContent()

            val w = size.width
            val h = size.height

            if (variant == ClayVariant.INSET) {
                // Sunken debossed lighting (dark shadow on top/left, soft highlight on bottom/right)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = if (dark) 0.55f else 0.08f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = h * 0.4f
                    ),
                    size = size
                )
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = if (dark) 0.10f else 0.60f)
                        ),
                        startY = h * 0.6f,
                        endY = h
                    ),
                    size = size
                )
            } else {
                // Volumetric convex 3D inflated clay lighting
                // 1. Top-Left soft specular highlight curve
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (dark) 0.18f else 0.70f),
                            Color.White.copy(alpha = if (dark) 0.05f else 0.20f),
                            Color.Transparent
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(w * 0.65f, h * 0.65f)
                    ),
                    size = size
                )

                // 2. Bottom-Right inner recessed ambient occlusion shadow
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF475569).copy(alpha = if (dark) 0.40f else 0.05f),
                            Color(0xFF1E293B).copy(alpha = if (dark) 0.60f else 0.10f)
                        ),
                        start = Offset(w * 0.35f, h * 0.35f),
                        end = Offset(w, h)
                    ),
                    size = size
                )
            }
        }
        .border(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                colors = when (variant) {
                    ClayVariant.GOLD -> listOf(
                        Color(0xFFFFF6D8),
                        Color(0x88E5C378),
                        Color(0x33785416)
                    )
                    ClayVariant.EMERALD -> listOf(
                        Color(0xFFA7F3D0),
                        Color(0x8810B981),
                        Color(0x33064E3B)
                    )
                    ClayVariant.INSET -> listOf(
                        if (dark) Color(0x33000000) else Color(0x1F94A3B8),
                        if (dark) Color(0x18FFFFFF) else Color(0x66FFFFFF)
                    )
                    else -> if (dark) {
                        listOf(
                            Color(0x4DFFFFFF), // Soft top light rim catch
                            Color(0x18FFFFFF),
                            Color(0x22000000), // Bottom deep edge
                            Color(0x55000000)
                        )
                    } else {
                        listOf(
                            Color(0xFFFFFFFF),
                            Color(0x44CBD5E1),
                            Color(0x2294A3B8)
                        )
                    }
                },
                start = Offset(0f, 0f),
                end = Offset(300f, 300f)
            ),
            shape = shape
        )
}

/**
 * 3D Claymorphic Card Container:
 * Inflated, tactile card component that replaces standard flat cards with volumetric depth.
 */
@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    variant: ClayVariant = ClayVariant.OBSIDIAN,
    elevation: ClayElevation = ClayElevation.MEDIUM,
    shape: Shape = RoundedCornerShape(24.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = BankingTheme.colors.isDark
    val pressModifier = if (onClick != null) {
        Modifier.clayPressEffect(onClick = onClick)
    } else Modifier

    Box(
        modifier = modifier
            .then(pressModifier)
            .claymorphic(
                variant = variant,
                shape = shape,
                elevation = elevation,
                isDark = isDark
            ),
        content = content
    )
}

/**
 * 3D Claymorphic Push Button:
 * Inflated pillowy button with bouncy physics and rich multi-gradient depth.
 */
@Composable
fun ClayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ClayVariant = ClayVariant.GOLD,
    elevation: ClayElevation = ClayElevation.MEDIUM,
    shape: Shape = RoundedCornerShape(18.dp),
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val isDark = BankingTheme.colors.isDark
    val effectiveVariant = if (enabled) variant else ClayVariant.SURFACE

    Box(
        modifier = modifier
            .clayPressEffect(isClickable = enabled, onClick = if (enabled) onClick else null)
            .claymorphic(
                variant = effectiveVariant,
                shape = shape,
                elevation = if (enabled) elevation else ClayElevation.LOW,
                isDark = isDark
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}

/**
 * 3D Claymorphic Icon Button:
 * Rounded circular or squircle tactile clay button for actions, toolbar shortcuts, and controls.
 */
@Composable
fun ClayIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ClayVariant = ClayVariant.SURFACE,
    elevation: ClayElevation = ClayElevation.LOW,
    tint: Color = BankingTheme.colors.primaryAccent,
    size: Dp = 46.dp,
    iconSize: Dp = 22.dp,
    shape: Shape = CircleShape
) {
    val isDark = BankingTheme.colors.isDark

    Box(
        modifier = modifier
            .size(size)
            .clayPressEffect(onClick = onClick)
            .claymorphic(
                variant = variant,
                shape = shape,
                elevation = elevation,
                isDark = isDark
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * 3D Claymorphic Pill / Capsule:
 * Tactile volumetric badge for status tags, filters, currency tokens, and quick amounts.
 */
@Composable
fun ClayPill(
    text: String,
    modifier: Modifier = Modifier,
    variant: ClayVariant = ClayVariant.SURFACE,
    icon: ImageVector? = null,
    textColor: Color = BankingTheme.colors.textPrimary,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val isDark = BankingTheme.colors.isDark
    val activeVariant = if (isSelected) ClayVariant.GOLD else variant
    val effectiveTextColor = if (isSelected) Obsidian950 else textColor

    val baseModifier = if (onClick != null) {
        modifier.clayPressEffect(onClick = onClick)
    } else modifier

    Box(
        modifier = baseModifier
            .claymorphic(
                variant = activeVariant,
                shape = RoundedCornerShape(16.dp),
                elevation = if (isSelected) ClayElevation.MEDIUM else ClayElevation.LOW,
                isDark = isDark
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = effectiveTextColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = effectiveTextColor,
                fontSize = 11.sp
            )
        }
    }
}

/**
 * 3D Claymorphic Stat / Metric Plaque:
 * Inflated card displaying a 3D clay icon sphere, prominent figure, and descriptive label.
 */
@Composable
fun ClayStatBox(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconTint: Color = BankingTheme.colors.primaryAccent,
    modifier: Modifier = Modifier,
    variant: ClayVariant = ClayVariant.OBSIDIAN,
    onClick: (() -> Unit)? = null
) {
    ClayCard(
        modifier = modifier.fillMaxWidth(),
        variant = variant,
        elevation = ClayElevation.MEDIUM,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    letterSpacing = 1.4.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    fontSize = 22.sp
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 3D Inflated Icon Sphere
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .claymorphic(
                        variant = ClayVariant.SURFACE,
                        shape = CircleShape,
                        elevation = ClayElevation.MEDIUM,
                        isDark = BankingTheme.colors.isDark
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * 3D Claymorphic Quick Action Button:
 * Pillowy round action button with label below, used in dashboards and quick transfer rows.
 */
@Composable
fun ClayQuickAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ClayVariant = ClayVariant.SURFACE,
    tint: Color = ChampagneGold,
    badgeText: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clayPressEffect(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .claymorphic(
                        variant = variant,
                        shape = CircleShape,
                        elevation = ClayElevation.MEDIUM,
                        isDark = BankingTheme.colors.isDark
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }
            if (badgeText != null) {
                Box(
                    modifier = Modifier
                        .graphicsLayer { translationX = 6f; translationY = -6f }
                        .clip(RoundedCornerShape(6.dp))
                        .background(EmeraldGreen)
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 3D Claymorphic Floating Navigation Bar:
 * Tactile inflated bottom pill bar respecting system window insets with 3D active pill indicators.
 */
@Composable
fun ClayFloatingNavigationBar(
    items: List<ClayNavItemData>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = BankingTheme.colors.isDark

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .claymorphic(
                variant = if (isDark) ClayVariant.OBSIDIAN else ClayVariant.SURFACE,
                shape = RoundedCornerShape(28.dp),
                elevation = ClayElevation.HIGH,
                isDark = isDark
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clayPressEffect(onClick = { onSelectIndex(index) })
                        .then(
                            if (isSelected) {
                                Modifier.claymorphic(
                                    variant = ClayVariant.GOLD,
                                    shape = RoundedCornerShape(20.dp),
                                    elevation = ClayElevation.MEDIUM,
                                    isDark = isDark
                                )
                            } else Modifier
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) Obsidian950 else BankingTheme.colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Obsidian950,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

data class ClayNavItemData(
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

/**
 * 3D Claymorphic Master Balance Hero Plaque:
 * Prominent executive vault card with inflated 3D tactile pillowy geometry.
 */
@Composable
fun ClayBalancePlaque(
    totalBalance: Double,
    isBalanceHidden: Boolean,
    onToggleBalanceHidden: () -> Unit,
    primaryCurrency: String = "USD",
    accountCount: Int = 4,
    onDepositClick: () -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = BankingTheme.colors.isDark

    ClayCard(
        modifier = modifier.fillMaxWidth(),
        variant = ClayVariant.OBSIDIAN,
        elevation = ClayElevation.MASSIVE,
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // Header Row: Depository Label & Security Pill
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
                            .background(EmeraldGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TOTAL DEPOSITORY ASSETS",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        letterSpacing = 1.6.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Balance Visibility Toggle Clay Pill
                Box(
                    modifier = Modifier
                        .clayPressEffect(onClick = onToggleBalanceHidden)
                        .claymorphic(
                            variant = ClayVariant.SURFACE,
                            shape = RoundedCornerShape(12.dp),
                            elevation = ClayElevation.LOW,
                            isDark = isDark
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBalanceHidden) "SHOW" else "HIDE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ChampagneGold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Massive Master Balance Figure
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isBalanceHidden) "••••••••••" else "$${"%,.2f".format(totalBalance)}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    fontSize = if (isBalanceHidden) 30.sp else 34.sp,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = primaryCurrency,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$accountCount Multi-Bank Ledgers • FDIC Insured Sweep $5M",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "+4.85% APY YIELD",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Clay Action Buttons: Deposit vs Send
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ClayButton(
                    onClick = onDepositClick,
                    variant = ClayVariant.GOLD,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Deposit & Wire In",
                        fontWeight = FontWeight.ExtraBold,
                        color = Obsidian950,
                        fontSize = 13.sp
                    )
                }

                ClayButton(
                    onClick = onSendClick,
                    variant = ClayVariant.SURFACE,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Transfer & Send",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

/**
 * 3D Claymorphic Sunken / Inset Input Container:
 * Creates a debossed, recessed clay receptacle for text fields, amount inputs, and search bars.
 */
@Composable
fun ClayInputContainer(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = BankingTheme.colors.isDark
    Box(
        modifier = modifier
            .claymorphic(
                variant = ClayVariant.INSET,
                shape = shape,
                elevation = ClayElevation.LOW,
                isDark = isDark
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        content = content
    )
}

/**
 * 3D Claymorphic Tactile Switch:
 * Inflated pillowy oval track with a sliding 3D ceramic/gold marble knob and spring physics.
 */
@Composable
fun ClaySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    activeVariant: ClayVariant = ClayVariant.GOLD,
    inactiveVariant: ClayVariant = ClayVariant.SURFACE
) {
    val isDark = BankingTheme.colors.isDark
    val knobOffset by animateFloatAsState(
        targetValue = if (checked) 24f else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 500f),
        label = "switchKnob"
    )

    Box(
        modifier = modifier
            .size(width = 54.dp, height = 30.dp)
            .clayPressEffect(onClick = { onCheckedChange(!checked) })
            .claymorphic(
                variant = if (checked) activeVariant else inactiveVariant,
                shape = RoundedCornerShape(15.dp),
                elevation = ClayElevation.LOW,
                isDark = isDark
            )
            .padding(3.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Sliding 3D Inflated Marble Knob
        Box(
            modifier = Modifier
                .graphicsLayer { translationX = knobOffset * density }
                .size(24.dp)
                .claymorphic(
                    variant = if (checked) ClayVariant.PORCELAIN else ClayVariant.OBSIDIAN,
                    shape = CircleShape,
                    elevation = ClayElevation.MEDIUM,
                    isDark = isDark
                )
        )
    }
}

/**
 * 3D Claymorphic Pill Badge:
 * Compact, volumetric inflated badge for KYC certifications, APY rates, and security labels.
 */
@Composable
fun ClayBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: ClayVariant = ClayVariant.GOLD,
    icon: ImageVector? = null,
    textColor: Color = Obsidian950
) {
    val isDark = BankingTheme.colors.isDark
    Box(
        modifier = modifier
            .claymorphic(
                variant = variant,
                shape = RoundedCornerShape(12.dp),
                elevation = ClayElevation.LOW,
                isDark = isDark
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * 3D Claymorphic Section Header Bar:
 * Tactile uppercase header with optional badge or action pill.
 */
@Composable
fun ClaySectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    badgeText: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ChampagneGold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.5.sp,
                    letterSpacing = 1.4.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = TextSecondary.copy(alpha = 0.7f)
                    )
                }
            }
            if (badgeText != null) {
                Spacer(modifier = Modifier.width(8.dp))
                ClayBadge(text = badgeText, variant = ClayVariant.EMERALD, textColor = Color.Black)
            }
        }

        if (actionText != null && onActionClick != null) {
            Box(
                modifier = Modifier
                    .clayPressEffect(onClick = onActionClick)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

