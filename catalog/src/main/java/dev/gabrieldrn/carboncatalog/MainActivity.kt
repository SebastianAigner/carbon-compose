package dev.gabrieldrn.carboncatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.gabrieldrn.carbon.CarbonDesignSystem
import dev.gabrieldrn.carbon.button.PrimaryButton
import dev.gabrieldrn.carbon.color.LocalCarbonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarbonDesignSystem {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LocalCarbonTheme.current.background),
                    contentAlignment = Alignment.Center
                ) {
                    PrimaryButton(
                        label = "Primary button",
                        onClick = {}
                    )
                }
            }
        }
    }
}
