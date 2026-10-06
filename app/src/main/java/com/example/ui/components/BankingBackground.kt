package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.BankingTheme

enum class BankingBackgroundType {
    DASHBOARD,
    ACCOUNTS,
    TRANSACTIONS,
    TRANSFERS,
    CARDS,
    CRYPTO,
    VAULTS,
    SECURITY,
    STATEMENTS
}

/**
 * Premium banking background canvas modifier drawing refined financial architectural
 * gridlines, delicate radial auras, and subtle geometric security textures.
 */
fun Modifier.bankingBackground(
    type: BankingBackgroundType = BankingBackgroundType.DASHBOARD,
    isDark: Boolean? = null
): Modifier = this.composed {
    val dark = isDark ?: BankingTheme.colors.isDark
    this.drawBehind {
        val w = size.width
        val h = size.height

        // 1. Base gradient
        val baseGradient = if (dark) {
            when (type) {
                BankingBackgroundType.DASHBOARD -> listOf(Color(0xFF06080D), Color(0xFF0D121B), Color(0xFF07090E))
                BankingBackgroundType.TRANSFERS -> listOf(Color(0xFF05080E), Color(0xFF0B1424), Color(0xFF07090E))
                BankingBackgroundType.CARDS -> listOf(Color(0xFF0A0D14), Color(0xFF131822), Color(0xFF07090E))
                BankingBackgroundType.CRYPTO -> listOf(Color(0xFF060910), Color(0xFF101322), Color(0xFF07090E))
                BankingBackgroundType.VAULTS -> listOf(Color(0xFF060A0D), Color(0xFF0E1A1A), Color(0xFF07090E))
                BankingBackgroundType.SECURITY -> listOf(Color(0xFF07090E), Color(0xFF0C141D), Color(0xFF07090E))
                else -> listOf(Color(0xFF07090E), Color(0xFF0D111A))
            }
        } else {
            when (type) {
                BankingBackgroundType.DASHBOARD -> listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC), Color(0xFFF1F5F9))
                BankingBackgroundType.TRANSFERS -> listOf(Color(0xFFFFFFFF), Color(0xFFF0FDF4), Color(0xFFF1F5F9))
                BankingBackgroundType.CARDS -> listOf(Color(0xFFFFFFFF), Color(0xFFFAF5FF), Color(0xFFF8FAFC))
                BankingBackgroundType.CRYPTO -> listOf(Color(0xFFFFFFFF), Color(0xFFEFF6FF), Color(0xFFF1F5F9))
                BankingBackgroundType.VAULTS -> listOf(Color(0xFFFFFFFF), Color(0xFFECFDF5), Color(0xFFF8FAFC))
                BankingBackgroundType.SECURITY -> listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC), Color(0xFFF1F5F9))
                else -> listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC))
            }
        }

        drawRect(
            brush = Brush.verticalGradient(
                colors = baseGradient,
                startY = 0f,
                endY = h
            )
        )

        // 2. Soft Aura Glow (Top Center / Corner)
        val auraColor = if (dark) {
            when (type) {
                BankingBackgroundType.DASHBOARD -> Color(0x1CE5C378) // Gold aura
                BankingBackgroundType.TRANSFERS -> Color(0x1A10B981) // Emerald aura
                BankingBackgroundType.CARDS -> Color(0x188B5CF6) // Purple aura
                BankingBackgroundType.CRYPTO -> Color(0x1806B6D4) // Cyan aura
                BankingBackgroundType.VAULTS -> Color(0x1A10B981) // Vault yield green
                BankingBackgroundType.SECURITY -> Color(0x163B82F6) // Cobalt shield
                else -> Color(0x12E5C378)
            }
        } else {
            when (type) {
                BankingBackgroundType.DASHBOARD -> Color(0x10B48328)
                BankingBackgroundType.TRANSFERS -> Color(0x10059669)
                BankingBackgroundType.CARDS -> Color(0x0C7C3AED)
                BankingBackgroundType.CRYPTO -> Color(0x100284C7)
                BankingBackgroundType.VAULTS -> Color(0x10059669)
                BankingBackgroundType.SECURITY -> Color(0x0E2563EB)
                else -> Color(0x0EB48328)
            }
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(auraColor, Color.Transparent),
                center = Offset(w * 0.5f, 120f),
                radius = w * 0.75f
            ),
            center = Offset(w * 0.5f, 120f),
            radius = w * 0.75f
        )

        // 3. Subtle architectural financial line patterns & guilloche accents
        val gridColor = if (dark) Color(0x0AFFFFFF) else Color(0x080F172A)
        val gridSpacing = 48f

        // Horizontal ledger guide lines (subtle in top half)
        var currY = 40f
        while (currY < h * 0.6f) {
            drawLine(
                color = gridColor,
                start = Offset(0f, currY),
                end = Offset(w, currY),
                strokeWidth = 1f
            )
            currY += gridSpacing
        }

        // Vertical column guides
        var currX = gridSpacing
        while (currX < w) {
            drawLine(
                color = gridColor.copy(alpha = gridColor.alpha * 0.6f),
                start = Offset(currX, 0f),
                end = Offset(currX, h * 0.5f),
                strokeWidth = 1f
            )
            currX += gridSpacing * 2f
        }

        // Concentric security watermark arcs (top right corner)
        val arcColor = if (dark) Color(0x06E5C378) else Color(0x08B48328)
        for (radius in listOf(100f, 160f, 220f, 280f)) {
            drawCircle(
                color = arcColor,
                center = Offset(w - 20f, 20f),
                radius = radius,
                style = Stroke(width = 1f)
            )
        }
    }
}

@Composable
fun BankingBackgroundContainer(
    type: BankingBackgroundType = BankingBackgroundType.DASHBOARD,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = BankingTheme.colors.isDark
    Box(
        modifier = modifier
            .fillMaxSize()
            .bankingBackground(type, isDark)
    ) {
        content()
    }
}
