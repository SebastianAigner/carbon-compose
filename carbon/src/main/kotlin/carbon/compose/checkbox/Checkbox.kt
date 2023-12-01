package carbon.compose.checkbox

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import carbon.compose.checkbox.CheckboxInteractiveState.Companion.isEnabled
import carbon.compose.foundation.color.LocalCarbonTheme
import carbon.compose.foundation.interaction.ToggleableFocusIndication
import carbon.compose.foundation.spacing.SpacingScale
import carbon.compose.foundation.text.CarbonTypography
import carbon.compose.foundation.text.Text

private val checkboxBorderWidth = 1.dp
private val checkboxCornerRadius = 2.dp

/**
 * # Carbon Checkbox
 * Checkboxes are used when there are multiple items to select in a list. Users can select zero,
 * one, or any number of items.
 *
 * ## Content
 * - The checkbox itself is a square box with a checkmark or an indeterminate mark.
 * - The label describes the information the user wants to select or unselect.
 * - The error or warning message are displayed below the checkbox and help the user understand
 * about a certain state regarding the checkbox context.
 *
 * ## Interactions
 * The component applies a tri-state toggleable interaction to the checkbox root composable if the
 * [onClick] callback is provided, meaning that the whole component is clickable in order to create
 * a more accessible click target. Otherwise, the checkbox won't be interactable.
 *
 * (From [Checkbox documentation](https://carbondesignsystem.com/components/checkbox/usage/))
 *
 * @param state The [ToggleableState] of the checkbox.
 * @param interactiveState The [CheckboxInteractiveState] of the checkbox.
 * @param label The text to be displayed next to the checkbox.
 * @param onClick Callback invoked when the checkbox is clicked.
 * @param modifier The modifier to be applied to the checkbox.
 * @param errorMessage The error message to be displayed below the checkbox, it will be displayed
 * only if the [interactiveState] is [CheckboxInteractiveState.Error].
 * @param warningMessage The warning message to be displayed below the checkbox, it will be
 * displayed only if the [interactiveState] is [CheckboxInteractiveState.Warning].
 * @param interactionSource The [MutableInteractionSource] that keeps track of the checkbox state.
 */
@Composable
public fun Checkbox(
    state: ToggleableState,
    interactiveState: CheckboxInteractiveState,
    label: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    errorMessage: String = "",
    warningMessage: String = "",
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val colors = CheckboxColors.colors()

    val checkboxModifier = when {
        interactiveState == CheckboxInteractiveState.ReadOnly -> Modifier.focusable(
            enabled = interactiveState.isEnabled,
            interactionSource = interactionSource,
        )
        onClick != null -> Modifier.triStateToggleable(
            state = state,
            interactionSource = interactionSource,
            enabled = interactiveState.isEnabled,
            onClick = onClick,
            indication = null
        )
        else -> Modifier
    }

    Column(modifier = modifier.then(checkboxModifier)) {
        Row {
            CheckboxComponent(
                colors = colors,
                interactiveState = interactiveState,
                state = state,
                modifier = Modifier.indication(
                    interactionSource = interactionSource,
                    indication = ToggleableFocusIndication(4.dp)
                )
            )
            Text(
                text = label,
                color = colors.labelColor(interactiveState = interactiveState),
                modifier = Modifier.padding(start = SpacingScale.spacing03),
                style = CarbonTypography.bodyCompact01
            )
        }
        if (interactiveState == CheckboxInteractiveState.Error) {
            ErrorContent(
                colors = colors,
                errorMessage = errorMessage,
                modifier = Modifier.padding(top = SpacingScale.spacing03)
            )
        }
        if (interactiveState == CheckboxInteractiveState.Warning) {
            WarningContent(
                colors = colors,
                warningMessage = warningMessage,
                modifier = Modifier.padding(top = SpacingScale.spacing03)
            )
        }
    }
}

@Composable
private fun CheckboxComponent(
    colors: CheckboxColors,
    interactiveState: CheckboxInteractiveState,
    state: ToggleableState,
    modifier: Modifier = Modifier
) {
    val checkmarkIcon = rememberVectorPainter(image = checkboxCheckmarkIcon)
    val indeterminateIcon = rememberVectorPainter(image = checkboxIndeterminateIcon)
    val icon = when (state) {
        ToggleableState.On -> checkmarkIcon
        ToggleableState.Indeterminate -> indeterminateIcon
        ToggleableState.Off -> null
    }

    Canvas(
        modifier = Modifier
            .padding(2.dp)
            .requiredSize(16.dp)
            .then(modifier)
    ) {
        val borderWidth = checkboxBorderWidth.toPx()

        val borderColor = colors.borderColor(
            interactiveState = interactiveState,
            state = state
        )
        val drawBorder = borderColor != Color.Transparent

        // Background
        // Antialiasing issue: this inset is to reduce the background size when drawing
        // border to avoid antialiasing artifacts outside the border
        inset(if (drawBorder) borderWidth * .5f else 0f) {
            drawRoundRect(
                color = colors.backgroundColor(
                    interactiveState = interactiveState,
                    state = state
                ),
                cornerRadius = CornerRadius(checkboxCornerRadius.toPx()),
            )
        }
        // Border
        if (drawBorder) {
            inset(borderWidth * .5f) {
                drawRoundRect(
                    color = borderColor,
                    cornerRadius = CornerRadius(2f.dp.toPx()),
                    style = Stroke(borderWidth)
                )
            }
        }
        // Checkmark
        icon?.run {
            draw(
                size = intrinsicSize * .5f,
                colorFilter = ColorFilter.tint(
                    colors.checkmarkColor(
                        interactiveState = interactiveState,
                        state = state
                    )
                )
            )
        }
    }
}

@Composable
private fun ErrorContent(
    colors: CheckboxColors,
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Image(
            imageVector = checkboxErrorIcon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(colors.errorIconColor),
            modifier = Modifier
                .padding(2.dp)
                .requiredSize(16.dp)
        )
        Text(
            text = errorMessage,
            color = colors.errorMessageTextColor,
            modifier = Modifier
                .padding(start = SpacingScale.spacing03)
                .heightIn(min = 20.dp),
            style = CarbonTypography.label01
        )
    }
}

@Composable
private fun WarningContent(
    colors: CheckboxColors,
    warningMessage: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Box {
            Image(
                imageVector = checkboxWarningIcon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(colors.warningIconColor),
                modifier = Modifier
                    .padding(2.dp)
                    .requiredSize(16.dp)
            )
            Image(
                imageVector = checkboxWarningInnerIcon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(colors.warningIconInnerFillColor),
                modifier = Modifier
                    .padding(2.dp)
                    .requiredSize(16.dp)
            )
        }
        Text(
            text = warningMessage,
            color = colors.warningMessageTextColor,
            modifier = Modifier
                .padding(start = SpacingScale.spacing03)
                .heightIn(min = 20.dp),
            style = CarbonTypography.label01
        )
    }
}

/**
 * Represents the possible interactive states of a [Checkbox].
 */
public enum class CheckboxInteractiveState {

    /**
     * Default state, the checkbox is enabled and can be interacted with.
     */
    Default,

    /**
     * Disabled state, the checkbox is disabled and cannot be interacted with.
     */
    Disabled,

    /**
     * Read-only state, the checkbox cannot be interacted with but stays focusable.
     */
    ReadOnly,

    /**
     * Error state, the checkbox is enabled and be interacted with. An error message is displayed
     * below the checkbox.
     */
    Error,

    /**
     * Warning state, the checkbox is enabled and be interacted with. A warning message is displayed
     * below the checkbox.
     */
    Warning;

    internal companion object {

        private val enabledStates: Array<CheckboxInteractiveState> =
            arrayOf(Default, ReadOnly, Error, Warning)

        internal val CheckboxInteractiveState.isEnabled: Boolean
            get() = this in enabledStates
    }
}

/**
 * The set of colors used to style a [Checkbox].
 */
@Immutable
@Suppress("LongParameterList")
internal class CheckboxColors(
    val borderColor: Color,
    val borderDisabledColor: Color,
    val borderReadOnlyColor: Color,
    val borderErrorColor: Color,

    val backgroundCheckedColor: Color,
    val backgroundDisabledCheckedColor: Color,

    val checkmarkCheckedColor: Color,
    val checkmarkReadOnlyCheckedColor: Color,

    val labelColor: Color,
    val labelDisabledColor: Color,

    val errorMessageTextColor: Color,
    val errorIconColor: Color,

    val warningMessageTextColor: Color,
    val warningIconColor: Color,
    val warningIconInnerFillColor: Color,
) {

    fun borderColor(
        interactiveState: CheckboxInteractiveState,
        state: ToggleableState
    ): Color = when (interactiveState) {
        CheckboxInteractiveState.Default,
        CheckboxInteractiveState.Warning ->
            if (state == ToggleableState.Off) borderColor else Color.Transparent

        CheckboxInteractiveState.Disabled ->
            if (state == ToggleableState.Off) borderDisabledColor else Color.Transparent

        CheckboxInteractiveState.ReadOnly -> borderReadOnlyColor
        CheckboxInteractiveState.Error -> borderErrorColor
    }

    fun backgroundColor(
        interactiveState: CheckboxInteractiveState,
        state: ToggleableState
    ): Color = when (interactiveState) {
        CheckboxInteractiveState.Default,
        CheckboxInteractiveState.Error,
        CheckboxInteractiveState.Warning ->
            if (state == ToggleableState.Off) Color.Transparent else backgroundCheckedColor

        CheckboxInteractiveState.Disabled ->
            if (state == ToggleableState.Off) Color.Transparent else backgroundDisabledCheckedColor

        CheckboxInteractiveState.ReadOnly -> Color.Transparent
    }

    fun checkmarkColor(
        interactiveState: CheckboxInteractiveState,
        state: ToggleableState
    ): Color = when {
        state == ToggleableState.Off -> Color.Transparent
        interactiveState == CheckboxInteractiveState.ReadOnly -> checkmarkReadOnlyCheckedColor
        else -> checkmarkCheckedColor
    }

    fun labelColor(interactiveState: CheckboxInteractiveState): Color =
        if (interactiveState == CheckboxInteractiveState.Disabled) {
            labelDisabledColor
        } else {
            labelColor
        }

    internal companion object {

        @Composable
        fun colors(): CheckboxColors = with(LocalCarbonTheme.current) {
            CheckboxColors(
                borderColor = iconPrimary,
                borderDisabledColor = iconDisabled,
                borderReadOnlyColor = iconDisabled,
                borderErrorColor = supportError,

                backgroundCheckedColor = iconPrimary,
                backgroundDisabledCheckedColor = iconDisabled,

                checkmarkCheckedColor = iconInverse,
                checkmarkReadOnlyCheckedColor = iconPrimary,

                labelColor = textPrimary,
                labelDisabledColor = textDisabled,

                errorMessageTextColor = textError,
                errorIconColor = textError,

                warningMessageTextColor = textPrimary,
                warningIconColor = supportWarning,
                warningIconInnerFillColor = Color.Black,
            )
        }
    }
}
