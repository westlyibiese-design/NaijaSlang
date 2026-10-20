package com.westly.naijaslang.core.data

import android.content.Context
import org.json.JSONArray

private const val DICTIONARY_ASSET = "dictionary.json"

/**
 * Reads assets/dictionary.json. Call this on a background thread.
 * Entries without a word, and repeated ids, are skipped instead of crashing.
 */
internal fun loadDictionaryEntries(context: Context): List<DictionaryEntry> {
    val text = context.assets.open(DICTIONARY_ASSET).bufferedReader(Charsets.UTF_8).use { it.readText() }
    val array = JSONArray(text)
    val entries = ArrayList<DictionaryEntry>(array.length())
    val seenIds = HashSet<String>()

    for (i in 0 until array.length()) {
        val item = array.optJSONObject(i) ?: continue
        val word = item.optString("word", "").trim()
        if (word.isEmpty()) continue
        val id = item.optString("id", "").ifBlank { word }
        if (!seenIds.add(id)) continue

        val sensesArray = item.optJSONArray("senses")
        val senses = ArrayList<String>()
        if (sensesArray != null) {
            for (s in 0 until sensesArray.length()) {
                val sense = sensesArray.optString(s, "")
                if (sense.isNotBlank()) senses.add(sense)
            }
        }

        entries.add(
            DictionaryEntry(
                id = id,
                word = word,
                meaning = item.optString("meaning", ""),
                senses = senses,
                example = item.optString("example", ""),
            ),
        )
    }
    return entries
}
