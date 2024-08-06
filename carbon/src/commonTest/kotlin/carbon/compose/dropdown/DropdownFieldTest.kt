package carbon.compose.dropdown

import androidx.annotation.CallSuper
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.isFocusable
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.width
import carbon.compose.CarbonDesignSystem
import carbon.compose.dropdown.base.DropdownColors
import carbon.compose.dropdown.base.DropdownField
import carbon.compose.dropdown.base.DropdownInteractiveState
import carbon.compose.dropdown.base.DropdownPlaceholderText
import carbon.compose.dropdown.base.DropdownSize
import carbon.compose.dropdown.base.DropdownStateIcon
import carbon.compose.dropdown.base.DropdownTestTags
import carbon.compose.foundation.color.WhiteTheme
import carbon.compose.foundation.spacing.SpacingScale
import carbon.compose.semantics.assertIsReadOnly
import kotlin.test.Test

open class DropdownFieldTest {

    protected var state by mutableStateOf<DropdownInteractiveState>(
        DropdownInteractiveState.Enabled
    )
    protected val placeholder = "Dropdown"

    protected val interactiveStates = listOf(
        DropdownInteractiveState.Enabled,
        DropdownInteractiveState.Warning("Warning message goes here"),
        DropdownInteractiveState.Error("Error message goes here"),
        DropdownInteractiveState.Disabled,
        DropdownInteractiveState.ReadOnly
    )

    open fun ComposeUiTest.setup() {
        setContent {
            val expandedStates = remember { MutableTransitionState(false) }
            val transition = updateTransition(expandedStates, "Dropdown")

            CarbonDesignSystem(WhiteTheme) {
                val colors = DropdownColors.colors()
                DropdownField(
                    state = state,
                    dropdownSize = DropdownSize.Large,
                    transition = transition,
                    expandedStates = expandedStates,
                    colors = colors,
                    onExpandedChange = { expandedStates.targetState = it },
                    fieldContent = {
                        DropdownPlaceholderText(
                            placeholderText = placeholder,
                            state = state,
                            colors = colors
                        )

                        DropdownStateIcon(state = state)
                    }
                )
            }
        }
    }

    @CallSuper
    open fun onContentValidation(
        testScope: ComposeUiTest,
        state: DropdownInteractiveState
    ): Unit = with(testScope) {
        onNodeWithTag(DropdownTestTags.FIELD)
            .assertIsDisplayed()
            .assertHeightIsEqualTo(DropdownSize.Large.height)

        onNodeWithTag(DropdownTestTags.FIELD_PLACEHOLDER, useUnmergedTree = true)
            .assertIsDisplayed()

        onNodeWithTag(DropdownTestTags.FIELD_CHEVRON, useUnmergedTree = true)
            .assertIsDisplayed()

        when (state) {
            is DropdownInteractiveState.Enabled,
            is DropdownInteractiveState.ReadOnly -> {
                onNodeWithTag(DropdownTestTags.FIELD_DIVIDER, useUnmergedTree = true)
                    .assertIsDisplayed()

                onNodeWithTag(DropdownTestTags.FIELD_WARNING_ICON, useUnmergedTree = true)
                    .assertIsNotDisplayed()

                onNodeWithTag(DropdownTestTags.FIELD_ERROR_ICON, useUnmergedTree = true)
                    .assertIsNotDisplayed()
            }
            is DropdownInteractiveState.Warning ->
                onNodeWithTag(DropdownTestTags.FIELD_WARNING_ICON, useUnmergedTree = true)
                    .assertIsDisplayed()

            is DropdownInteractiveState.Error -> {
                onNodeWithTag(DropdownTestTags.FIELD_ERROR_ICON, useUnmergedTree = true)
                    .assertIsDisplayed()

                onNodeWithTag(DropdownTestTags.FIELD_DIVIDER, useUnmergedTree = true)
                    .assertIsNotDisplayed()
            }
            is DropdownInteractiveState.Disabled ->
                onNodeWithTag(DropdownTestTags.FIELD)
                    .assertHasNoClickAction()
                    .assert(isFocusable().not())
        }
    }

    @Test
    fun dropdownField_validateContent() = runComposeUiTest {
        setup()
        interactiveStates.forEach {
            state = it
            onContentValidation(this, state)
        }
    }

    @CallSuper
    open fun onLayoutValidationGetFieldContentWidths(
        testScope: ComposeUiTest,
        state: DropdownInteractiveState,
        contentWidths: MutableList<Dp>
    ): Unit = with(testScope) {
        contentWidths += onNodeWithTag(
            DropdownTestTags.FIELD_PLACEHOLDER,
            useUnmergedTree = true
        ).getUnclippedBoundsInRoot().width

        when (state) {
            is DropdownInteractiveState.Enabled,
            is DropdownInteractiveState.ReadOnly,
            is DropdownInteractiveState.Disabled -> {
            }
            is DropdownInteractiveState.Warning ->
                onNodeWithTag(DropdownTestTags.FIELD_WARNING_ICON, useUnmergedTree = true)
                    .assertIsDisplayed()
                    .also {
                        contentWidths.add(it.getUnclippedBoundsInRoot().width)
                        contentWidths.add(SpacingScale.spacing03 * 2) // Horizontal padding
                    }

            is DropdownInteractiveState.Error ->
                onNodeWithTag(DropdownTestTags.FIELD_ERROR_ICON, useUnmergedTree = true)
                    .assertIsDisplayed()
                    .also {
                        contentWidths.add(it.getUnclippedBoundsInRoot().width)
                        contentWidths.add(SpacingScale.spacing03 * 2) // Horizontal padding
                    }
        }
    }

    @Test
    fun dropdownField_validateLayout() = runComposeUiTest {
        setup()
        val contentWidths = mutableListOf<Dp>()

        interactiveStates.forEach {
            state = it

            onLayoutValidationGetFieldContentWidths(this, state, contentWidths)

            onNodeWithTag(DropdownTestTags.FIELD_LAYOUT, useUnmergedTree = true)
                .assertLeftPositionInRootIsEqualTo(SpacingScale.spacing05)
                .assertWidthIsEqualTo(contentWidths.reduce(Dp::plus))

            contentWidths.clear()
        }
    }

    @Test
    fun dropdownField_validateSemantics() = runComposeUiTest {
        setup()
        onNodeWithTag(DropdownTestTags.FIELD)
            .assertHasClickAction()
            .requestFocus()
            .assertIsFocused()
    }

    @Test
    fun dropdownField_disabled_validateSemantics() = runComposeUiTest {
        setup()
        state = DropdownInteractiveState.Disabled
        onNodeWithTag(DropdownTestTags.FIELD)
            .assertHasNoClickAction()
            .assertIsNotEnabled()
            .assert(isFocusable().not())
    }

    @Test
    fun dropdownField_readOnly_validateSemantics() = runComposeUiTest {
        setup()
        state = DropdownInteractiveState.ReadOnly
        onNodeWithTag(DropdownTestTags.FIELD)
            .assertIsReadOnly()
    }
}
