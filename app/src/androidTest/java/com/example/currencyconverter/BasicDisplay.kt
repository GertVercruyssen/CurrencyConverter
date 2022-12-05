package com.example.currencyconverter

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class BasicDisplay {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Test if the screen displays
     */
    @Test
    fun screenDisplaysWithDefaultValues() {
        val text = composeTestRule.activity.getString(R.string.arrow_down)
        composeTestRule.onNodeWithText(text).assertExists()
    }
}