package com.westly.naijaslang.core.data

import androidx.compose.runtime.Immutable
import java.text.Normalizer

/** The "#" group: entries that start with a digit (or any non A-Z character). */
const val DIGIT_GROUP = "#"

/**
 * One dictionary entry, exactly as stored in assets/dictionary.json.
 *
 * @param senses only filled for entries with two or more separate meanings
 *   (shown as a numbered list on the Word Detail screen in Phase 4).
 */
@Immutable
class DictionaryEntry(
    val id: String,
    val word: String,
    val meaning: String,
    val senses: List<String>,
    val example: String,
) {
    /** Lower case, no accents: used for matching. */
    internal val wordKey: String = normalizeForSearch(word)
    internal val meaningKey: String = normalizeForSearch(meaning)

    /** Used to put the entries in A to Z order (punctuation such as "-" is ignored). */
    internal val sortKey: String = normalizeForSort(word)

    /** "#" or "A".."Z", worked out from the word itself. */
    val group: String = groupOf(sortKey)
}

/** Lower case + accents removed + curly quotes straightened. */
internal fun normalizeForSearch(text: String): String {
    val decomposed = Normalizer.normalize(text, Normalizer.Form.NFD)
    val out = StringBuilder(decomposed.length)
    for (c in decomposed) {
        if (Character.getType(c) == Character.NON_SPACING_MARK.toInt()) continue
        out.append(
            when (c) {
                '\u2019', '\u2018' -> '\''
                '\u201C', '\u201D' -> '"'
                else -> c.lowercaseChar()
            },
        )
    }
    return out.toString()
}

private fun normalizeForSort(word: String): String {
    val key = normalizeForSearch(word)
    val out = StringBuilder(key.length)
    for (c in key) {
        if (c.isLetterOrDigit() || c == ' ') out.append(c)
    }
    return out.toString().trim()
}

private fun groupOf(sortKey: String): String {
    val first = sortKey.firstOrNull() ?: return DIGIT_GROUP
    return if (first in 'a'..'z') first.uppercaseChar().toString() else DIGIT_GROUP
}

/** One row of the A to Z list: a letter heading or a word. */
sealed interface BrowseRow {
    val letter: String

    data class Header(override val letter: String) : BrowseRow

    class Word(val entry: DictionaryEntry) : BrowseRow {
        override val letter: String get() = entry.group
    }
}

/**
 * Everything the screens need, built once from the loaded entries.
 * Nothing here knows how many entries or which letters exist.
 */
class DictionaryIndex(entries: List<DictionaryEntry>) {

    /** All entries in A to Z order, "#" group first. */
    val entries: List<DictionaryEntry> = entries.sortedWith(
        compareBy<DictionaryEntry>({ it.group }, { it.sortKey }),
    )

    /** Letter headings and words in display order. */
    val rows: List<BrowseRow>

    /** Letter ("#", "A".."Z") to the position of its heading in [rows]. */
    val firstRowOfLetter: Map<String, Int>

    /** The letters that have at least one entry. */
    val availableLetters: Set<String>

    init {
        val rowList = ArrayList<BrowseRow>(this.entries.size + 27)
        val firstRows = LinkedHashMap<String, Int>()
        var currentGroup: String? = null
        for (entry in this.entries) {
            if (entry.group != currentGroup) {
                currentGroup = entry.group
                firstRows[entry.group] = rowList.size
                rowList.add(BrowseRow.Header(entry.group))
            }
            rowList.add(BrowseRow.Word(entry))
        }
        rows = rowList
        firstRowOfLetter = firstRows
        availableLetters = firstRows.keys.toSet()
    }

    /**
     * Words and meanings that match [rawQuery], ignoring case and accents.
     * Order: exact word, words starting with the text, words containing it,
     * then meaning-only matches. Each group is A to Z.
     */
    fun search(rawQuery: String): List<DictionaryEntry> {
        val query = normalizeForSearch(rawQuery).trim()
        if (query.isEmpty()) return emptyList()

        val exact = ArrayList<DictionaryEntry>()
        val startsWith = ArrayList<DictionaryEntry>()
        val contains = ArrayList<DictionaryEntry>()
        val inMeaning = ArrayList<DictionaryEntry>()

        for (entry in entries) {
            val word = entry.wordKey
            when {
                word == query -> exact.add(entry)
                word.startsWith(query) -> startsWith.add(entry)
                word.contains(query) -> contains.add(entry)
                entry.meaningKey.contains(query) -> inMeaning.add(entry)
            }
        }
        return exact + startsWith + contains + inMeaning
    }
}
