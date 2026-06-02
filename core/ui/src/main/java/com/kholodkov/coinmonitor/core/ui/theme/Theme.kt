package com.kholodkov.coinmonitor.core.ui.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Immutable
data class ExtendedColorScheme(
    val positiveAmount: Color,
    val negativeAmount: Color,
    val overduePurchaseContent: Color,
    val overduePurchaseContainer: Color,
    val boughtPurchaseContent: Color,
    val boughtPurchaseContainer: Color,
    val availablePurchaseContent: Color,
    val availablePurchaseContainer: Color,
    val unreachablePurchaseContent: Color,
    val unreachablePurchaseContainer: Color,
    val plannedPurchaseContent: Color,
    val plannedPurchaseContainer: Color,
)

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val lightExtended = ExtendedColorScheme(
    positiveAmount = positiveAmountLight,
    negativeAmount = negativeAmountLight,
    overduePurchaseContent = overduePurchaseContentLight,
    overduePurchaseContainer = overduePurchaseContainerLight,
    boughtPurchaseContent = boughtPurchaseContentLight,
    boughtPurchaseContainer = boughtPurchaseContainerLight,
    availablePurchaseContent = availablePurchaseContentLight,
    availablePurchaseContainer = availablePurchaseContainerLight,
    unreachablePurchaseContent = unreachablePurchaseContentLight,
    unreachablePurchaseContainer = unreachablePurchaseContainerLight,
    plannedPurchaseContent = plannedPurchaseContentLight,
    plannedPurchaseContainer = plannedPurchaseContainerLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val darkExtended = ExtendedColorScheme(
    positiveAmount = positiveAmountDark,
    negativeAmount = negativeAmountDark,
    overduePurchaseContent = overduePurchaseContentDark,
    overduePurchaseContainer = overduePurchaseContainerDark,
    boughtPurchaseContent = boughtPurchaseContentDark,
    boughtPurchaseContainer = boughtPurchaseContainerDark,
    availablePurchaseContent = availablePurchaseContentDark,
    availablePurchaseContainer = availablePurchaseContainerDark,
    unreachablePurchaseContent = unreachablePurchaseContentDark,
    unreachablePurchaseContainer = unreachablePurchaseContainerDark,
    plannedPurchaseContent = plannedPurchaseContentDark,
    plannedPurchaseContainer = plannedPurchaseContainerDark,
)


@Composable
fun CoinMonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }

    val extendedColors = if (darkTheme) darkExtended else lightExtended

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

val LocalExtendedColors = staticCompositionLocalOf { lightExtended }
