package com.example.currencyconverter.data.source
import com.example.currencyconverter.data.Result
import com.example.currencyconverter.model.RatesData

interface RatesDataSource {
    suspend fun getRates(): Result<RatesData>

    suspend fun saveRates(rates: Result.Success<RatesData>)
}