/*
 * Copyright 2024 Gabriel Derrien
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gabrieldrn.carbon.dropdown

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.runComposeUiTest
import com.gabrieldrn.carbon.dropdown.base.DropdownInteractiveState
import com.gabrieldrn.carbon.dropdown.base.DropdownOption
import com.gabrieldrn.carbon.dropdown.base.DropdownSize
import com.gabrieldrn.carbon.dropdown.base.DropdownTestTags
import com.gabrieldrn.carbon.dropdown.multiselect.MultiselectDropdown
import com.gabrieldrn.carbon.toList
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiselectDropdownTest {

    private var state by mutableStateOf<DropdownInteractiveState>(DropdownInteractiveState.Enabled)
    private val options = (0..9).associateWith { DropdownOption("Option $it") }
    private val selectedOptions = mutableStateListOf<Int>()
    private val minVisibleItems = 4
    private var dropdownSize by mutableStateOf(DropdownSize.Large)
    private var isExpanded by mutableStateOf(false)
    private val placeholder = "Multiselect dropdown"

    fun ComposeUiTest.setup() {
        setContent {
            MultiselectDropdown(
                expanded = isExpanded,
                placeholder = placeholder,
                options = options,
                selectedOptions = selectedOptions,
                onOptionClicked = selectedOptions::add,
                onClearSelection = selectedOptions::clear,
                onExpandedChange = { isExpanded = it },
                onDismissRequest = { isExpanded = false },
                minVisibleItems = minVisibleItems,
                dropdownSize = dropdownSize,
                state = state,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    fun tearDown() {
        state = DropdownInteractiveState.Enabled
        isExpanded = false
        selectedOptions.clear()
        dropdownSize = DropdownSize.Large
    }

    // Same test as in DropdownTest.kt, could it be merged?
    @Test
    fun multiselectDropdown_optionsPopup_validateLayout() = runComposeUiTest {
        setup()

        isExpanded = true

        onNodeWithTag(DropdownTestTags.POPUP_CONTENT)
            .assertIsDisplayed()

        onAllNodesWithTag(DropdownTestTags.MENU_OPTION).run {
            assertCountEquals(minVisibleItems + 1)
            toList().forEach { it.assertIsDisplayed() }
        }

        onNodeWithTag(DropdownTestTags.POPUP_CONTENT)
            .performScrollToNode(hasText("Option 9"))

        onNode(hasTestTag(DropdownTestTags.MENU_OPTION).and(hasText("Option 9")))
            .assertIsDisplayed()

        tearDown()
    }

    @Test
    fun multiselectDropdown_optionsPopup_onOptionClick_validateCallbackIsInvoked() =
        runComposeUiTest {
            setup()

            isExpanded = true

            onAllNodesWithTag(DropdownTestTags.MENU_OPTION)
                .onFirst()
                .performClick()

            assertEquals(
                expected = 1,
                actual = selectedOptions.size
            )

            tearDown()
        }

    @Test
    fun multiselectDropdown_optionsPopup_onOptionClick_optionsListIsNotReorganized() =
        runComposeUiTest {
            setup()

            isExpanded = true

            onAllNodesWithTag(DropdownTestTags.MENU_OPTION)
                .get(3)
                .performClick()

            onAllNodesWithTag(DropdownTestTags.MENU_OPTION)
                .get(0)
                .assert(hasText("Option 0")) // Option 0 is still at the top of the list

            tearDown()
        }

    @Test
    fun multiselectDropdown_optionsPopup_withSelectedOptions_selectedOptionsAreOnTopOfList() =
        runComposeUiTest {
            setup()

            selectedOptions.addAll(listOf(3, 5, 9))
            isExpanded = true

            onAllNodesWithTag(DropdownTestTags.MENU_OPTION).run {
                assertCountEquals(minVisibleItems + 1)
                toList().run {
                    get(0).assert(hasText("Option 3"))
                    get(1).assert(hasText("Option 5"))
                    get(2).assert(hasText("Option 9"))
                }
            }

            tearDown()
        }

    @Test
    fun multiselectDropdown_withSelectedOptions_tagIsDisplayedWithCountOfSelectedOptions() =
        runComposeUiTest {
            setup()

            selectedOptions.addAll(listOf(1, 2, 3))

            onNodeWithTag(DropdownTestTags.FIELD_MULTISELECT_TAG)
                .assert(hasText("3"))

            tearDown()
        }

    @Test
    fun multiselectDropdown_onClearSelection_validateCallbackIsInvoked() = runComposeUiTest {
        setup()

        selectedOptions.addAll(listOf(0, 1, 2))

        onNodeWithTag(DropdownTestTags.FIELD_MULTISELECT_TAG)
            .performClick()

        assertEquals(
            expected = 0,
            actual = selectedOptions.size
        )

        tearDown()
    }
}
