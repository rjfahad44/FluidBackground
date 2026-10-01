package com.bitbytestudio.fluidbackground.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FluidFallbackBackground(
    modifier: Modifier = Modifier,
    color1: Color = Color(0xFF0D1B2A),
    color2: Color = Color(0xFF00B4D8),
    color3: Color = Color(0xFF7209B7),
    color4: Color = Color(0xFF03071E),
    speed: Float = 0.35f,
    intensity: Float = 1.0f,
) {
    var timeState by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(speed) {
        var previousNanos = 0L
        while (true) {
            withFrameNanos { frameTimeNanos ->
                if (previousNanos != 0L) {
                    val deltaSeconds = (frameTimeNanos - previousNanos) / 1_000_000_000f
                    timeState += deltaSeconds * speed
                }
                previousNanos = frameTimeNanos
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val t = timeState
                val w = size.width
                val h = size.height

                drawRect(color = color4)

                val radius = maxOf(w, h) * 0.9f * intensity

                // Blob 1 - Main Swirl
                val center1 = Offset(
                    x = w * (0.5f + 0.35f * sin(t * 0.9f)),
                    y = h * (0.5f + 0.30f * cos(t * 0.7f)),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color1, Color.Transparent),
                        center = center1,
                        radius = radius,
                    ),
                    center = center1,
                    radius = radius,
                )

                // Blob 2 - Secondary Vortex
                val center2 = Offset(
                    x = w * (0.5f - 0.35f * cos(t * 0.8f)),
                    y = h * (0.5f + 0.35f * sin(t * 1.1f)),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color2, Color.Transparent),
                        center = center2,
                        radius = radius * 0.85f,
                    ),
                    center = center2,
                    radius = radius * 0.85f,
                )

                // Blob 3 - Fluid Highlight Swirl
                val center3 = Offset(
                    x = w * (0.5f + 0.25f * sin(t * 1.3f)),
                    y = h * (0.5f - 0.30f * cos(t * 0.9f)),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color3, Color.Transparent),
                        center = center3,
                        radius = radius * 0.75f,
                    ),
                    center = center3,
                    radius = radius * 0.75f,
                )
            },
    )
}
