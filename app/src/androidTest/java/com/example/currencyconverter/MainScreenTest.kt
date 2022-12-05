package com.example.currencyconverter

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.currencyconverter.data.RatesRepository
import com.example.currencyconverter.ui.MainScreen
import com.example.currencyconverter.ui.MainViewModel
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class MainScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Checks that Main screen can display the content
     */
    @Test
    fun screenContainsCurrencies() {
        composeTestRule.setContent {
            MainScreen(
                viewModel = MainViewModel(RatesRepository(FakeSuccessDataSource(),FakeSuccessDataSource()))
            )
        }
        runBlocking {
            composeTestRule.onNodeWithText("USD").assertExists()
        }
    }
    /**
     * Checks that Main screen can display errors
     */
    @Test
    fun screenContainsErrors() {
        composeTestRule.setContent {
            MainScreen(
                viewModel = MainViewModel(RatesRepository(FakeFailingDataSource(),FakeFailingDataSource()))
            )
        }
        runBlocking {
            composeTestRule.onNodeWithText("failed to get data").assertExists()
        }
    }
}