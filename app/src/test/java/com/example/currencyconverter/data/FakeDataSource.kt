package com.example.currencyconverter.data

import com.example.currencyconverter.data.source.RatesDataSource
import com.example.currencyconverter.model.RatesData

class FakeFailingDataSource : RatesDataSource {
    override suspend fun getRates(): Result<RatesData> {
            return Result.Error(Exception("failed to get data"))
    }
    override suspend fun saveRates(rates: Result.Success<RatesData>) {
    }
}
class FakeSuccessDataSource : RatesDataSource {
    override suspend fun getRates(): Result<RatesData> {
        return Result.Success(RatesData(listOf("AED" to 3.67,"EUR" to 0.99,"JPY" to 146.20,"USD" to 1.0), System.currentTimeMillis()/1000L))
    }
    override suspend fun saveRates(rates: Result.Success<RatesData>) {
    }
}
