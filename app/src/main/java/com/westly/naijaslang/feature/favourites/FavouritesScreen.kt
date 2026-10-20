package com.westly.naijaslang.feature.favourites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.westly.naijaslang.R
import com.westly.naijaslang.core.data.AppData
import com.westly.naijaslang.core.data.DictionaryState
import com.westly.naijaslang.core.theme.NaijaTheme
import com.westly.naijaslang.core.ui.AppHeader
import com.westly.naijaslang.feature.home.WordCard

/**
 * Phase 4: the saved words, A to Z. Tapping a card opens its Word Detail;
 * tapping the filled heart removes the word from the list straight away.
 */
@Composable
fun FavouritesScreen(onOpenWord: (String) -> Unit) {
    val colors = NaijaTheme.colors
    val dictionaryState by AppData.dictionary.collectAsState()
    val favouriteIds by AppData.favouriteIds.collectAsState()
    val index = (dictionaryState as? DictionaryState.Ready)?.index

    // The index is already in A to Z order (case and accents ignored).
    val saved = remember(index, favouriteIds) {
        index?.entries?.filter { it.id in favouriteIds }.orEmpty()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader()

        Text(
            text = stringResource(R.string.favourites_title),
            style = MaterialTheme.typography.headlineLarge,
            color = colors.wordTitle,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
        )

        if (index != null && saved.isEmpty()) {
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
            ) {
                items(items = saved, key = { it.id }) { entry ->
                    WordCard(
                        entry = entry,
                        isFavourite = true,
                        onToggleFavourite = { AppData.toggleFavourite(entry.id) },
                        onClick = { onOpenWord(entry.id) },
                    )
                }
            }
        }
    }
}
