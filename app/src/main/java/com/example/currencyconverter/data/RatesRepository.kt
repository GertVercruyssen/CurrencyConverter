package com.example.currencyconverter.data

import com.example.currencyconverter.model.RatesData
import com.example.currencyconverter.data.source.RatesDataSource

/**
 * Supplies the currency rates.
 * all functions suspend so access them via a coroutine
 * Should be accessed via Hilt
 */
class RatesRepository (
    private val storedDataSource: RatesDataSource,
    private val remoteDataSource: RatesDataSource)
{
    private var latestRatesData = Result.Success(RatesData())
    private val fetchInterval = 1800 //data becomes old after 30 minutes

    suspend fun getRates(): Result<RatesData> {
        var faultResult: Result<RatesData>? =null

        //if no data is present, load the stored data
        if(latestRatesData.data.timestamp == 0L) {
            when ( val storedResult = storedDataSource.getRates()) {
                is Result.Success -> latestRatesData = storedResult
                is Result.Error -> faultResult = storedResult
            }
        }

        //if our data is old, get new data from the API and save
        if ((System.currentTimeMillis() / 1000L) - latestRatesData.data.timestamp > fetchInterval) {
            when ( val remoteResult = remoteDataSource.getRates()) {
                is Result.Success -> {
                    latestRatesData = remoteResult
                    faultResult = null
                    saveRates()
                }
                is Result.Error -> faultResult = remoteResult
            }
        }
        return faultResult ?: latestRatesData
    }

    suspend fun saveRates() {
        storedDataSource.saveRates(latestRatesData)
    }
}