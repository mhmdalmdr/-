package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// SudX Luxury Dark Color Scheme (Obsidian Navy + Champagne Gold + Pearl)
private val DarkColorScheme = darkColorScheme(
    primary = SudXChampagneGold,
    onPrimary = SudXMidnightNavy,
    primaryContainer = SudXRoyalNavy,
    onPrimaryContainer = SudXSoftGold,
    secondary = SudXIndigoMist,
    onSecondary = Color.White,
    secondaryContainer = SudXObsidianVariant,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = SudXSoftGold,
    onTertiary = SudXMidnightNavy,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = SudXObsidianBorder,
    outlineVariant = SudXObsidianBorder.copy(alpha = 0.6f)
)

// SudX Luxury Light Color Scheme (Royal Navy + Champagne Gold + Silk Alabaster)
private val LightColorScheme = lightColorScheme(
    primary = SudXRoyalNavy,
    onPrimary = SudXGoldTint,
    primaryContainer = SudXNavySoft,
    onPrimaryContainer = SudXMidnightNavy,
    secondary = SudXSapphireSlate,
    onSecondary = Color.White,
    secondaryContainer = SudXGoldTint,
    onSecondaryContainer = SudXMidnightNavy,
    tertiary = SudXChampagneGold,
    onTertiary = SudXMidnightNavy,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = SudXHairline,
    outlineVariant = SudXGoldBorder.copy(alpha = 0.7f)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
