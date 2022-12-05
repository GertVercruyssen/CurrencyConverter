package com.example.currencyconverter.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.currencyconverter.data.Result
import com.example.currencyconverter.model.RatesData
import com.example.currencyconverter.data.source.RatesDataSource
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import java.io.IOException

/**
 * imports and saves the rates to local storage
 * local storage is implemented as a Preferences DataStore for simplicity
 * The datastore is provided via Hilt
 */
class StoredRatesDataSource(private val dataStore: DataStore<Preferences>) : RatesDataSource {

    private val savedRatesFlow: Flow<String> = dataStore.data.catch { exception ->
        // dataStore.data throws an IOException when an error is encountered when reading data
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        it[PreferencesKeys.SAVED_RATES] ?: ""
    }

    override suspend fun getRates(): Result<RatesData> {
        return try {
            val json: String = savedRatesFlow.first()
            val loadedRates: RatesData = Gson().fromJson(json, RatesData::class.java)
            Result.Success(loadedRates)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveRates(rates: Result.Success<RatesData>) {
        val json = Gson().toJson(rates.data)
        dataStore.edit { preferences -> preferences[PreferencesKeys.SAVED_RATES] = json}
    }

    private object PreferencesKeys {
        val SAVED_RATES = stringPreferencesKey("saved_rates")
    }
}