package com.bitbytestudio.fluidbackground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.bitbytestudio.fluidbackground.ui.FluidScreen
import com.bitbytestudio.fluidbackground.ui.theme.FluidBackgroundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FluidBackgroundTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    FluidScreen(
                        color1 = Color(0xFF0A0E2A),
                        color2 = Color(0xFF00F5D4),
                        color3 = Color(0xFF7B2CBF),
                        color4 = Color(0xFF03045E),
                        speed = 0.45f,
                        intensity = 0.7f,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Fluid Background",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun GreetingPreview() {
    FluidBackgroundTheme {
        FluidScreen(
            color1 = Color(0xFFB90845),
            color2 = Color(0xFF00F5D4),
            color3 = Color(0xFF7B2CBF),
            color4 = Color(0xFF8C6B05),
            speed = 0.45f,
            intensity = 0.7f,
        )
    }
}
