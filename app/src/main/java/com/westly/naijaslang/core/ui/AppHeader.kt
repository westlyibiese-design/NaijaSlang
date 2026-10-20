package com.westly.naijaslang.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.westly.naijaslang.R
import com.westly.naijaslang.core.theme.NaijaTheme

/**
 * Which buttons the header shows.
 *  - [Home]: settings gear on the right (opens Settings).
 *  - [Plain]: no icons (Favourites, Settings).
 *  - [Detail]: back arrow on the left, share icon on the right (used by Word Detail in Phase 4).
 *  - [Back]: back arrow on the left, nothing on the right (used by the About page).
 */
sealed interface HeaderVariant {
    data class Home(val onSettingsClick: () -> Unit) : HeaderVariant
    data object Plain : HeaderVariant
    data class Detail(val onBackClick: () -> Unit, val onShareClick: () -> Unit) : HeaderVariant
    data class Back(val onBackClick: () -> Unit) : HeaderVariant
}

private const val HEADER_TILE_DP = 120f
private const val HEADER_PATTERN_ALPHA = 0.09f

/**
 * The green Ankara header shown on every main screen: pattern, logo,
 * "Naija Slang" and the tagline. [bottomContent] is placed under the title row,
 * still on the green (the Home screen puts the search bar there).
 */
@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    variant: HeaderVariant = HeaderVariant.Plain,
    bottomContent: (@Composable () -> Unit)? = null,
) {
    val colors = NaijaTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.headerGreen)
            .drawBehind {
                drawAnkaraPattern(
                    tilePx = HEADER_TILE_DP.dp.toPx(),
                    phaseX = 0f,
                    phaseY = 0f,
                    color = Color.White.copy(alpha = HEADER_PATTERN_ALPHA),
                )
            },
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val onBackClick = when (variant) {
                    is HeaderVariant.Detail -> variant.onBackClick
                    is HeaderVariant.Back -> variant.onBackClick
                    else -> null
                }
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = colors.onHeader,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                } else {
                    Spacer(Modifier.width(8.dp))
                }

                LogoMark(width = 50.dp)
                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.brand_name),
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onHeader,
                    )
                    Text(
                        text = stringResource(R.string.brand_tagline),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.headerTagline,
                    )
                }

                when (variant) {
                    is HeaderVariant.Home -> IconButton(onClick = variant.onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.cd_settings),
                            tint = colors.onHeader,
                            modifier = Modifier.size(28.dp),
                        )
                    }

                    is HeaderVariant.Detail -> IconButton(onClick = variant.onShareClick) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = stringResource(R.string.cd_share),
                            tint = colors.onHeader,
                            modifier = Modifier.size(26.dp),
                        )
                    }

                    is HeaderVariant.Back, HeaderVariant.Plain -> Spacer(Modifier.width(8.dp))
                }
            }

            if (bottomContent != null) {
                Spacer(Modifier.height(16.dp))
                Box(modifier = Modifier.padding(end = 8.dp)) { bottomContent() }
            }
        }
    }
}
