package com.example.currencyconverter.ui

import android.provider.Telephony.Mms.Rate
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.FakeFailingDataSource
import com.example.currencyconverter.data.FakeSuccessDataSource
import com.example.currencyconverter.data.RatesRepository
import com.example.currencyconverter.model.RatesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    /**
     * check the contents of the UIState produced by a MainViewModel
     */
    @Test
    fun uiStateContent() = runTest {
        // Set Main dispatcher to not run coroutines eagerly, for just this one test
        Dispatchers.setMain(StandardTestDispatcher())

        val viewModel = MainViewModel(RatesRepository(FakeSuccessDataSource(),FakeSuccessDataSource()))
        viewModel.refreshRates()
        viewModel.setNewAmount("123.123")
        viewModel.setNewCurrency("USD")
        val mainUIState = viewModel.uiState.first()
        assert(mainUIState.latestError == null)
        assert(mainUIState.selectedCurrency == "USD")
        assert(mainUIState.currentAmount == 123.123)
    }
}