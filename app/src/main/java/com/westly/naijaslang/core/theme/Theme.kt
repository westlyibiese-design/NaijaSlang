package com.westly.naijaslang.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.westly.naijaslang.core.prefs.UserPreferences

private val LightScheme = lightColorScheme(
    primary = LightHeaderGreen,
    onPrimary = Color.White,
    primaryContainer = LightExampleBox,
    onPrimaryContainer = LightWordTitle,
    secondary = Gold,
    onSecondary = Color.White,
    tertiary = Cream,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightCard,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCard,
    onSurfaceVariant = LightTextSecondary,
    outline = LightDivider,
    outlineVariant = LightDivider,
)

private val DarkScheme = darkColorScheme(
    primary = DarkWordTitle,
    onPrimary = DarkBackground,
    primaryContainer = DarkExampleBox,
    onPrimaryContainer = DarkWordTitle,
    secondary = Gold,
    onSecondary = DarkBackground,
    tertiary = Cream,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkDivider,
    outlineVariant = DarkDivider,
)

// Rounded shapes as in the design (cards, chips, buttons, search bar).
private val NaijaShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
)

/**
 * App theme.
 *
 * @param darkTheme true for the dark theme (already decided: the saved choice,
 *   or the phone's setting while no choice was saved).
 * @param textSizeStep 0..4, the step chosen in Settings. It is applied on top of
 *   the phone's own font size, so it scales all text in the app live.
 */
@Composable
fun NaijaSlangTheme(
    darkTheme: Boolean,
    textSizeStep: Int = UserPreferences.DEFAULT_TEXT_SIZE_STEP,
    content: @Composable () -> Unit,
) {
    val phoneDensity = LocalDensity.current
    val appDensity = remember(phoneDensity.density, phoneDensity.fontScale, textSizeStep) {
        Density(
            density = phoneDensity.density,
            fontScale = phoneDensity.fontScale * UserPreferences.textScaleFor(textSizeStep),
        )
    }

    CompositionLocalProvider(
        LocalDensity provides appDensity,
        LocalNaijaColors provides if (darkTheme) DarkNaijaColors else LightNaijaColors,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkScheme else LightScheme,
            typography = NaijaTypography,
            shapes = NaijaShapes,
            content = content,
        )
    }
}

/** Access to the extra colours: `NaijaTheme.colors.gold`. */
object NaijaTheme {
    val colors: NaijaColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNaijaColors.current
}
