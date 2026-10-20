package com.westly.naijaslang.core.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * The "copy" icon (two overlapping pages). The core icon set that the app uses
 * does not include it, so it is drawn here as a small vector.
 */
val CopyIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Copy",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).addPath(
        pathData = addPathNodes(
            "M16,1L4,1c-1.1,0 -2,0.9 -2,2l0,14l2,0L4,3l12,0L16,1z" +
                "M19,5L8,5c-1.1,0 -2,0.9 -2,2l0,14c0,1.1 0.9,2 2,2l11,0c1.1,0 2,-0.9 2,-2L21,7" +
                "c0,-1.1 -0.9,-2 -2,-2z" +
                "M19,21L8,21L8,7l11,0L19,21z",
        ),
        fill = SolidColor(Color.Black),
    ).build()
}
