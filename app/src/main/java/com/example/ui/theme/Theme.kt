package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LeadDarkColorScheme = darkColorScheme(
    primary = ChampagneGold,
    onPrimary = Obsidian950,
    primaryContainer = Color(0xFF243048),
    onPrimaryContainer = ChampagneLight,
    secondary = EmeraldGreen,
    onSecondary = Obsidian950,
    secondaryContainer = Color(0xFF1D263B),
    onSecondaryContainer = EmeraldGreenLight,
    tertiary = ElectricCyan,
    onTertiary = Obsidian950,
    background = Color(0xFF07090E),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0D111A),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF131926),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF253046),
    outlineVariant = Color(0xFF3D4F72),
    error = CrimsonExpense,
    onError = Color.White
)

private val LeadLightColorScheme = lightColorScheme(
    primary = Color(0xFFB48328),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = Color(0xFF059669),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    background = Color(0xFFF6F8FA),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFCBD5E1),
    error = Color(0xFFE11D48),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val bankingColors = if (darkTheme) DarkBankingColorScheme else LightBankingColorScheme
    val materialScheme = if (darkTheme) LeadDarkColorScheme else LeadLightColorScheme

    CompositionLocalProvider(LocalBankingColors provides bankingColors) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = Typography,
            content = content
        )
    }
}
