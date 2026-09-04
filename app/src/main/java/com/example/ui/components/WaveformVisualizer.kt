package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SamlytCyan
import com.example.ui.theme.SamlytGreen
import com.example.ui.theme.SamlytOrange
import com.example.ui.theme.SamlytYellow

@Composable
fun WaveformVisualizer(
  isLive: Boolean = true,
  barCount: Int = 11,
  maxHeight: Dp = 38.dp,
  rmsLevel: Float = 0f,
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "waveform")

  val h1 by transition.animateFloat(
    initialValue = 0.2f, targetValue = 1.0f,
    animationSpec = infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h1"
  )
  val h2 by transition.animateFloat(
    initialValue = 0.5f, targetValue = 0.9f,
    animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h2"
  )
  val h3 by transition.animateFloat(
    initialValue = 0.8f, targetValue = 0.3f,
    animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h3"
  )
  val h4 by transition.animateFloat(
    initialValue = 0.3f, targetValue = 0.85f,
    animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h4"
  )

  val factors = listOf(h1, h2, h3, h4, h1, h3, h2, h4, h3, h2, h1)
  val barColors = listOf(
    SamlytOrange,
    SamlytOrange,
    SamlytYellow,
    SamlytGreen,
    SamlytCyan,
    SamlytOrange,
    SamlytCyan,
    SamlytGreen,
    SamlytYellow,
    SamlytOrange,
    SamlytOrange
  )

  Row(
    modifier = modifier.height(maxHeight),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    for (i in 0 until barCount) {
      val baseFactor = factors[i % factors.size]
      val factor = when {
        !isLive -> 0.22f
        rmsLevel > 0.05f -> (0.2f + 0.8f * rmsLevel * (0.4f + 0.6f * baseFactor)).coerceIn(0.18f, 1.0f)
        else -> baseFactor
      }
      val barHeight = maxHeight * factor
      val color = barColors[i % barColors.size]

      Box(
        modifier = Modifier
          .width(4.dp)
          .height(barHeight)
          .clip(RoundedCornerShape(999.dp))
          .background(color)
      )
    }
  }
}
