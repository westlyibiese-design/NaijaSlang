package com.westly.naijaslang.core.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform

/*
 * The Ankara / Adire inspired pattern. The geometry is copied from the <pattern>
 * of Splash_Light_Green.html: one 200 x 200 tile with
 *   - a ring with a dot,
 *   - a cross,
 *   - two stacked chevrons,
 *   - two short vertical bars,
 *   - two short horizontal bars,
 *   - a 3 x 3 grid of dots.
 * The same tile is used by the animated splash and by the header of every screen.
 */

private const val TILE = 200f
private const val STROKE_WIDTH = 8f

/** Everything that is drawn as an outline (stroke-width 8, round caps and joins). */
private val strokeShapes: Path = Path().apply {
    // circle cx=50 cy=50 r=26
    addOval(Rect(center = Offset(50f, 50f), radius = 26f))
    // cross
    moveTo(122f, 22f); lineTo(178f, 78f)
    moveTo(178f, 22f); lineTo(122f, 78f)
    // two chevrons
    moveTo(24f, 130f); lineTo(50f, 156f); lineTo(76f, 130f)
    moveTo(24f, 156f); lineTo(50f, 182f); lineTo(76f, 156f)
    // vertical bars
    moveTo(100f, 30f); lineTo(100f, 70f)
    moveTo(100f, 130f); lineTo(100f, 170f)
    // horizontal bars
    moveTo(8f, 100f); lineTo(40f, 100f)
    moveTo(160f, 100f); lineTo(192f, 100f)
}

/** Everything that is drawn as a filled dot. */
private val fillShapes: Path = Path().apply {
    addOval(Rect(center = Offset(50f, 50f), radius = 8f))
    for (y in floatArrayOf(130f, 150f, 170f)) {
        for (x in floatArrayOf(130f, 150f, 170f)) {
            addOval(Rect(center = Offset(x, y), radius = 5f))
        }
    }
}

private val strokeStyle = Stroke(width = STROKE_WIDTH, cap = StrokeCap.Round, join = StrokeJoin.Round)

/**
 * Fills the whole drawing area with the repeating pattern.
 *
 * @param tilePx size of one tile in pixels (the splash uses 200 dp).
 * @param phaseX moves the pattern sideways, 0 until [tilePx]. Used by the splash drift.
 * @param phaseY moves the pattern down, 0 until [tilePx].
 * @param color colour of the pattern, with its own alpha (the design uses white at 7 %).
 */
fun DrawScope.drawAnkaraPattern(
    tilePx: Float,
    phaseX: Float,
    phaseY: Float,
    color: Color,
) {
    val k = tilePx / TILE
    var y = phaseY - tilePx
    while (y < size.height) {
        var x = phaseX - tilePx
        while (x < size.width) {
            withTransform({
                translate(x, y)
                scale(k, k, Offset.Zero)
            }) {
                drawPath(path = strokeShapes, color = color, style = strokeStyle)
                drawPath(path = fillShapes, color = color)
            }
            x += tilePx
        }
        y += tilePx
    }
}
