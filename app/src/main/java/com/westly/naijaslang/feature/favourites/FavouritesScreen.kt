package com.westly.naijaslang.feature.favourites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.westly.naijaslang.R
import com.westly.naijaslang.core.theme.NaijaTheme
import com.westly.naijaslang.core.ui.AppHeader

/**
 * Phase 2: header, title and the real empty-state message.
 * The list of saved words comes with the hearts in Phase 4.
 */
@Composable
fun FavouritesScreen() {
    val colors = NaijaTheme.colors

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader()

        Text(
            text = stringResource(R.string.favourites_title),
            style = MaterialTheme.typography.headlineLarge,
            color = colors.wordTitle,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp),
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.favourites_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}
