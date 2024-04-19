package carbon.compose.catalog.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import carbon.compose.button.Button
import carbon.compose.catalog.home.Destination
import carbon.compose.catalog.R
import carbon.compose.foundation.color.LocalCarbonTheme
import carbon.compose.foundation.spacing.SpacingScale

@Composable
fun DropdownDemoMenu(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(LocalCarbonTheme.current.background)
            .padding(SpacingScale.spacing03)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(SpacingScale.spacing03)
    ) {
        Button(
            label = "Default Dropdown",
            iconPainter = painterResource(id = R.drawable.ic_arrow_right),
            onClick = { onNavigate(Destination.Dropdown_Default.route) },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            label = "Multiselect Dropdown",
            iconPainter = painterResource(id = R.drawable.ic_arrow_right),
            onClick = { onNavigate(Destination.Dropdown_MultiSelect.route) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
