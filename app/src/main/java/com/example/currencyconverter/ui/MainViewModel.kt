package com.example.currencyconverter.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.RatesRepository
import com.example.currencyconverter.data.Result
import com.example.currencyconverter.model.RatesData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Model for the state that is shared with the view
 * default values here will be used if there is no data from the repository
 */
@Stable
data class MainUIState(
    val currentAmount: Double = 0.0,
    val selectedCurrency: String = "",
    val latestError: String? = null,
    val convertedCurrencyList: List<Pair<String, Double>> = listOf()
)

@HiltViewModel
class MainViewModel @Inject constructor (
    private val ratesRepository: RatesRepository
) : ViewModel() {
    private var ratesData: RatesData = RatesData()
    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(MainUIState())
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

    init {
        refreshRates()
    }

    //The rates are collected during launch
    fun refreshRates() {
        viewModelScope.launch {
            when (val result = ratesRepository.getRates()) {
                is Result.Success -> {
                    ratesData = result.data
                    _uiState.update {
                        it.copy(
                            latestError = null,
                            selectedCurrency = result.data.rates.first().first,
                            convertedCurrencyList = result.data.rates.map { currency ->
                                currency.first to calculateValues(it.currentAmount, ratesData.rates, result.data.rates.first().first , currency.first)
                            }
                        )
                    }
                }
                is Result.Error -> _uiState.update { it.copy(latestError = result.exception.message) }
            }
        }
    }

    //is called every time the user changes the text field
    fun setNewAmount(input: String) {
        var amount = 0.0
        try {
            amount = input.toDouble()
        } catch (e: NumberFormatException) {
            //don't do anything
        }
        _uiState.update {
            it.copy(
                currentAmount = amount,
                convertedCurrencyList = ratesData.rates.map { currency ->
                    currency.first to calculateValues(amount, ratesData.rates,it.selectedCurrency, currency.first)
                }
            )
        }
    }

    fun setNewCurrency(input: String) {
        _uiState.update {
            it.copy(
                selectedCurrency = input,
                convertedCurrencyList = ratesData.rates.map { currency ->
                    currency.first to calculateValues(it.currentAmount, ratesData.rates, input , currency.first)
                }
            )
        }
    }

    //Converts the amount from its source currency to its target currency
    //Business logic
    //divide the amount by the exchange rate to get USD value, then multiply with the rate for the target currency
    private fun calculateValues(amount: Double, rates: List<Pair<String, Double>>, sourceCurrency: String, targetCurrency: String): Double {
        if(!rates.any{it.first == _uiState.value.selectedCurrency})
            return 0.0
        else
            return amount / rates.first { it.first == sourceCurrency }.second * rates.first { it.first == targetCurrency }.second
    }
}

