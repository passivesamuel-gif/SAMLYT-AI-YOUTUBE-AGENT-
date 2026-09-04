package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SamlytGreen
import com.example.ui.theme.SamlytOrange

@Composable
fun SamlytLogo(
  modifier: Modifier = Modifier,
  size: Dp = 72.dp,
  isLiveAnimated: Boolean = false
) {
  val infiniteTransition = rememberInfiniteTransition(label = "logo_anim")
  val rotation by if (isLiveAnimated) {
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = 360f,
      animationSpec = infiniteRepeatable(
        animation = tween(12000, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Restart
      ),
      label = "rotation"
    )
  } else {
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = 0f,
      animationSpec = infiniteRepeatable(tween(1000)),
      label = "static"
    )
  }

  Box(
    modifier = modifier
      .size(size)
      .shadow(16.dp, RoundedCornerShape(size * 0.28f), spotColor = SamlytOrange)
      .clip(RoundedCornerShape(size * 0.28f))
      .background(
        Brush.linearGradient(
          colors = listOf(Color(0xFF262626), Color(0xFF141414), Color(0xFF0D0D0D)),
          start = Offset(0f, 0f),
          end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
      )
      .border(1.dp, Brush.linearGradient(listOf(Color(0xFF3F3F46), Color(0xFF18181B))), RoundedCornerShape(size * 0.28f)),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size * 0.82f)) {
      val w = this.size.width
      val h = this.size.height
      val cx = w / 2f
      val cy = h / 2f
      val radius = w * 0.44f

      // Glowing outer orange/yellow arc
      drawArc(
        brush = Brush.sweepGradient(
          colors = listOf(
            SamlytOrange,
            Color(0xFFFBBF24),
            SamlytOrange,
            Color(0x00000000)
          )
        ),
        startAngle = 135f + rotation,
        sweepAngle = 270f,
        useCenter = false,
        topLeft = Offset(cx - radius, cy - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = w * 0.055f, cap = StrokeCap.Round)
      )

      rotate(rotation, pivot = Offset(cx, cy)) {
        // 4 Aperture Shutter Blades (White/Silver Triangles)
        val bladeDist = w * 0.16f
        val bladeSize = w * 0.18f

        // Top Blade
        val topPath = Path().apply {
          moveTo(cx - bladeSize / 2f, cy - bladeDist - bladeSize)
          lineTo(cx + bladeSize / 2f, cy - bladeDist - bladeSize)
          lineTo(cx, cy - bladeDist)
          close()
        }
        drawPath(topPath, color = Color(0xFFE2E8F0))

        // Bottom Blade
        val botPath = Path().apply {
          moveTo(cx - bladeSize / 2f, cy + bladeDist + bladeSize)
          lineTo(cx + bladeSize / 2f, cy + bladeDist + bladeSize)
          lineTo(cx, cy + bladeDist)
          close()
        }
        drawPath(botPath, color = Color(0xFFCBD5E1))

        // Left Blade
        val leftPath = Path().apply {
          moveTo(cx - bladeDist - bladeSize, cy - bladeSize / 2f)
          lineTo(cx - bladeDist - bladeSize, cy + bladeSize / 2f)
          lineTo(cx - bladeDist, cy)
          close()
        }
        drawPath(leftPath, color = Color(0xFFF1F5F9))

        // Right Blade
        val rightPath = Path().apply {
          moveTo(cx + bladeDist + bladeSize, cy - bladeSize / 2f)
          lineTo(cx + bladeDist + bladeSize, cy + bladeSize / 2f)
          lineTo(cx + bladeDist, cy)
          close()
        }
        drawPath(rightPath, color = Color(0xFF94A3B8))
      }

      // Center glowing emerald play circle
      val centerRadius = w * 0.13f
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFF4ADE80), SamlytGreen, Color(0xFF15803D)),
          center = Offset(cx, cy),
          radius = centerRadius
        ),
        radius = centerRadius,
        center = Offset(cx, cy)
      )

      // Center white play triangle
      val playSize = centerRadius * 0.9f
      val playPath = Path().apply {
        moveTo(cx - playSize * 0.35f, cy - playSize * 0.5f)
        lineTo(cx + playSize * 0.55f, cy)
        lineTo(cx - playSize * 0.35f, cy + playSize * 0.5f)
        close()
      }
      drawPath(playPath, color = Color.White)
    }
  }
}
