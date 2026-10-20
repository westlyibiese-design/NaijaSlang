package com.westly.naijaslang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.westly.naijaslang.core.data.AppData
import com.westly.naijaslang.core.prefs.PreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Must come first. Shows the plain-colour system splash (blank, no icon)
        // and then switches to the normal theme.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // Starts loading the dictionary on a background thread while the splash plays.
        AppData.start(applicationContext)
        // Light icons on the splash. NaijaSlangApp adjusts the bars once the splash is gone.
        val transparent = android.graphics.Color.TRANSPARENT
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(transparent),
            navigationBarStyle = SystemBarStyle.dark(transparent),
        )

        val repository = PreferencesRepository(applicationContext)

        // One tiny read of the saved settings before the first frame, so the very
        // first thing drawn already has the right (light or dark) splash and text size.
        val initialPreferences = runBlocking { repository.preferences.first() }

        setContent {
            val preferences by repository.preferences.collectAsState(initial = initialPreferences)
            NaijaSlangApp(preferences)
        }
    }
}
