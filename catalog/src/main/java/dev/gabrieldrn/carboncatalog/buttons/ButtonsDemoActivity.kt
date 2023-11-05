package dev.gabrieldrn.carboncatalog.buttons

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import dev.gabrieldrn.carbon.CarbonDesignSystem
import dev.gabrieldrn.carbon.button.Button
import dev.gabrieldrn.carbon.button.ButtonSize
import dev.gabrieldrn.carbon.button.CarbonButton
import dev.gabrieldrn.carbon.button.IconButton
import dev.gabrieldrn.carbon.foundation.color.LocalCarbonTheme
import dev.gabrieldrn.carbon.foundation.spacing.SpacingScale
import dev.gabrieldrn.carbon.uishell.UiShellHeader
import dev.gabrieldrn.carboncatalog.R

class ButtonsDemoActivity : AppCompatActivity() {

    private val buttons = mapOf(
        "Primary" to CarbonButton.Primary,
        "Secondary" to CarbonButton.Secondary,
        "Tertiary" to CarbonButton.Tertiary,
        "Ghost" to CarbonButton.Ghost,
        "PrimaryDanger" to CarbonButton.PrimaryDanger,
        "TertiaryDanger" to CarbonButton.TertiaryDanger,
        "GhostDanger" to CarbonButton.GhostDanger,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            CarbonDesignSystem {
                Column(
                    modifier = Modifier
                        .background(LocalCarbonTheme.current.background)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UiShellHeader(
                        headerName = "Buttons",
                        menuIconRes = R.drawable.ic_arrow_left,
                        onMenuIconPressed = onBackPressedDispatcher::onBackPressed,
                    )

                    var isEnabled by remember {
                        mutableStateOf(true)
                    }

                    Button(
                        label = "Toggle isEnabled",
                        onClick = { isEnabled = !isEnabled },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(SpacingScale.spacing05))

                    // FIXME Expected performance issue when toggling isEnabled. Change this to
                    //  present only one button, parameterized with toggle and dropdown components
                    //  when available.
                    buttons.forEach { (label, buttonType) ->
                        Row(
                            modifier = Modifier
                                .padding(horizontal = SpacingScale.spacing05)
                                .padding(bottom = SpacingScale.spacing05),
                        ) {
                            Button(
                                label = label,
                                onClick = {},
                                buttonType = buttonType,
                                buttonSize = ButtonSize.LargeProductive,
                                isEnabled = isEnabled,
                                iconPainter = painterResource(id = R.drawable.ic_add),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(SpacingScale.spacing05))
                            IconButton(
                                onClick = {},
                                buttonType = buttonType,
                                isEnabled = isEnabled,
                                iconPainter = painterResource(id = R.drawable.ic_add),
                            )
                        }
                    }
                }
            }
        }
    }
}
