package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Obsidian & Luxury Dark Base Constants
val Obsidian950 = Color(0xFF07090E)
val Obsidian900 = Color(0xFF0D111A)
val Obsidian850 = Color(0xFF131926)

// Dynamic Luxury Surface & Border Tokens (Real-time Light & Dark)
val SurfaceDark: Color
    @Composable
    get() = BankingTheme.colors.backgroundElevated

val SurfaceCard: Color
    @Composable
    get() = BankingTheme.colors.surfaceCard

val SurfaceCardElevated: Color
    @Composable
    get() = BankingTheme.colors.surfaceCardElevated

val SurfaceBorder: Color
    @Composable
    get() = BankingTheme.colors.border

val SurfaceBorderGlow: Color
    @Composable
    get() = BankingTheme.colors.borderGlow

// Champagne Gold (Lead Signature Accent)
val ChampagneGold = Color(0xFFE5C378)
val ChampagneLight = Color(0xFFF7ECD1)
val ChampagneDark = Color(0xFFB89240)
val ChampagneGlow = Color(0x33E5C378)

// Cashflow & Status Accents
val EmeraldGreen = Color(0xFF10B981)
val EmeraldGreenLight = Color(0xFF34D399)
val EmeraldGreenGlow = Color(0x2610B981)

val CrimsonExpense = Color(0xFFF43F5E)
val CrimsonLight = Color(0xFFFB7185)
val CrimsonGlow = Color(0x26F43F5E)

val ElectricCyan = Color(0xFF06B6D4)
val CobaltBlue = Color(0xFF3B82F6)
val PurpleVault = Color(0xFF8B5CF6)
val AmberPending = Color(0xFFF59E0B)

// Dynamic Typography Tokens (Real-time Light & Dark)
val TextPrimary: Color
    @Composable
    get() = BankingTheme.colors.textPrimary

val TextSecondary: Color
    @Composable
    get() = BankingTheme.colors.textSecondary

val TextTertiary: Color
    @Composable
    get() = BankingTheme.colors.textTertiary

val TextMuted: Color
    @Composable
    get() = BankingTheme.colors.textMuted

// Card Finishes
val CardMetalObsidian = Color(0xFF10141D)
val CardMetalTitanium = Color(0xFF28303F)
val CardMetalChampagne = Color(0xFF2B2519)
val CardMetalEmerald = Color(0xFF122822)

// ==========================================
// Comprehensive Banking Design Tokens
// ==========================================
@Immutable
data class BankingColorScheme(
    val isDark: Boolean,
    val background: Color,
    val backgroundElevated: Color,
    val surfaceCard: Color,
    val surfaceCardElevated: Color,
    val surfaceGlass: Color,
    val border: Color,
    val borderGlow: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textMuted: Color,
    val primaryAccent: Color,
    val primaryAccentLight: Color,
    val primaryAccentGlow: Color,
    val success: Color,
    val successLight: Color,
    val successGlow: Color,
    val error: Color,
    val errorLight: Color,
    val errorGlow: Color,
    val warning: Color,
    val inputBackground: Color,
    val inputBorder: Color,
    val navContainer: Color,
    val navSelectedIcon: Color,
    val navSelectedIndicator: Color,
    val watermarkColor: Color
)

val DarkBankingColorScheme = BankingColorScheme(
    isDark = true,
    background = Color(0xFF07090E),
    backgroundElevated = Color(0xFF0D111A),
    surfaceCard = Color(0xFF131926),
    surfaceCardElevated = Color(0xFF1D263B),
    surfaceGlass = Color(0xCC131926),
    border = Color(0xFF253046),
    borderGlow = Color(0xFF3D4F72),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFF94A3B8),
    textTertiary = Color(0xFF64748B),
    textMuted = Color(0xFF475569),
    primaryAccent = Color(0xFFE5C378), // Champagne Gold
    primaryAccentLight = Color(0xFFF7ECD1),
    primaryAccentGlow = Color(0x33E5C378),
    success = Color(0xFF10B981),
    successLight = Color(0xFF34D399),
    successGlow = Color(0x2610B981),
    error = Color(0xFFF43F5E),
    errorLight = Color(0xFFFB7185),
    errorGlow = Color(0x26F43F5E),
    warning = Color(0xFFF59E0B),
    inputBackground = Color(0xFF131926),
    inputBorder = Color(0xFF253046),
    navContainer = Color(0xFF0D111A),
    navSelectedIcon = Color(0xFF07090E),
    navSelectedIndicator = Color(0xFFE5C378),
    watermarkColor = Color(0x0DE5C378)
)

val LightBankingColorScheme = BankingColorScheme(
    isDark = false,
    background = Color(0xFFF6F8FA), // Alabaster
    backgroundElevated = Color(0xFFFFFFFF),
    surfaceCard = Color(0xFFFFFFFF),
    surfaceCardElevated = Color(0xFFF8FAFC),
    surfaceGlass = Color(0xF2FFFFFF),
    border = Color(0xFFE2E8F0),
    borderGlow = Color(0xFFCBD5E1),
    textPrimary = Color(0xFF0F172A), // Crisp slate black
    textSecondary = Color(0xFF475569),
    textTertiary = Color(0xFF64748B),
    textMuted = Color(0xFF94A3B8),
    primaryAccent = Color(0xFFB48328), // Deep warm gold with AAA contrast
    primaryAccentLight = Color(0xFFFEF3C7),
    primaryAccentGlow = Color(0x20B48328),
    success = Color(0xFF059669),
    successLight = Color(0xFFD1FAE5),
    successGlow = Color(0x20059669),
    error = Color(0xFFE11D48),
    errorLight = Color(0xFFFFE4E6),
    errorGlow = Color(0x20E11D48),
    warning = Color(0xFFD97706),
    inputBackground = Color(0xFFFFFFFF),
    inputBorder = Color(0xFFCBD5E1),
    navContainer = Color(0xFFFFFFFF),
    navSelectedIcon = Color(0xFFFFFFFF),
    navSelectedIndicator = Color(0xFFB48328),
    watermarkColor = Color(0x0DB48328)
)

val LocalBankingColors = staticCompositionLocalOf { DarkBankingColorScheme }

object BankingTheme {
    val colors: BankingColorScheme
        @Composable
        get() = LocalBankingColors.current
}
