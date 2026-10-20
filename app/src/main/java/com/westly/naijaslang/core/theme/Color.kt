package com.westly.naijaslang.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/*
 * The one colour file of the app.
 * Brand colours come from the splash HTML files. Everything else was sampled
 * with code from the light and dark screenshots of the design.
 */

// ---------- Brand ----------
val GreenDeep = Color(0xFF04502D)
val GreenBrand = Color(0xFF0A6B3A)
val Gold = Color(0xFFD4A017)
val Cream = Color(0xFFF6E7B0)

// ---------- Animated splash ----------
// Light splash (green)
val SplashLightDeep = Color(0xFF04502D)
val SplashLightCenter = Color(0xFF0A6B3A)
val SplashLightMid = Color(0xFF07603A)

// Dark splash
val SplashDarkDeep = Color(0xFF11151A)
val SplashDarkCenter = Color(0xFF1B2028)
val SplashDarkMid = Color(0xFF161B22)

// ---------- Light theme ----------
internal val LightBackground = Color(0xFFF8F6F2)
internal val LightCard = Color(0xFFFFFFFF)
internal val LightHeaderGreen = Color(0xFF035C37)
internal val LightWordTitle = Color(0xFF04502D)
internal val LightTextPrimary = Color(0xFF14201A)
internal val LightTextSecondary = Color(0xFF5B6270)
internal val LightPlaceholder = Color(0xFF6B7480)
internal val LightDivider = Color(0xFFE6E6E0)
internal val LightNavBar = Color(0xFFFFFFFF)
internal val LightNavUnselected = Color(0xFF4F6B5C)
internal val LightSearchBar = Color(0xFFFFFFFF)
internal val LightSearchBorder = Color(0x00000000)
internal val LightSearchIcon = Color(0xFF04502D)
internal val LightDetailBackground = Color(0xFFFFFAEE)
internal val LightExampleBox = Color(0xFFFAF6EC)
internal val LightExampleBoxBorder = Color(0x00000000)
internal val LightSectionLabel = Color(0xFF6E6E6E)
internal val LightCopyButton = Color(0xFFC49428)
internal val LightCardBorder = Color(0x00000000)
internal val LightSwitchTrackOff = Color(0xFFCDD0D5)
internal val LightSwitchThumbOff = Color(0xFFFFFFFF)
internal val LightSliderInactive = Color(0xFFEAEEED)

// ---------- Dark theme ----------
internal val DarkBackground = Color(0xFF151A20)
internal val DarkCard = Color(0xFF1E262C)
internal val DarkHeaderGreen = Color(0xFF04512F)
internal val DarkWordTitle = Color(0xFF33B570)
internal val DarkTextPrimary = Color(0xFFF1F5F8)
internal val DarkTextSecondary = Color(0xFFC9D3DA)
internal val DarkPlaceholder = Color(0xFFB8BCC2)
internal val DarkDivider = Color(0xFF2A323A)
internal val DarkNavBar = Color(0xFF1E262C)
internal val DarkNavUnselected = Color(0xFFCBD0D5)
internal val DarkSearchBar = Color(0xFF2A323A)
internal val DarkSearchBorder = Color(0xFF3A444C)
internal val DarkSearchIcon = Color(0xFF4FBD7E)
internal val DarkDetailBackground = Color(0xFF161C22)
internal val DarkExampleBox = Color(0xFF332F24)
internal val DarkExampleBoxBorder = Color(0xFF4A402A)
internal val DarkSectionLabel = Color(0xFF2BA05A)
internal val DarkCopyButton = Color(0xFFB27D13)
internal val DarkCardBorder = Color(0xFF2A323A)
internal val DarkSwitchTrackOff = Color(0xFF3A4557)
internal val DarkSwitchThumbOff = Color(0xFFDCDFE8)
internal val DarkSliderInactive = Color(0xFF334255)

/**
 * Colours the design needs that Material 3 has no slot for.
 * Read them with `NaijaTheme.colors`.
 */
@Immutable
class NaijaColors(
    val isDark: Boolean,
    val gold: Color,
    val cream: Color,
    val headerGreen: Color,
    val onHeader: Color,
    val headerTagline: Color,
    val wordTitle: Color,
    val card: Color,
    val divider: Color,
    val textSecondary: Color,
    val searchBar: Color,
    val searchBarBorder: Color,
    val searchIcon: Color,
    val searchPlaceholder: Color,
    val navBar: Color,
    val navUnselected: Color,
    val detailBackground: Color,
    val exampleBox: Color,
    val exampleBoxBorder: Color,
    val sectionLabel: Color,
    val copyButton: Color,
    val cardBorder: Color,
    val switchTrackOff: Color,
    val switchThumbOff: Color,
    val sliderInactive: Color,
)

internal val LightNaijaColors = NaijaColors(
    isDark = false,
    gold = Gold,
    cream = Cream,
    headerGreen = LightHeaderGreen,
    onHeader = Color.White,
    headerTagline = Cream,
    wordTitle = LightWordTitle,
    card = LightCard,
    divider = LightDivider,
    textSecondary = LightTextSecondary,
    searchBar = LightSearchBar,
    searchBarBorder = LightSearchBorder,
    searchIcon = LightSearchIcon,
    searchPlaceholder = LightPlaceholder,
    navBar = LightNavBar,
    navUnselected = LightNavUnselected,
    detailBackground = LightDetailBackground,
    exampleBox = LightExampleBox,
    exampleBoxBorder = LightExampleBoxBorder,
    sectionLabel = LightSectionLabel,
    copyButton = LightCopyButton,
    cardBorder = LightCardBorder,
    switchTrackOff = LightSwitchTrackOff,
    switchThumbOff = LightSwitchThumbOff,
    sliderInactive = LightSliderInactive,
)

internal val DarkNaijaColors = NaijaColors(
    isDark = true,
    gold = Gold,
    cream = Cream,
    headerGreen = DarkHeaderGreen,
    onHeader = Color.White,
    headerTagline = Cream,
    wordTitle = DarkWordTitle,
    card = DarkCard,
    divider = DarkDivider,
    textSecondary = DarkTextSecondary,
    searchBar = DarkSearchBar,
    searchBarBorder = DarkSearchBorder,
    searchIcon = DarkSearchIcon,
    searchPlaceholder = DarkPlaceholder,
    navBar = DarkNavBar,
    navUnselected = DarkNavUnselected,
    detailBackground = DarkDetailBackground,
    exampleBox = DarkExampleBox,
    exampleBoxBorder = DarkExampleBoxBorder,
    sectionLabel = DarkSectionLabel,
    copyButton = DarkCopyButton,
    cardBorder = DarkCardBorder,
    switchTrackOff = DarkSwitchTrackOff,
    switchThumbOff = DarkSwitchThumbOff,
    sliderInactive = DarkSliderInactive,
)

internal val LocalNaijaColors = staticCompositionLocalOf { LightNaijaColors }
