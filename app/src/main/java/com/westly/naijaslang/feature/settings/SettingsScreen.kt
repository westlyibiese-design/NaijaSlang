package com.westly.naijaslang.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.westly.naijaslang.core.ui.AppHeader

/**
 * Phase 2: only the header. The Dark mode switch, Text size slider, About,
 * Rate, Share and Report come in Phase 5.
 */
@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader()
    }
}
