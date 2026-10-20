package com.westly.naijaslang.feature.splash

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.westly.naijaslang.R
import com.westly.naijaslang.core.theme.Cream
import com.westly.naijaslang.core.theme.Nunito
import com.westly.naijaslang.core.theme.SplashDarkCenter
import com.westly.naijaslang.core.theme.SplashDarkDeep
import com.westly.naijaslang.core.theme.SplashDarkMid
import com.westly.naijaslang.core.theme.SplashLightCenter
import com.westly.naijaslang.core.theme.SplashLightDeep
import com.westly.naijaslang.core.theme.SplashLightMid
import com.westly.naijaslang.core.ui.LOGO_HEIGHT
import com.westly.naijaslang.core.ui.LOGO_WIDTH
import com.westly.naijaslang.core.ui.drawAnkaraPattern
import com.westly.naijaslang.core.ui.drawLogoBubble
import com.westly.naijaslang.core.ui.drawLogoCover
import com.westly.naijaslang.core.ui.drawLogoPages
import kotlin.math.cos
import kotlin.math.sin

/*
 * Native Compose copy of Splash_Light_Green.html and Splash_Dark.html.
 *
 * Everything is a function of ONE clock (milliseconds since the first frame),
 * so the colours, sizes, delays, durations and easings can be compared with the
 * HTML file line by line. Numbers in comments are the values from that file.
 */

// ---------- Timings (ms) from the HTML ----------
private const val SHOW_FOR_MS = 4800f          // SHOW_FOR = 4800
private const val EXIT_MS = 650f               // .out animation .65s
private const val PATTERN_FADE_MS = 1600f      // fadeIn 1.6s
private const val DRIFT_MS = 45000f            // drift 45s linear infinite
private const val BUBBLE_DELAY = 300f          // pop .9s, delay .3s
private const val BUBBLE_MS = 900f
private const val RIPPLE_1_DELAY = 450f        // ripple 1.6s, delay .45s / .7s
private const val RIPPLE_2_DELAY = 700f
private const val RIPPLE_MS = 1600f
private const val PAGE_L_DELAY = 1000f         // openPage .8s, delay 1s / 1.06s
private const val PAGE_R_DELAY = 1060f
private const val PAGE_MS = 800f
private const val COVER_DELAY = 1350f          // fadeIn .5s, delay 1.35s
private const val COVER_MS = 500f
private const val FLOAT_DELAY = 1600f          // float 3.6s ease-in-out infinite, delay 1.6s
private const val FLOAT_MS = 3600f
private const val TITLE_DELAY = 1250f          // rise .7s, delay 1.25s + i * .055s
private const val TITLE_STEP = 55f
private const val TITLE_MS = 700f
private const val TAGLINE_DELAY = 2100f        // fadeUp .8s, delay 2.1s
private const val TAGLINE_MS = 800f
private const val LOADER_DELAY = 2400f         // fadeIn .6s, delay 2.4s
private const val LOADER_MS = 600f
private const val DOT_CYCLE_MS = 960f          // dot .96s linear infinite
private const val FOOTER_DELAY = 2600f         // fadeIn .8s, delay 2.6s
private const val FOOTER_MS = 800f

/** Time used to draw the finished frame when the phone has animations turned off. */
private const val FINISHED_MS = 100_000f

// ---------- Easings (CSS cubic-bezier values) ----------
private val EaseOut: Easing = CubicBezierEasing(0f, 0f, 0.58f, 1f)
private val EaseInOut: Easing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
private val PopEase: Easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
private val RiseEase: Easing = CubicBezierEasing(0.34f, 1.4f, 0.64f, 1f)
private val PageEase: Easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
private val ExitEase: Easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

private fun Float.unit(): Float = coerceIn(0f, 1f)

/** 0..1 progress of an animation that starts at [delay] and lasts [duration]. */
private fun progress(t: Float, delay: Float, duration: Float): Float = ((t - delay) / duration).unit()

private class SplashPalette(
    val deep: Color,
    val center: Color,
    val mid: Color,
    val vignette: Color,
) {
    companion object {
        // --green-deep / --green and the gradient stops of Splash_Light_Green.html
        val Light = SplashPalette(
            deep = SplashLightDeep,
            center = SplashLightCenter,
            mid = SplashLightMid,
            vignette = SplashLightDeep.copy(alpha = 0.55f),
        )

        // The same file with the dark colours of Splash_Dark.html
        val Dark = SplashPalette(
            deep = SplashDarkDeep,
            center = SplashDarkCenter,
            mid = SplashDarkMid,
            vignette = SplashDarkDeep.copy(alpha = 0.6f),
        )
    }
}

private fun animationsTurnedOff(context: Context): Boolean = try {
    Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
} catch (e: Exception) {
    false
}

/**
 * The animated splash.
 *
 * @param darkTheme dark splash when true, green splash when false.
 * @param dataReady Phase 3 will set this to true when the dictionary has loaded.
 *   The splash leaves only after its 4.8 seconds AND when this is true.
 *   Until then it defaults to true, so nothing waits.
 * @param onFinished called once, after the splash has faded out.
 */
@Composable
fun SplashScreen(
    darkTheme: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    dataReady: Boolean = true,
) {
    val palette = if (darkTheme) SplashPalette.Dark else SplashPalette.Light
    val context = LocalContext.current
    val reduceMotion = remember { animationsTurnedOff(context) }

    val currentOnFinished by rememberUpdatedState(onFinished)
    val currentDataReady by rememberUpdatedState(dataReady)

    // The one clock (ms) and the progress of the exit (0..1).
    val clock = remember { mutableFloatStateOf(0f) }
    val leave = remember { mutableFloatStateOf(0f) }
    // Read inside draw / graphicsLayer blocks only, so the clock never recomposes anything.
    val time: () -> Float = remember(reduceMotion) {
        { if (reduceMotion) FINISHED_MS else clock.floatValue }
    }

    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        var leaveStartedAt = -1f
        while (true) {
            val now = withFrameNanos { it }
            val t = (now - start) / 1_000_000f
            clock.floatValue = t
            if (leaveStartedAt < 0f && t >= SHOW_FOR_MS && currentDataReady) {
                leaveStartedAt = t
            }
            if (leaveStartedAt >= 0f) {
                val p = ((t - leaveStartedAt) / EXIT_MS).unit()
                leave.floatValue = p
                if (p >= 1f) {
                    currentOnFinished()
                    break
                }
            }
        }
    }

    val brandName = stringResource(R.string.brand_name)
    val words = remember(brandName) { brandName.split(" ") }
    val tagline = stringResource(R.string.brand_tagline)
    val copyright = stringResource(R.string.copyright)

    val config = LocalConfiguration.current
    val widthDp = config.screenWidthDp.toFloat()
    val heightDp = config.screenHeightDp.toFloat()

    // clamp(min, vw / vh share, max) values from the CSS
    val logoWidth = (widthDp * 0.42f).coerceIn(150f, 230f).dp
    val logoMarginBottom = (heightDp * 0.04f).coerceIn(18f, 34f).dp
    val titleSize = (widthDp * 0.17f).coerceIn(51.2f, 89.6f).dp
    val taglineSize = (widthDp * 0.039f).coerceIn(14.4f, 17.6f).dp
    val taglineMarginTop = (heightDp * 0.024f).coerceIn(14f, 22f).dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 1f - ExitEase.transform(leave.floatValue) }
            .semantics { contentDescription = brandName },
    ) {
        // Background: gradient, drifting pattern, vignette
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBackground(palette, time(), reduceMotion)
        }

        // Content, loader and footer leave together (.leave in the HTML)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val e = ExitEase.transform(leave.floatValue)
                    alpha = 1f - e
                    scaleX = 1f + 0.06f * e
                    scaleY = 1f + 0.06f * e
                    translationY = -8.dp.toPx() * e
                },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                LogoWithRipples(
                    width = logoWidth,
                    time = time,
                    reduceMotion = reduceMotion,
                )
                Spacer(modifier = Modifier.height(logoMarginBottom))

                var index = 0
                for (word in words) {
                    TitleWord(word = word, firstIndex = index, size = titleSize, time = time)
                    index += word.length
                }

                Spacer(modifier = Modifier.height(taglineMarginTop))
                Text(
                    text = tagline,
                    color = Cream,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = with(LocalDensity.current) { taglineSize.toSp() },
                    letterSpacing = 0.01.em,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer {
                        val p = EaseOut.transform(progress(time(), TAGLINE_DELAY, TAGLINE_MS))
                        alpha = p
                        translationY = 10.dp.toPx() * (1f - p)
                    },
                )
            }

            if (!reduceMotion) {
                Canvas(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 96.dp)
                        .size(36.dp),
                ) {
                    drawLoader(time())
                }
            }

            Text(
                text = copyright,
                color = Color.White,
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                fontSize = with(LocalDensity.current) { 13.6.dp.toSp() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 36.dp)
                    .graphicsLayer {
                        alpha = 0.55f * EaseOut.transform(progress(time(), FOOTER_DELAY, FOOTER_MS))
                    },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Background
// ---------------------------------------------------------------------------

/**
 * CSS radial-gradient(rx ry at cx cy): an ellipse. Compose only has circular
 * gradients, so a circle of radius 100 is stretched to the ellipse.
 */
private fun DrawScope.drawEllipticalGradient(
    centerXFraction: Float,
    centerYFraction: Float,
    radiusX: Float,
    radiusY: Float,
    stops: Array<Pair<Float, Color>>,
) {
    val unit = 100f
    val cx = size.width * centerXFraction
    val cy = size.height * centerYFraction
    val sx = radiusX / unit
    val sy = radiusY / unit
    withTransform({
        translate(cx, cy)
        scale(sx, sy, Offset.Zero)
    }) {
        drawRect(
            brush = Brush.radialGradient(
                *stops,
                center = Offset.Zero,
                radius = unit,
            ),
            topLeft = Offset(-cx / sx, -cy / sy),
            size = Size(size.width / sx, size.height / sy),
        )
    }
}

private fun DrawScope.drawBackground(palette: SplashPalette, t: Float, reduceMotion: Boolean) {
    // html background and .splash radial-gradient(120% 90% at 50% 45%, green 0%, #07603a 40%, deep 100%)
    drawRect(color = palette.deep)
    drawEllipticalGradient(
        centerXFraction = 0.5f,
        centerYFraction = 0.45f,
        radiusX = size.width * 1.2f,
        radiusY = size.height * 0.9f,
        stops = arrayOf(0f to palette.center, 0.4f to palette.mid, 1f to palette.deep),
    )

    // Pattern: white at 7 %, fades in over 1.6 s, then drifts 200 px in 45 s
    val fade = EaseOut.transform(progress(t, 0f, PATTERN_FADE_MS))
    if (fade > 0f) {
        val tile = 200.dp.toPx()
        val drift = if (reduceMotion) 0f else ((t % DRIFT_MS) / DRIFT_MS) * tile
        drawAnkaraPattern(
            tilePx = tile,
            phaseX = drift,
            phaseY = drift,
            color = Color.White.copy(alpha = 0.07f * fade),
        )
    }

    // .vignette radial-gradient(60% 40% at 50% 46%, rgba(deep,.55) 0%, rgba(deep,0) 100%)
    drawEllipticalGradient(
        centerXFraction = 0.5f,
        centerYFraction = 0.46f,
        radiusX = size.width * 0.6f,
        radiusY = size.height * 0.4f,
        stops = arrayOf(0f to palette.vignette, 1f to palette.vignette.copy(alpha = 0f)),
    )
}

// ---------------------------------------------------------------------------
// Logo: ripples, bubble pop, book pages, float
// ---------------------------------------------------------------------------

@Composable
private fun LogoWithRipples(width: Dp, time: () -> Float, reduceMotion: Boolean) {
    Canvas(modifier = Modifier.size(width = width, height = width * (LOGO_HEIGHT / LOGO_WIDTH))) {
        val t = time()
        val s = size.width / LOGO_WIDTH

        // Two ripples: circle as wide as the logo, centred at 50% / 45%
        if (!reduceMotion) {
            for (delay in floatArrayOf(RIPPLE_1_DELAY, RIPPLE_2_DELAY)) {
                if (t < delay) continue
                val p = EaseOut.transform(progress(t, delay, RIPPLE_MS))
                if (p >= 1f) continue
                val scale = 0.5f + 1.4f * p           // scale(.5) -> scale(1.9)
                val alpha = 0.35f * 0.8f * (1f - p)   // border .35 white, opacity .8 -> 0
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = size.width / 2f * scale,
                    center = Offset(size.width / 2f, size.height * 0.45f),
                    style = Stroke(width = 2.dp.toPx() * scale),
                )
            }
        }

        // Float: translateY 0 -> -7px -> 0, 3.6 s ease-in-out, starts after 1.6 s
        val floatY = if (reduceMotion || t < FLOAT_DELAY) {
            0f
        } else {
            val phase = ((t - FLOAT_DELAY) % FLOAT_MS) / FLOAT_MS
            val triangle = if (phase < 0.5f) phase * 2f else (1f - phase) * 2f
            -7.dp.toPx() * EaseInOut.transform(triangle)
        }

        // Bubble pop: scale 0 -> 1 and rotate -12deg -> 0, .9 s, origin 20% 90%
        val pop = progress(t, BUBBLE_DELAY, BUBBLE_MS)
        val popEased = PopEase.transform(pop)
        val bubbleAlpha = PopEase.transform((pop / 0.6f).unit()).unit()
        if (bubbleAlpha <= 0f) return@Canvas
        val pivot = Offset(0.2f * LOGO_WIDTH, 0.9f * LOGO_HEIGHT)

        withTransform({
            translate(0f, floatY)
            scale(s, s, Offset.Zero)
            scale(popEased, popEased, pivot)
            rotate(-12f * (1f - popEased), pivot)
        }) {
            drawBubbleShadow(bubbleAlpha, s)
            drawLogoBubble(alpha = bubbleAlpha)

            // Pages open (scaleX .02 -> 1), then the cover fades in
            val pl = progress(t, PAGE_L_DELAY, PAGE_MS)
            val pr = progress(t, PAGE_R_DELAY, PAGE_MS)
            drawLogoPages(
                leftScaleX = 0.02f + 0.98f * PageEase.transform(pl),
                leftAlpha = PageEase.transform((pl / 0.2f).unit()).unit(),
                rightScaleX = 0.02f + 0.98f * PageEase.transform(pr),
                rightAlpha = PageEase.transform((pr / 0.2f).unit()).unit(),
            )
            drawLogoCover(EaseOut.transform(progress(t, COVER_DELAY, COVER_MS)))
        }
    }
}

/**
 * Soft shadow under the bubble (CSS drop-shadow(0 10px 18px rgba(0,0,0,.25))).
 * Compose cannot blur a path on every Android version, so a few growing,
 * very transparent copies are stacked instead.
 */
private fun DrawScope.drawBubbleShadow(alpha: Float, logoScale: Float) {
    val offsetY = 10f / logoScale
    val spread = 18f / logoScale
    val layers = 6
    for (i in 0 until layers) {
        val grow = spread * (i / (layers - 1f))
        drawOval(
            color = Color.Black.copy(alpha = 0.045f * alpha),
            topLeft = Offset(6f - grow, 4f - grow + offsetY),
            size = Size(188f + 2f * grow, 168f + 2f * grow),
        )
    }
}

// ---------------------------------------------------------------------------
// Title: letter by letter, two lines
// ---------------------------------------------------------------------------

@Composable
private fun TitleWord(word: String, firstIndex: Int, size: Dp, time: () -> Float) {
    val fontSize = with(LocalDensity.current) { size.toSp() }
    Row {
        word.forEachIndexed { i, letter ->
            val delay = TITLE_DELAY + (firstIndex + i) * TITLE_STEP
            Text(
                text = letter.toString(),
                color = Color.White,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = fontSize,
                lineHeight = fontSize * 0.98f,
                letterSpacing = (-0.02).em,
                style = TextStyle(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None,
                    ),
                ),
                modifier = Modifier.graphicsLayer {
                    val p = RiseEase.transform(progress(time(), delay, TITLE_MS))
                    alpha = p.unit()
                    translationY = this.size.height * 0.7f * (1f - p)   // translateY(70%)
                    rotationZ = 8f * (1f - p)                          // rotate(8deg)
                },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Loader: 8 dots on a circle
// ---------------------------------------------------------------------------

private fun DrawScope.drawLoader(t: Float) {
    val fade = EaseOut.transform(progress(t, LOADER_DELAY, LOADER_MS))
    if (fade <= 0f) return
    val center = Offset(size.width / 2f, size.height / 2f)
    val ringRadius = 14.dp.toPx()
    val dotRadius = 3.dp.toPx()
    for (k in 0 until 8) {
        // dot .96s linear infinite, animation-delay: k * .12s - .96s
        val elapsed = t + DOT_CYCLE_MS - k * 120f
        val phase = (elapsed % DOT_CYCLE_MS) / DOT_CYCLE_MS
        val dotAlpha = 1f - 0.8f * phase                 // opacity 1 -> .2
        val angle = Math.toRadians(k * 45.0)
        drawCircle(
            color = Color.White.copy(alpha = dotAlpha * fade),
            radius = dotRadius,
            center = Offset(
                x = center.x + ringRadius * sin(angle).toFloat(),
                y = center.y - ringRadius * cos(angle).toFloat(),
            ),
        )
    }
}
