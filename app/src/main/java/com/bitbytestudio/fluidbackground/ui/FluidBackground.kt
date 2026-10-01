package com.bitbytestudio.fluidbackground.ui

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bitbytestudio.fluidbackground.Shader.FLUID_SHADER

object ShaderSupport {
    val isSupported: Boolean by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                RuntimeShaderHelper.createShader(FLUID_SHADER)
                true
            } catch (_: Throwable) {
                false
            }
        } else {
            false
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal object RuntimeShaderHelper {
    fun createShader(agsl: String): RuntimeShader {
        return RuntimeShader(agsl)
    }
}

@Composable
fun FluidScreen(
    modifier: Modifier = Modifier,
    color1: Color = Color(0xFF0A0E2A),
    color2: Color = Color(0xFF00F5D4),
    color3: Color = Color(0xFF7B2CBF),
    color4: Color = Color(0xFF03045E),
    speed: Float = 0.45f,
    intensity: Float = 1.0f,
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        FluidBackground(
            modifier = Modifier.fillMaxSize(),
            color1 = color1,
            color2 = color2,
            color3 = color3,
            color4 = color4,
            speed = speed,
            intensity = intensity,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        ) {
            content()
        }
    }
}

@Composable
fun FluidBackground(
    modifier: Modifier = Modifier,
    color1: Color = Color(0xFF0A0E2A),
    color2: Color = Color(0xFF00F5D4),
    color3: Color = Color(0xFF7B2CBF),
    color4: Color = Color(0xFF03045E),
    speed: Float = 0.45f,
    intensity: Float = 1.0f,
) {
    if (ShaderSupport.isSupported) {
        FluidShaderBackground(
            modifier = modifier,
            color1 = color1,
            color2 = color2,
            color3 = color3,
            color4 = color4,
            speed = speed,
            intensity = intensity,
        )
    } else {
        FluidFallbackBackground(
            modifier = modifier,
            color1 = color1,
            color2 = color2,
            color3 = color3,
            color4 = color4,
            speed = speed,
            intensity = intensity,
        )
    }
}
