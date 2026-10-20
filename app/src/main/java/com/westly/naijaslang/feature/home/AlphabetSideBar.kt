package com.westly.naijaslang.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.westly.naijaslang.core.data.DIGIT_GROUP
import com.westly.naijaslang.core.theme.NaijaTheme

/** "#" at the very top, then A to Z. Which of them can be used comes from the data. */
private val SideBarLetters: List<String> = listOf(DIGIT_GROUP) + ('A'..'Z').map { it.toString() }

/**
 * The A to Z bar on the right of the list.
 *
 * @param available letters that have entries; all others are faded and cannot be selected.
 * @param current the letter shown in the gold circle.
 * @param onLetterSelected called when a letter is tapped, or when a finger dragging
 *   along the bar moves onto a different (available) letter.
 * @param onTouchEnd called when the finger is lifted.
 */
@Composable
fun AlphabetSideBar(
    available: Set<String>,
    current: String?,
    onLetterSelected: (String) -> Unit,
    onTouchEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = NaijaTheme.colors
    val currentOnLetterSelected by rememberUpdatedState(onLetterSelected)
    val currentOnTouchEnd by rememberUpdatedState(onTouchEnd)

    // The letters follow the text size setting, but stop growing at 1.15x so
    // 27 letters always fit in the height of the screen.
    val fontScale = LocalDensity.current.fontScale
    val letterSize = (12f * fontScale.coerceAtMost(1.15f) / fontScale).sp

    Column(
        modifier = modifier
            .width(40.dp)
            .fillMaxHeight()
            .drawBehind {
                drawLine(
                    color = colors.divider,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .pointerInput(available) {
                awaitEachGesture {
                    var lastLetter: String? = null

                    fun select(y: Float) {
                        val position = (y / size.height * SideBarLetters.size).toInt()
                            .coerceIn(0, SideBarLetters.lastIndex)
                        val letter = SideBarLetters[position]
                        if (letter in available && letter != lastLetter) {
                            lastLetter = letter
                            currentOnLetterSelected(letter)
                        }
                    }

                    val down = awaitFirstDown()
                    select(down.position.y)
                    down.consume()
                    drag(down.id) { change ->
                        select(change.position.y)
                        change.consume()
                    }
                    currentOnTouchEnd()
                }
            },
    ) {
        for (letter in SideBarLetters) {
            val enabled = letter in available
            val selected = enabled && letter == current

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .sizeIn(maxHeight = 26.dp)
                        .fillMaxHeight()
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .clip(CircleShape)
                        .background(if (selected) colors.gold else Color.Transparent),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = letter,
                        maxLines = 1,
                        softWrap = false,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = letterSize,
                            lineHeight = letterSize,
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                        ),
                        color = when {
                            selected -> Color.White
                            else -> colors.textSecondary
                        },
                        modifier = Modifier.alpha(if (enabled) 1f else 0.3f),
                    )
                }
            }
        }
    }
}
