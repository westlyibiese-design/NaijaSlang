package com.westly.naijaslang.feature.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.westly.naijaslang.R
import com.westly.naijaslang.core.data.AppData
import com.westly.naijaslang.core.data.DictionaryEntry
import com.westly.naijaslang.core.data.DictionaryState
import com.westly.naijaslang.core.theme.NaijaTheme
import com.westly.naijaslang.core.ui.AppHeader
import com.westly.naijaslang.core.ui.CopyIcon
import com.westly.naijaslang.core.ui.HeaderVariant
import kotlinx.coroutines.delay

/**
 * Phase 4: the Word Detail screen. Shows only what the data has:
 * the word, its meaning (numbered when the entry has senses), its example
 * (hidden when there is none), the heart, the share icon and Copy word.
 */
@Composable
fun WordDetailScreen(
    entryId: String,
    onBack: () -> Unit,
) {
    val colors = NaijaTheme.colors
    val context = LocalContext.current
    val dictionaryState by AppData.dictionary.collectAsState()
    val favouriteIds by AppData.favouriteIds.collectAsState()

    val index = (dictionaryState as? DictionaryState.Ready)?.index
    val entry = remember(index, entryId) { index?.entries?.firstOrNull { it.id == entryId } }

    val brandName = stringResource(R.string.brand_name)
    val exampleLabel = stringResource(R.string.detail_example)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.detailBackground),
    ) {
        AppHeader(
            variant = HeaderVariant.Detail(
                onBackClick = onBack,
                onShareClick = {
                    if (entry != null) {
                        shareEntry(context, entry, exampleLabel, brandName)
                    }
                },
            ),
        )

        if (entry != null) {
            DetailContent(
                entry = entry,
                isFavourite = entry.id in favouriteIds,
                onToggleFavourite = { AppData.toggleFavourite(entry.id) },
                onCopy = { copyWord(context, entry.word) },
            )
        }
    }
}

@Composable
private fun DetailContent(
    entry: DictionaryEntry,
    isFavourite: Boolean,
    onToggleFavourite: () -> Unit,
    onCopy: () -> Unit,
) {
    val colors = NaijaTheme.colors

    var copied by remember(entry.id) { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(1500)
            copied = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = colors.card,
            border = if (colors.isDark) BorderStroke(1.dp, colors.cardBorder) else null,
            shadowElevation = if (colors.isDark) 0.dp else 3.dp,
        ) {
            Column(modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 20.dp, bottom = 24.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = entry.word,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 40.sp,
                            lineHeight = 46.sp,
                        ),
                        color = colors.wordTitle,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp),
                    )
                    IconButton(
                        onClick = onToggleFavourite,
                        modifier = Modifier.size(52.dp),
                    ) {
                        Icon(
                            imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavourite) {
                                stringResource(R.string.cd_favourite_remove, entry.word)
                            } else {
                                stringResource(R.string.cd_favourite_add, entry.word)
                            },
                            tint = colors.gold,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }

                Column(modifier = Modifier.padding(end = 12.dp)) {
                    Spacer(Modifier.height(20.dp))
                    SectionLabel(stringResource(R.string.detail_meaning))
                    Spacer(Modifier.height(6.dp))
                    MeaningBlock(entry)

                    if (entry.example.isNotBlank()) {
                        Spacer(Modifier.height(18.dp))
                        HorizontalDivider(thickness = 1.dp, color = colors.divider)
                        Spacer(Modifier.height(18.dp))
                        SectionLabel(stringResource(R.string.detail_example))
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = colors.exampleBox,
                            border = if (colors.isDark) BorderStroke(1.dp, colors.exampleBoxBorder) else null,
                        ) {
                            Text(
                                text = entry.example,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 18.sp,
                                    lineHeight = 27.sp,
                                    fontStyle = FontStyle.Italic,
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Surface(
            onClick = {
                onCopy()
                copied = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = MaterialTheme.shapes.medium,
            color = colors.copyButton,
            shadowElevation = 3.dp,
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = CopyIcon,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = stringResource(if (copied) R.string.detail_copied else R.string.detail_copy_word),
                    style = MaterialTheme.typography.titleMedium,
                    color = androidx.compose.ui.graphics.Color.White,
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = NaijaTheme.colors.sectionLabel,
    )
}

/** Numbered list (1, 2, 3) when the entry has senses, otherwise one block of text. */
@Composable
private fun MeaningBlock(entry: DictionaryEntry) {
    val meaningStyle = MaterialTheme.typography.titleSmall.copy(fontSize = 19.sp, lineHeight = 28.sp)
    val color = MaterialTheme.colorScheme.onSurface

    if (entry.senses.isEmpty()) {
        Text(text = entry.meaning, style = meaningStyle, color = color)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            entry.senses.forEachIndexed { position, sense ->
                Row {
                    Text(
                        text = "${position + 1}.",
                        style = meaningStyle,
                        color = NaijaTheme.colors.wordTitle,
                        modifier = Modifier.width(30.dp),
                    )
                    Text(
                        text = sense,
                        style = meaningStyle,
                        color = color,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

/** Copies only the word. */
private fun copyWord(context: Context, word: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("word", word))
}

/** The meaning as one text: the entry's senses as "1. ... 2. ...", or the plain meaning. */
private fun meaningText(entry: DictionaryEntry): String =
    if (entry.senses.isEmpty()) {
        entry.meaning
    } else {
        entry.senses.mapIndexed { position, sense -> "${position + 1}. $sense" }.joinToString(" ")
    }

/** Opens the Android share sheet with: word + meaning, the example (if any), "(Naija Slang)". */
private fun shareEntry(context: Context, entry: DictionaryEntry, exampleLabel: String, brandName: String) {
    val text = buildString {
        append(entry.word).append(": ").append(meaningText(entry))
        if (entry.example.isNotBlank()) {
            append('\n').append(exampleLabel).append(": \"").append(entry.example).append('"')
        }
        append('\n').append('(').append(brandName).append(')')
    }
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(send, null))
}
