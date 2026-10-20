package com.westly.naijaslang.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.westly.naijaslang.R

/**
 * Nunito is bundled inside the app (res/font/nunito.ttf, one variable font file),
 * so it works fully offline. Each weight below picks its own point on the
 * font's weight axis.
 */
@OptIn(ExperimentalTextApi::class)
private fun nunito(weight: FontWeight) = Font(
    resId = R.font.nunito,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

val Nunito = FontFamily(
    nunito(FontWeight.Normal),
    nunito(FontWeight.SemiBold),
    nunito(FontWeight.Bold),
    nunito(FontWeight.ExtraBold),
)

private fun style(
    size: TextUnit,
    weight: FontWeight,
    lineHeight: TextUnit,
    letterSpacing: TextUnit = 0.sp,
) = TextStyle(
    fontFamily = Nunito,
    fontWeight = weight,
    fontSize = size,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
)

val NaijaTypography = Typography(
    displayLarge = style(57.sp, FontWeight.ExtraBold, 64.sp),
    displayMedium = style(45.sp, FontWeight.ExtraBold, 52.sp),
    displaySmall = style(36.sp, FontWeight.ExtraBold, 44.sp),
    // Big screen titles such as "Favourites"
    headlineLarge = style(36.sp, FontWeight.ExtraBold, 44.sp),
    headlineMedium = style(28.sp, FontWeight.ExtraBold, 36.sp),
    headlineSmall = style(24.sp, FontWeight.Bold, 32.sp),
    // "Naija Slang" in the header, word titles
    titleLarge = style(26.sp, FontWeight.ExtraBold, 32.sp),
    titleMedium = style(20.sp, FontWeight.ExtraBold, 26.sp),
    titleSmall = style(16.sp, FontWeight.Bold, 22.sp),
    bodyLarge = style(16.sp, FontWeight.Normal, 24.sp),
    bodyMedium = style(14.sp, FontWeight.Normal, 20.sp),
    bodySmall = style(12.sp, FontWeight.Normal, 16.sp),
    labelLarge = style(14.sp, FontWeight.SemiBold, 20.sp),
    // Tagline, bottom tab labels
    labelMedium = style(12.sp, FontWeight.SemiBold, 16.sp),
    labelSmall = style(11.sp, FontWeight.SemiBold, 16.sp),
)
