package com.example.currencyconverter.data.source.remote

import com.example.currencyconverter.model.RatesData
import com.example.currencyconverter.data.source.RatesDataSource
import com.example.currencyconverter.data.Result
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * imports the rates from openexchangerates.org
 */
class APIRatesDataSource: RatesDataSource {

    override suspend fun getRates(): Result<RatesData> {
        return try {
            Result.Success(
                withContext(Dispatchers.IO) { retrieveDataFromAPI() }
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun retrieveDataFromAPI(): RatesData {
        val url =
            URL("https://openexchangerates.org/api/latest.json?app_id=ca2b7b8f76ba410a8f7b9bd4cffb774a&prettyprint=true&show_alternative=false")
        val connection = url.openConnection() as HttpsURLConnection

        if (connection.responseCode == 200) {
            val inputStream = connection.inputStream
            val inputStreamReader = inputStream.reader(charset("UTF-8"))
            val receivedData = Gson().fromJson(inputStreamReader, RequestData::class.java)
            inputStreamReader.close()
            inputStream.close()
            return RatesData(receivedData.rates.toList(), System.currentTimeMillis()/1000L)
        } else {
            throw Exception("Could not connect to the API")
        }
    }

    override suspend fun saveRates(rates: Result.Success<RatesData>) {
        throw Exception("illegal operation error: cannot save to API")
    }

    class RequestData (
        var rates: Map<String, Double>
    )
}