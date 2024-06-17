package carbon.compose.textinput

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import carbon.compose.CarbonDesignSystem
import carbon.compose.foundation.spacing.SpacingScale

private val loremIpsum =
    """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut 
        labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco 
        laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in 
        voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat 
        cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum
    """.trimIndent().replace("\n","")

private class TextInputStatePreviewParameterProvider : PreviewParameterProvider<TextInputState> {
    override val values: Sequence<TextInputState>
        get() = TextInputState.entries.asSequence()
}

@Preview
@Composable
private fun EmptyTextInputPreview(
) {
    var text by remember {
        mutableStateOf("")
    }

    CarbonDesignSystem {
        TextInput(
            label = "Label",
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.padding(SpacingScale.spacing03),
            placeholderText = "Placeholder",
        )
    }
}

@Preview
@Composable
private fun TextInputPreview(
    @PreviewParameter(TextInputStatePreviewParameterProvider::class) state: TextInputState
) {
    var text by remember {
        mutableStateOf(loremIpsum)
    }

    CarbonDesignSystem {
        TextInput(
            label = "Label",
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.padding(SpacingScale.spacing03),
            placeholderText = "Placeholder",
            helperText = state.name,
            state = state,
        )
    }
}

@Preview
@Composable
private fun EmptyTextAreaPreview(
) {
    var text by remember {
        mutableStateOf("")
    }

    CarbonDesignSystem {
        TextArea(
            label = "Label",
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.padding(SpacingScale.spacing03),
            placeholderText = "Placeholder",
            maxLines = 7
        )
    }
}

@Preview
@Composable
private fun TextAreaPreview(
    @PreviewParameter(TextInputStatePreviewParameterProvider::class) state: TextInputState
) {
    var text by remember {
        mutableStateOf(loremIpsum)
    }

    CarbonDesignSystem {
        TextArea(
            label = "Label",
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.padding(SpacingScale.spacing03),
            placeholderText = "Placeholder",
            helperText = state.name,
            state = state,
            maxLines = 7
        )
    }
}
