package carbon.compose.dropdown

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.remember
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
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
import carbon.compose.dropdown.multiselect.DropdownMultiselectTag
import carbon.compose.foundation.color.WhiteTheme
import kotlin.test.Test

class MultiselectDropdownFieldTest : DropdownFieldTest() {

    override fun ComposeUiTest.setup() {
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
                        DropdownMultiselectTag(
                            state = state,
                            count = 1,
                            onCloseTagClick = {}
                        )

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

    override fun onContentValidation(
        testScope: ComposeUiTest,
        state: DropdownInteractiveState
    ) {
        super.onContentValidation(testScope, state)

        testScope.onNodeWithTag(DropdownTestTags.FIELD_MULTISELECT_TAG, useUnmergedTree = true)
            .assertIsDisplayed()
    }

    override fun onLayoutValidationGetFieldContentWidths(
        testScope: ComposeUiTest,
        state: DropdownInteractiveState,
        contentWidths: MutableList<Dp>
    ) {
        super.onLayoutValidationGetFieldContentWidths(testScope, state, contentWidths)

        contentWidths.add(
            testScope.onNodeWithTag(DropdownTestTags.FIELD_MULTISELECT_TAG, useUnmergedTree = true)
                .getUnclippedBoundsInRoot()
                .width
        )
    }

    @Test
    fun dropdownField_multiselectTag_validateSemantics() = runComposeUiTest {
        setup()

        interactiveStates.forEach { state ->
            this@MultiselectDropdownFieldTest.state = state

            if (state in arrayOf(
                    DropdownInteractiveState.Disabled, DropdownInteractiveState.ReadOnly
                )
            ) {
                onNodeWithTag(DropdownTestTags.FIELD_MULTISELECT_TAG, useUnmergedTree = true)
                    .assertIsNotEnabled()
            } else {
                onNodeWithTag(DropdownTestTags.FIELD_MULTISELECT_TAG, useUnmergedTree = true)
                    .assertIsEnabled()
                    .assertHasClickAction()
            }
        }
    }
}
