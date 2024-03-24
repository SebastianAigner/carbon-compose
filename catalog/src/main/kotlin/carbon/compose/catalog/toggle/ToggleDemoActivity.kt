package carbon.compose.catalog.toggle

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import carbon.compose.catalog.R
import carbon.compose.catalog.theme.CarbonCatalogTheme
import carbon.compose.foundation.color.LocalCarbonTheme
import carbon.compose.foundation.spacing.SpacingScale
import carbon.compose.toggle.SmallToggle
import carbon.compose.toggle.Toggle
import carbon.compose.uishell.UiShellHeader

@Suppress("UndocumentedPublicClass")
class ToggleDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))

        setContent {
            CarbonCatalogTheme {
                Column(
                    modifier = Modifier.background(LocalCarbonTheme.current.background)
                ) {
                    UiShellHeader(
                        headerName = "Toggle",
                        menuIconRes = R.drawable.ic_arrow_left,
                        onMenuIconPressed = { onBackPressedDispatcher.onBackPressed() },
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = rememberScrollState())
                            .padding(WindowInsets.navigationBars.asPaddingValues())
                    ) {
                        var isToggled by rememberSaveable { mutableStateOf(false) }

                        DefaultToggles(
                            isToggled = isToggled,
                            onToggleChange = { isToggled = it }
                        )
                        SmallToggles(
                            isToggled = isToggled,
                            onToggleChange = { isToggled = it },
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun DefaultToggles(
        isToggled: Boolean,
        onToggleChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier) {
            Toggle(
                isToggled = isToggled,
                onToggleChange = onToggleChange,
                label = "Toggle",
                actionText = if (isToggled) "On" else "Off",
                modifier = Modifier.padding(SpacingScale.spacing05)
            )
            Toggle(
                isToggled = isToggled,
                isEnabled = false,
                onToggleChange = {},
                actionText = "Disabled",
                modifier = Modifier.padding(SpacingScale.spacing05)
            )
            Toggle(
                isToggled = isToggled,
                isReadOnly = true,
                onToggleChange = {},
                actionText = "Read only",
                modifier = Modifier.padding(SpacingScale.spacing05)
            )
        }
    }

    @Composable
    private fun SmallToggles(
        isToggled: Boolean,
        onToggleChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier) {
            SmallToggle(
                isToggled = isToggled,
                onToggleChange = onToggleChange,
                actionText = if (isToggled) "On" else "Off",
                modifier = Modifier.padding(SpacingScale.spacing05)
            )
            SmallToggle(
                isToggled = isToggled,
                isEnabled = false,
                onToggleChange = {},
                actionText = "Disabled",
                modifier = Modifier.padding(SpacingScale.spacing05)
            )
            SmallToggle(
                isToggled = isToggled,
                isReadOnly = true,
                onToggleChange = {},
                actionText = "Read only",
                modifier = Modifier.padding(SpacingScale.spacing05)
            )
        }
    }
}
