package com.westly.naijaslang.core.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * What the user chose in Settings.
 *
 * @param darkMode null = no choice yet, follow the phone's dark / light setting.
 *   true / false = the user flipped the Dark mode switch.
 * @param textSizeStep 0..4 (small to large). 2 (the middle step) is the default.
 */
data class UserPreferences(
    val darkMode: Boolean? = null,
    val textSizeStep: Int = DEFAULT_TEXT_SIZE_STEP,
) {
    companion object {
        const val TEXT_SIZE_STEPS = 5
        const val DEFAULT_TEXT_SIZE_STEP = 2

        // Multipliers for the 5 steps, small to large. The middle one is 1.0 (normal).
        private val TEXT_SCALES = floatArrayOf(0.85f, 0.92f, 1.0f, 1.15f, 1.3f)

        fun textScaleFor(step: Int): Float =
            TEXT_SCALES[step.coerceIn(0, TEXT_SIZE_STEPS - 1)]
    }
}

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences",
)

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val TEXT_SIZE_STEP = intPreferencesKey("text_size_step")
    }

    val preferences: Flow<UserPreferences> = context.userPreferencesDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { prefs ->
            UserPreferences(
                darkMode = prefs[Keys.DARK_MODE],
                textSizeStep = (prefs[Keys.TEXT_SIZE_STEP] ?: UserPreferences.DEFAULT_TEXT_SIZE_STEP)
                    .coerceIn(0, UserPreferences.TEXT_SIZE_STEPS - 1),
            )
        }

    /** null removes the choice again (follow the phone). */
    suspend fun setDarkMode(darkMode: Boolean?) {
        context.userPreferencesDataStore.edit { prefs ->
            if (darkMode == null) prefs.remove(Keys.DARK_MODE) else prefs[Keys.DARK_MODE] = darkMode
        }
    }

    suspend fun setTextSizeStep(step: Int) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[Keys.TEXT_SIZE_STEP] = step.coerceIn(0, UserPreferences.TEXT_SIZE_STEPS - 1)
        }
    }
}
