package carbon.compose.catalog.home

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import carbon.compose.Carbon
import carbon.compose.catalog.R
import carbon.compose.foundation.spacing.SpacingScale

private const val wipItemVerticalRotation = -90f

@Composable
fun WIPIndicatorItem(
    isInVerticalLayout: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .then(
                if (isInVerticalLayout) Modifier
                    .padding(vertical = SpacingScale.spacing05)
                    .fillMaxWidth()
                else Modifier
                    .padding(horizontal = SpacingScale.spacing05)
                    .fillMaxHeight()
            )
            .wipBackground()
            .padding(SpacingScale.spacing05),
        contentAlignment = Alignment.Center
    ) {
        @Composable
        fun content() {
            val arrow = painterResource(
                id = if (isInVerticalLayout) R.drawable.ic_arrow_down else R.drawable.ic_arrow_right
            )
            Image(
                painter = arrow,
                colorFilter = ColorFilter.tint(Carbon.theme.textPrimary),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            BasicText(
                text = "TO BE IMPLEMENTED",
                style = Carbon.typography.code02.copy(color = Carbon.theme.textPrimary),
                modifier = Modifier.then(
                    if (isInVerticalLayout) Modifier else Modifier.rotateVertical()
                )
            )
            Image(
                painter = arrow,
                colorFilter = ColorFilter.tint(Carbon.theme.textPrimary),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }

        if (isInVerticalLayout) {
            Row(
                modifier = Modifier
                    .composed { background(Carbon.theme.background.copy(alpha = .9f)) }
                    .padding(SpacingScale.spacing03),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    SpacingScale.spacing03,
                    Alignment.CenterHorizontally
                )
            ) {
                content()
            }
        } else {
            Column(
                modifier = Modifier
                    .composed { background(Carbon.theme.background.copy(alpha = .9f)) }
                    .padding(SpacingScale.spacing03),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    SpacingScale.spacing03,
                    Alignment.CenterVertically
                )
            ) {
                content()
            }
        }
    }
}

private fun Modifier.rotateVertical(): Modifier = this
    .layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }
    .rotate(wipItemVerticalRotation)

private fun Modifier.wipBackground(): Modifier = this.composed {
    val context = LocalContext.current
    val backgroundInverse = Carbon.theme.backgroundInverse
    val brush = remember {
        val pattern = AppCompatResources.getDrawable(context, R.drawable.pattern_diagonal_stripes)!!
            .apply { setTint(backgroundInverse.toArgb()) }
            .toBitmap()
            .asImageBitmap()
        ShaderBrush(ImageShader(pattern, TileMode.Repeated, TileMode.Repeated))
    }
    background(Carbon.theme.supportWarning).background(brush)
}
