package com.westly.naijaslang.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.westly.naijaslang.R
import com.westly.naijaslang.core.data.AppData
import com.westly.naijaslang.core.data.BrowseRow
import com.westly.naijaslang.core.data.DictionaryEntry
import com.westly.naijaslang.core.data.DictionaryIndex
import com.westly.naijaslang.core.data.DictionaryState
import com.westly.naijaslang.core.theme.NaijaTheme
import com.westly.naijaslang.core.ui.AppHeader
import com.westly.naijaslang.core.ui.HeaderVariant
import kotlinx.coroutines.launch

/**
 * The A to Z list with the side bar, hearts, and live search.
 * Tapping a card opens its Word Detail (Phase 4); the heart toggles on its own.
 */
@Composable
fun HomeScreen(onOpenSettings: () -> Unit, onOpenWord: (String) -> Unit) {
    val dictionaryState by AppData.dictionary.collectAsState()
    val favouriteIds by AppData.favouriteIds.collectAsState()
    val index = (dictionaryState as? DictionaryState.Ready)?.index

    var query by rememberSaveable { mutableStateOf("") }
    val trimmedQuery = query.trim()

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(
            variant = HeaderVariant.Home(onSettingsClick = onOpenSettings),
            bottomContent = {
                SearchBar(query = query, onQueryChange = { query = it })
            },
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            if (index != null) {
                if (trimmedQuery.isEmpty()) {
                    BrowseList(index = index, favouriteIds = favouriteIds, onOpenWord = onOpenWord)
                } else {
                    SearchResults(
                        index = index,
                        query = trimmedQuery,
                        favouriteIds = favouriteIds,
                        onOpenWord = onOpenWord,
                    )
                }
            }
        }
    }
}

/** The full A to Z list with letter headings and the side bar. */
@Composable
private fun BrowseList(
    index: DictionaryIndex,
    favouriteIds: Set<String>,
    onOpenWord: (String) -> Unit,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // The letter under the finger while the side bar is being used.
    var touchedLetter by remember { mutableStateOf<String?>(null) }

    // The letter of the first visible row. At the very end of the list (where a short
    // last letter such as Z can never reach the top) the last letter is shown.
    val scrolledLetter by remember(index) {
        derivedStateOf {
            val rows = index.rows
            if (rows.isEmpty()) {
                null
            } else {
                val position = if (listState.canScrollBackward && !listState.canScrollForward) {
                    rows.lastIndex
                } else {
                    listState.firstVisibleItemIndex
                }
                rows[position.coerceIn(0, rows.lastIndex)].letter
            }
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentPadding = PaddingValues(start = 16.dp, end = 12.dp, top = 4.dp, bottom = 16.dp),
        ) {
            items(
                items = index.rows,
                key = { row ->
                    when (row) {
                        is BrowseRow.Header -> "header-${row.letter}"
                        is BrowseRow.Word -> row.entry.id
                    }
                },
                contentType = { row -> if (row is BrowseRow.Header) "header" else "word" },
            ) { row ->
                when (row) {
                    is BrowseRow.Header -> LetterHeading(row.letter)
                    is BrowseRow.Word -> WordCard(
                        entry = row.entry,
                        isFavourite = row.entry.id in favouriteIds,
                        onToggleFavourite = { AppData.toggleFavourite(row.entry.id) },
                        onClick = { onOpenWord(row.entry.id) },
                    )
                }
            }
        }

        AlphabetSideBar(
            available = index.availableLetters,
            current = touchedLetter ?: scrolledLetter,
            onLetterSelected = { letter ->
                touchedLetter = letter
                val row = index.firstRowOfLetter[letter]
                if (row != null) {
                    scope.launch { listState.scrollToItem(row) }
                }
            },
            onTouchEnd = { touchedLetter = null },
        )
    }
}

/** Search results as a flat list: no letter headings, no side bar. */
@Composable
private fun SearchResults(
    index: DictionaryIndex,
    query: String,
    favouriteIds: Set<String>,
    onOpenWord: (String) -> Unit,
) {
    val colors = NaijaTheme.colors
    val results = remember(index, query) { index.search(query) }
    val listState = rememberLazyListState()

    // Every new search starts at the top. Coming back from Word Detail keeps the position
    // (the last searched text is remembered, so the same text does not scroll to the top).
    var lastQuery by rememberSaveable { mutableStateOf(query) }
    LaunchedEffect(query) {
        if (query != lastQuery) {
            listState.scrollToItem(0)
            lastQuery = query
        }
    }

    if (results.isEmpty()) {
        Text(
            text = stringResource(R.string.search_no_results, query),
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 40.dp),
        )
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
        ) {
            items(items = results, key = { it.id }) { entry ->
                WordCard(
                    entry = entry,
                    isFavourite = entry.id in favouriteIds,
                    onToggleFavourite = { AppData.toggleFavourite(entry.id) },
                    onClick = { onOpenWord(entry.id) },
                )
            }
        }
    }
}

@Composable
private fun LetterHeading(letter: String) {
    Text(
        text = letter,
        style = MaterialTheme.typography.headlineMedium,
        color = NaijaTheme.colors.wordTitle,
        modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 8.dp),
    )
}

/** A rounded card: word, meaning (two lines at most) and the heart. Tapping the card calls [onClick]. */
@Composable
internal fun WordCard(
    entry: DictionaryEntry,
    isFavourite: Boolean,
    onToggleFavourite: () -> Unit,
    onClick: () -> Unit,
) {
    val colors = NaijaTheme.colors

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = MaterialTheme.shapes.medium,
        color = colors.card,
        shadowElevation = if (colors.isDark) 0.dp else 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.word,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.wordTitle,
                )
                Text(
                    text = entry.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(
                onClick = onToggleFavourite,
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavourite) {
                        stringResource(R.string.cd_favourite_remove, entry.word)
                    } else {
                        stringResource(R.string.cd_favourite_add, entry.word)
                    },
                    tint = colors.gold,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    val colors = NaijaTheme.colors
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = CircleShape,
        color = colors.searchBar,
        border = if (colors.isDark) BorderStroke(1.dp, colors.searchBarBorder) else null,
        shadowElevation = if (colors.isDark) 0.dp else 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 18.dp, end = if (query.isNotEmpty()) 8.dp else 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.cd_search),
                tint = colors.searchIcon,
                modifier = Modifier.size(26.dp),
            )
            Spacer(Modifier.width(14.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(colors.searchIcon),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.searchPlaceholder,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.cd_clear_search),
                        tint = colors.searchIcon,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}
