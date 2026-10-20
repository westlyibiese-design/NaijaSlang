package com.westly.naijaslang

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.westly.naijaslang.core.data.AppData
import com.westly.naijaslang.core.prefs.UserPreferences
import com.westly.naijaslang.core.theme.NaijaSlangTheme
import com.westly.naijaslang.feature.splash.SplashScreen
import com.westly.naijaslang.navigation.AppNavigation

/**
 * Root of the app: theme + animated splash on top of the navigation.
 *
 * The splash is composed first. The rest of the app is composed one frame
 * later, behind the splash, so the splash is always the first thing drawn.
 */
@Composable
fun NaijaSlangApp(preferences: UserPreferences) {
    // The saved choice wins; until a choice is saved the app follows the phone.
    val darkTheme = preferences.darkMode ?: isSystemInDarkTheme()

    // Survives rotation / theme changes of the phone, so the splash does not replay.
    var splashDone by rememberSaveable { mutableStateOf(false) }

    // True when the dictionary and the saved favourites have loaded; the splash waits for it.
    val dataReady by AppData.ready.collectAsState()

    var appComposed by remember { mutableStateOf(splashDone) }
    LaunchedEffect(Unit) {
        withFrameNanos { }
        appComposed = true
    }

    SystemBars(darkTheme = darkTheme, splashVisible = !splashDone)

    NaijaSlangTheme(darkTheme = darkTheme, textSizeStep = preferences.textSizeStep) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (appComposed) {
                AppNavigation()
            }
            if (!splashDone) {
                SplashScreen(
                    darkTheme = darkTheme,
                    dataReady = dataReady,
                    onFinished = { splashDone = true },
                )
            }
        }
    }
}

/**
 * Edge to edge with the right icon colours:
 *  - status bar icons are always light (the header is always green);
 *  - navigation bar icons are light on the splash and in the dark theme, dark in the light theme.
 */
@Composable
private fun SystemBars(darkTheme: Boolean, splashVisible: Boolean) {
    val activity = LocalContext.current as ComponentActivity
    DisposableEffect(darkTheme, splashVisible) {
        val transparent = android.graphics.Color.TRANSPARENT
        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(transparent),
            navigationBarStyle = if (darkTheme || splashVisible) {
                SystemBarStyle.dark(transparent)
            } else {
                SystemBarStyle.light(transparent, transparent)
            },
        )
        onDispose { }
    }
}
