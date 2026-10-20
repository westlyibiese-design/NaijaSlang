package com.westly.naijaslang.core.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/*
 * Small outline icons for the Settings rows that the core icon set does not have.
 * They are drawn with strokes, so they look like the outline icons of the design.
 */

private fun strokeIcon(name: String, pathData: String, width: Float): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).addPath(
        pathData = addPathNodes(pathData),
        stroke = SolidColor(Color.Black),
        strokeLineWidth = width,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ).build()

/** Crescent moon (Dark mode). */
val MoonIcon: ImageVector by lazy {
    strokeIcon("Moon", "M20,14.5A8.5,8.5 0 1 1 9.5,4A6.5,6.5 0 0 0 20,14.5z", 2f)
}

/** Big and small T (Text size). */
val TextSizeIcon: ImageVector by lazy {
    strokeIcon("TextSize", "M3,6.5L13,6.5M8,6.5L8,19M14,12L21,12M17.5,12L17.5,19", 2.4f)
}

/** Flag (Report a word). */
val FlagIcon: ImageVector by lazy {
    strokeIcon("Flag", "M5,21L5,4M5,4L16,4L14.5,8L16,12L5,12", 2f)
}
