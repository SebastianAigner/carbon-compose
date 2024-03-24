package carbon.compose.dropdown

import androidx.annotation.IntRange
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.InspectableModifier
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import carbon.compose.dropdown.DropdownInteractiveState.Companion.helperText
import carbon.compose.dropdown.DropdownInteractiveState.Companion.isFocusable
import carbon.compose.dropdown.domain.getOptionsPopupHeightRatio
import carbon.compose.foundation.color.LocalCarbonTheme
import carbon.compose.foundation.input.onEnterKeyEvent
import carbon.compose.foundation.interaction.FocusIndication
import carbon.compose.foundation.motion.Motion
import carbon.compose.foundation.spacing.SpacingScale
import carbon.compose.foundation.text.CarbonTypography
import carbon.compose.foundation.text.Text
import carbon.compose.icons.ErrorIcon
import carbon.compose.icons.WarningIcon
import carbon.compose.semantics.readOnly

private val dropdownTransitionSpecFloat = tween<Float>(
    durationMillis = Motion.Duration.moderate01,
    easing = Motion.Standard.productiveEasing
)

private val dropdownTransitionSpecDp = tween<Dp>(
    durationMillis = Motion.Duration.moderate01,
    easing = Motion.Standard.productiveEasing
)

private const val CHEVRON_ROTATION_ANGLE = 180f

/**
 * Adds a callback to be invoked when the escape key (hardware) is pressed.
 */
private fun Modifier.onEscape(block: () -> Unit) = onPreviewKeyEvent {
    if (it.key == Key.Escape) {
        block()
        true
    } else {
        false
    }
}

/**
 * Sets up a custom clickability for the dropdown field.
 *
 * `pointerInput` handles pointer events.
 * `onEnterKeyEvent` handles physical enter key events (keyboard).
 * `semantics` handles accessibility events.
 */
private fun Modifier.dropdownClickable(
    expandedStates: MutableTransitionState<Boolean>,
    onClick: () -> Unit
): Modifier = this
    .pointerInput(Unit) {
        awaitEachGesture {
            // Custom pointer input to handle input events on the field.
            awaitFirstDown(pass = PointerEventPass.Initial)
            val expandStateOnDown = expandedStates.currentState
            waitForUpOrCancellation(pass = PointerEventPass.Initial)?.let {
                // Avoid expanding back if the dropdown was expanded on down.
                if (!expandStateOnDown) {
                    onClick()
                }
            }
        }
    }
    .onEnterKeyEvent {
        onClick()
    }
    .semantics(mergeDescendants = true) {
        onClick {
            onClick()
            true
        }
    }

/**
 * # Dropdown
 *
 * Dropdowns present a list of options from which a user can select one option.
 * A selected option can represent a value in a form, or can be used as an action to filter or sort
 * existing content.
 *
 * Only one option can be selected at a time.
 * - By default, the dropdown displays placeholder text in the field when closed.
 * - Clicking on a closed field opens a menu of options.
 * - Selecting an option from the menu closes it and the selected option text replaces the
 * placeholder text in the field and also remains as an option in place if the menu is open.
 *
 * (From [Dropdown documentation](https://carbondesignsystem.com/components/dropdown/usage/))
 *
 * @param K Type to identify the options.
 * @param expanded Whether the dropdown is expanded or not.
 * @param fieldPlaceholderText The text to be displayed in the field when no option is selected.
 * @param selectedOption The currently selected option. When not null, the option associated with
 * this key will be displayed in the field.
 * @param options The options to be displayed in the dropdown menu. A map signature ensures that the
 * keys are unique and can be used to identify the selected option. The strings associated with each
 * key are the texts to be displayed in the dropdown menu.
 * @param onOptionSelected Callback invoked when an option is selected. The selected option is
 * passed as a parameter, and the callback should be used to update a remembered state with the new
 * value.
 * @param onExpandedChange Callback invoked when the expanded state of the dropdown changes. It
 * should be used to update a remembered state with the new value.
 * @param onDismissRequest Callback invoked when the dropdown menu should be dismissed.
 * @param modifier The modifier to be applied to the dropdown.
 * @param state The [DropdownInteractiveState] of the dropdown.
 * @param dropdownSize The size of the dropdown, in terms of height. Defaults to
 * [DropdownSize.Large].
 * @param minVisibleItems The minimum number of items to be visible in the dropdown menu before the
 * user needs to scroll. This value is used to calculate the height of the menu. Defaults to 4.
 * @throws IllegalArgumentException If the options map is empty.
 */
@Composable
public fun <K : Any> Dropdown(
    expanded: Boolean,
    fieldPlaceholderText: String,
    selectedOption: K?,
    options: Map<K, DropdownOption>,
    onOptionSelected: (K) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    state: DropdownInteractiveState = DropdownInteractiveState.Enabled,
    dropdownSize: DropdownSize = DropdownSize.Large,
    @IntRange(from = 1) minVisibleItems: Int = 4,
) {
    require(options.isNotEmpty()) {
        "Dropdown must have at least one option."
    }

    val interactionSource = remember { MutableInteractionSource() }

    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded

    val colors = DropdownColors(LocalCarbonTheme.current)
    val componentHeight = dropdownSize.height

    val fieldText = remember(selectedOption) {
        options[selectedOption]?.value ?: fieldPlaceholderText
    }

    val transition = updateTransition(expandedStates, "Dropdown")
    val maxHeight = getOptionsPopupHeightRatio(options.size, minVisibleItems)
        .times(componentHeight)

    val height by transition.animateDp(
        transitionSpec = { dropdownTransitionSpecDp },
        label = "Popup content height"
    ) {
        if (it) maxHeight else 0.dp
    }

    val elevation by transition.animateDp(
        transitionSpec = { dropdownTransitionSpecDp },
        label = "Popup content shadow"
    ) {
        if (it) 3.dp else 0.dp
    }

    Column(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier
                .height(componentHeight)
                .indication(
                    interactionSource = interactionSource,
                    indication = FocusIndication()
                )
                .then(
                    InspectableModifier {
                        debugInspectorInfo {
                            properties["isExpanded"] = expanded.toString()
                            properties["interactiveState"] = state::class.java.simpleName
                        }
                    }
                )
        ) {
            DropdownField(
                state = state,
                interactionSource = interactionSource,
                transition = transition,
                expandedStates = expandedStates,
                fieldPlaceholderText = fieldText,
                colors = colors,
                onExpandedChange = onExpandedChange
            )

            if (expandedStates.currentState || expandedStates.targetState && options.isNotEmpty()) {
                Popup(
                    popupPositionProvider = DropdownMenuPositionProvider,
                    onDismissRequest = onDismissRequest,
                    properties = PopupProperties(focusable = true)
                ) {
                    DropdownPopupContent(
                        selectedOption = selectedOption,
                        options = options,
                        colors = colors,
                        componentHeight = componentHeight,
                        onOptionSelected = { option ->
                            onOptionSelected(option)
                            onDismissRequest()
                        },
                        modifier = Modifier
                            .width(maxWidth)
                            .height(height)
                            // This should be a box shadow (-> 0 2px 6px 0 rgba(0,0,0,.2)). But
                            // compose doesn't provide the same API as CSS for shadows. A 3dp
                            // elevation is the best approximation that could be found for now.
                            .shadow(elevation = elevation)
                            .onEscape(onDismissRequest)
                    )
                }
            }
        }

        state.helperText?.let {
            Text(
                text = it,
                style = CarbonTypography.helperText01,
                color = colors.helperTextColor(state),
                modifier = Modifier
                    .padding(top = SpacingScale.spacing02)
                    .testTag(DropdownTestTags.FIELD_HELPER_TEXT)
            )
        }
    }
}

@Composable
private fun DropdownField(
    state: DropdownInteractiveState,
    transition: Transition<Boolean>,
    expandedStates: MutableTransitionState<Boolean>,
    fieldPlaceholderText: String,
    colors: DropdownColors,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val chevronRotation by transition.animateFloat(
        transitionSpec = { dropdownTransitionSpecFloat },
        label = "Chevron rotation"
    ) {
        if (it) CHEVRON_ROTATION_ANGLE else 0f
    }

    Box(
        modifier = modifier
            .focusable(
                enabled = state.isFocusable,
                interactionSource = interactionSource
            )
            .fillMaxHeight()
            .background(colors.fieldBackgroundColor(state))
            .then(
                if (state is DropdownInteractiveState.Error) {
                    Modifier.border(2.dp, colors.fieldBorderErrorColor)
                } else {
                    Modifier
                }
            )
            .then(
                when (state) {
                    is DropdownInteractiveState.Disabled -> Modifier.semantics { disabled() }
                    is DropdownInteractiveState.ReadOnly -> Modifier.readOnly(
                        role = Role.DropdownList,
                        interactionSource = interactionSource,
                        mergeDescendants = true
                    )
                    else -> Modifier.dropdownClickable(
                        expandedStates = expandedStates,
                        onClick = { onExpandedChange(!expandedStates.currentState) }
                    )
                }
            )
            .semantics(mergeDescendants = true) {
                role = Role.DropdownList
                state.helperText?.let { stateDescription = it }
            }
            .testTag(DropdownTestTags.FIELD)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = fieldPlaceholderText,
                style = CarbonTypography.bodyCompact01,
                color = colors.fieldTextColor(state),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = SpacingScale.spacing03)
                    .testTag(DropdownTestTags.FIELD_PLACEHOLDER)
            )

            when (state) {
                is DropdownInteractiveState.Warning -> WarningIcon(
                    modifier = Modifier.testTag(DropdownTestTags.FIELD_WARNING_ICON)
                )
                is DropdownInteractiveState.Error -> ErrorIcon(
                    modifier = Modifier.testTag(DropdownTestTags.FIELD_ERROR_ICON)
                )
                else -> {}
            }

            Image(
                imageVector = chevronDownIcon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(colors.chevronIconColor(state)),
                modifier = Modifier
                    .padding(start = SpacingScale.spacing03)
                    .graphicsLayer {
                        rotationZ = chevronRotation
                    }
                    .testTag(DropdownTestTags.FIELD_CHEVRON)
            )
        }

        if (state !is DropdownInteractiveState.Error) {
            Spacer(
                modifier = Modifier
                    .background(color = colors.fieldBorderColor(state))
                    .height(1.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .testTag(DropdownTestTags.FIELD_DIVIDER)
            )
        }
    }
}

private object DropdownMenuPositionProvider : PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset = IntOffset(
        x = anchorBounds.left,
        y = anchorBounds.top + anchorBounds.height
    )
}
