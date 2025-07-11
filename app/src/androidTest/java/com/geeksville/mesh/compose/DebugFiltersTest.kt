/*
 * Copyright (c) 2025 Meshtastic LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.geeksville.mesh.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geeksville.mesh.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.platform.app.InstrumentationRegistry
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.geeksville.mesh.ui.debug.FilterMode

@RunWith(AndroidJUnit4::class)
class DebugFiltersTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun debugFilterBar_showsFilterButtonAndMenu() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filterLabel = context.getString(R.string.debug_filters)
        composeTestRule.setContent {
            var filterTexts by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(listOf<String>()) }
            var customFilterText by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
            val presetFilters = listOf("Error", "Warning", "Info")
            val logs = listOf(
                com.geeksville.mesh.model.DebugViewModel.UiMeshLog(
                    uuid = "1",
                    messageType = "Info",
                    formattedReceivedDate = "2024-01-01 12:00:00",
                    logMessage = "Sample log message"
                )
            )
            com.geeksville.mesh.ui.debug.DebugFilterBar(
                filterTexts = filterTexts,
                onFilterTextsChange = { filterTexts = it },
                customFilterText = customFilterText,
                onCustomFilterTextChange = { customFilterText = it },
                presetFilters = presetFilters,
                logs = logs
            )
        }
        // The filter button should be visible
        composeTestRule.onNodeWithText(filterLabel).assertIsDisplayed()
    }

    @Test
    fun debugFilterBar_addCustomFilter_displaysActiveFilter() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val activeFiltersLabel = context.getString(R.string.debug_active_filters)
        composeTestRule.setContent {
            var filterTexts by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(listOf<String>()) }
            var customFilterText by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
            com.geeksville.mesh.ui.debug.DebugActiveFilters(
                filterTexts = filterTexts,
                onFilterTextsChange = { filterTexts = it }, 
                filterMode = FilterMode.OR,
                onFilterModeChange = {}
            )
            com.geeksville.mesh.ui.debug.DebugCustomFilterInput(
                customFilterText = customFilterText,
                onCustomFilterTextChange = { customFilterText = it },
                filterTexts = filterTexts,
                onFilterTextsChange = { filterTexts = it },
            )
        }
        // Add a custom filter
        composeTestRule.onNodeWithText("Add custom filter").performTextInput("MyFilter")
        composeTestRule.onNodeWithContentDescription("Add filter").performClick()
        // The active filters label and the filter chip should be visible
        composeTestRule.onNodeWithText(activeFiltersLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText("MyFilter").assertIsDisplayed()
    }

    @Test
    fun debugActiveFilters_clearAllFilters_removesFilters() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val activeFiltersLabel = context.getString(R.string.debug_active_filters)
        composeTestRule.setContent {
            var filterTexts by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(listOf("A", "B")) }
            com.geeksville.mesh.ui.debug.DebugActiveFilters(
                filterTexts = filterTexts,
                onFilterTextsChange = { filterTexts = it },
                filterMode = FilterMode.OR,
                onFilterModeChange = {}
            )
        }
        // The active filters label and chips should be visible
        composeTestRule.onNodeWithText(activeFiltersLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("B").assertIsDisplayed()
        // Click the clear all filters button
        composeTestRule.onNodeWithContentDescription("Clear all filters").performClick()
        // The filter chips should no longer be visible
        composeTestRule.onNodeWithText("A").assertDoesNotExist()
        composeTestRule.onNodeWithText("B").assertDoesNotExist()
    }
} 