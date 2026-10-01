package com.bitbytestudio.fluidbackground.ui

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import com.bitbytestudio.fluidbackground.Shader.FLUID_SHADER

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FluidShaderBackground(
    modifier: Modifier = Modifier,
    color1: Color = Color(0xFF0A0E2A),
    color2: Color = Color(0xFF00F5D4),
    color3: Color = Color(0xFF7B2CBF),
    color4: Color = Color(0xFF03045E),
    speed: Float = 0.45f,
    intensity: Float = 1.0f,
) {
    val shader = remember {
        try {
            RuntimeShaderHelper.createShader(FLUID_SHADER)
        } catch (_: Throwable) {
            null
        }
    }

    if (shader == null) {
        FluidFallbackBackground(
            modifier = modifier,
            color1 = color1,
            color2 = color2,
            color3 = color3,
            color4 = color4,
            speed = speed,
            intensity = intensity,
        )
        return
    }

    val shaderBrush = remember(shader) { ShaderBrush(shader) }

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
                val currentTime = timeState

                try {
                    shader.setFloatUniform("resolution", size.width, size.height)
                    shader.setFloatUniform("time", currentTime)
                    shader.setFloatUniform("intensity", intensity)

                    shader.setColorUniform("color1", color1.toArgb())
                    shader.setColorUniform("color2", color2.toArgb())
                    shader.setColorUniform("color3", color3.toArgb())
                    shader.setColorUniform("color4", color4.toArgb())

                    drawRect(brush = shaderBrush)
                } catch (_: Throwable) {
                    drawRect(color = color4)
                }
            },
    )
}
