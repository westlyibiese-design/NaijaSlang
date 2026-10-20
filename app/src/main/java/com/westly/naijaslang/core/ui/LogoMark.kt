package com.westly.naijaslang.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import com.westly.naijaslang.core.theme.Gold

/*
 * The logo: a white speech bubble with a gold open book.
 * Geometry copied from the <svg viewBox="0 0 200 196"> of the splash HTML files.
 * All functions below draw in those 200 x 196 "logo units"; the caller scales.
 */

const val LOGO_WIDTH = 200f
const val LOGO_HEIGHT = 196f

/** Point the book pages open from (transform-origin 100px 90px in the HTML). */
val LogoBookPivot = Offset(100f, 90f)

private val bubbleTail = Path().apply {
    moveTo(44f, 148f); lineTo(24f, 190f); lineTo(88f, 166f); close()
}

private val leftPage = Path().apply {
    moveTo(96f, 56f)
    cubicTo(84f, 47f, 62f, 46f, 46f, 51f)
    lineTo(46f, 112f)
    cubicTo(62f, 107f, 84f, 109f, 96f, 118f)
    close()
}

private val rightPage = Path().apply {
    moveTo(104f, 56f)
    cubicTo(116f, 47f, 138f, 46f, 154f, 51f)
    lineTo(154f, 112f)
    cubicTo(138f, 107f, 116f, 109f, 104f, 118f)
    close()
}

private val bookCover = Path().apply {
    moveTo(40f, 58f)
    lineTo(40f, 120f)
    cubicTo(62f, 115f, 88f, 117f, 100f, 126f)
    cubicTo(112f, 117f, 138f, 115f, 160f, 120f)
    lineTo(160f, 58f)
}

private val coverStroke = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)

/** The white bubble (ellipse + tail). */
fun DrawScope.drawLogoBubble(alpha: Float = 1f) {
    drawOval(
        color = Color.White,
        topLeft = Offset(6f, 4f),
        size = Size(188f, 168f),
        alpha = alpha,
    )
    drawPath(path = bubbleTail, color = Color.White, alpha = alpha)
}

/** The two gold pages. [leftScaleX] / [rightScaleX] go from 0.02 (closed) to 1 (open). */
fun DrawScope.drawLogoPages(
    leftScaleX: Float,
    leftAlpha: Float,
    rightScaleX: Float,
    rightAlpha: Float,
) {
    if (leftAlpha > 0f) {
        scale(scaleX = leftScaleX, scaleY = 1f, pivot = LogoBookPivot) {
            drawPath(path = leftPage, color = Gold, alpha = leftAlpha)
        }
    }
    if (rightAlpha > 0f) {
        scale(scaleX = rightScaleX, scaleY = 1f, pivot = LogoBookPivot) {
            drawPath(path = rightPage, color = Gold, alpha = rightAlpha)
        }
    }
}

/** The gold outline under the pages. */
fun DrawScope.drawLogoCover(alpha: Float) {
    if (alpha > 0f) {
        drawPath(path = bookCover, color = Gold, alpha = alpha, style = coverStroke)
    }
}

/** The finished, still logo (used in the headers). */
@Composable
fun LogoMark(width: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.width(width).height(width * (LOGO_HEIGHT / LOGO_WIDTH))) {
        val s = size.width / LOGO_WIDTH
        withTransform({ scale(s, s, Offset.Zero) }) {
            drawLogoBubble()
            drawLogoPages(1f, 1f, 1f, 1f)
            drawLogoCover(1f)
        }
    }
}
