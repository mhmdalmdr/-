package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalSudXDarkTheme = staticCompositionLocalOf { false }

// SudX Luxury Dark Color Scheme (Obsidian Navy + Champagne Gold + Warm Ivory)
private val DarkColorScheme = darkColorScheme(
    primary = SudXChampagneGold,
    onPrimary = SudXMidnightNavy,
    primaryContainer = SudXNavySoftDark,
    onPrimaryContainer = SudXSoftGold,
    secondary = SudXIndigoMist,
    onSecondary = Color.White,
    secondaryContainer = SudXGoldTintDark,
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
    outlineVariant = SudXObsidianBorder.copy(alpha = 0.72f)
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
@ReadOnlyComposable
fun sudxIsDarkTheme(): Boolean = LocalSudXDarkTheme.current

@Composable
@ReadOnlyComposable
fun sudxGoldTintBg(): Color = if (LocalSudXDarkTheme.current) SudXGoldTintDark else SudXGoldTint

@Composable
@ReadOnlyComposable
fun sudxGoldFg(): Color = if (LocalSudXDarkTheme.current) SudXSoftGold else SudXBronzeGold

@Composable
@ReadOnlyComposable
fun sudxGoldBorder(): Color = if (LocalSudXDarkTheme.current) SudXGoldBorderDark else SudXGoldBorder

@Composable
@ReadOnlyComposable
fun sudxNavySoftBg(): Color = if (LocalSudXDarkTheme.current) SudXNavySoftDark else SudXNavySoft

@Composable
@ReadOnlyComposable
fun sudxNavyFg(): Color = if (LocalSudXDarkTheme.current) SudXNavyTextDark else SudXRoyalNavy

@Composable
@ReadOnlyComposable
fun sudxEmeraldSoftBg(): Color = if (LocalSudXDarkTheme.current) SudXEmeraldSoftDark else SudXEmeraldSoft

@Composable
@ReadOnlyComposable
fun sudxEmeraldFg(): Color = if (LocalSudXDarkTheme.current) SudXEmeraldBright else SudXEmeraldDark

@Composable
@ReadOnlyComposable
fun sudxCrimsonSoftBg(): Color = if (LocalSudXDarkTheme.current) SudXCrimsonSoftDark else SudXCrimsonSoft

@Composable
@ReadOnlyComposable
fun sudxCrimsonFg(): Color = if (LocalSudXDarkTheme.current) SudXCrimsonBright else SudXCrimson

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalSudXDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
