package com.westly.naijaslang.core.data

import android.content.Context
import com.westly.naijaslang.core.prefs.FavouritesRepository
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DictionaryState {
    data object Loading : DictionaryState
    class Ready(val index: DictionaryIndex) : DictionaryState
    data object Failed : DictionaryState
}

/**
 * The app's data, loaded once on a background thread while the splash plays.
 * Lives as long as the app process, so turning dark mode on or off (which
 * recreates the screen) never reloads anything.
 */
object AppData {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val started = AtomicBoolean(false)

    private val dictionaryState = MutableStateFlow<DictionaryState>(DictionaryState.Loading)
    private val favouriteIdsState = MutableStateFlow<Set<String>>(emptySet())
    private val favouritesLoaded = MutableStateFlow(false)

    private var favourites: FavouritesRepository? = null

    val dictionary: StateFlow<DictionaryState> = dictionaryState.asStateFlow()

    /** Ids of the saved words. */
    val favouriteIds: StateFlow<Set<String>> = favouriteIdsState.asStateFlow()

    /**
     * True when the dictionary and the saved favourites have been read
     * (or reading failed, so the app never waits forever on the splash).
     */
    val ready: StateFlow<Boolean> = combine(dictionaryState, favouritesLoaded) { dictionaryNow, favouritesNow ->
        dictionaryNow !is DictionaryState.Loading && favouritesNow
    }.stateIn(scope, SharingStarted.Eagerly, false)

    /** Safe to call more than once; only the first call starts loading. */
    fun start(context: Context) {
        if (!started.compareAndSet(false, true)) return
        val appContext = context.applicationContext
        val repository = FavouritesRepository(appContext)
        favourites = repository

        scope.launch {
            dictionaryState.value = try {
                DictionaryState.Ready(DictionaryIndex(loadDictionaryEntries(appContext)))
            } catch (e: Exception) {
                DictionaryState.Failed
            }
        }
        scope.launch {
            repository.ids.collect { ids ->
                favouriteIdsState.value = ids
                favouritesLoaded.value = true
            }
        }
    }

    fun toggleFavourite(id: String) {
        val repository = favourites ?: return
        scope.launch { repository.toggle(id) }
    }
}
