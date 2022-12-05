package com.example.currencyconverter.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import com.example.currencyconverter.MainActivity
import com.example.currencyconverter.data.source.local.StoredRatesDataSource
import com.example.currencyconverter.data.source.remote.APIRatesDataSource
import com.example.currencyconverter.model.RatesData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DataSourceTest {

    @Test
    fun localStorageGet() = runTest {
        val testDataStore = PreferenceDataStoreFactory.create(
            produceFile = { ApplicationProvider.getApplicationContext<MainActivity>().preferencesDataStoreFile("TEST_PREFS") }
        )
        val success = when(StoredRatesDataSource(testDataStore).getRates()) {
            is Result.Success -> true
            is Result.Error -> false
        }
        assert(success)
    }

    @Test
    fun localStorageSave() = runTest {
        val testDataStore = PreferenceDataStoreFactory.create(
            produceFile = { ApplicationProvider.getApplicationContext<MainActivity>().preferencesDataStoreFile("TEST_PREFS") }
        )
        val success = try {
            StoredRatesDataSource(testDataStore).saveRates(Result.Success<RatesData>(RatesData()))
            true
        } catch (e:Exception) {
            false
        }
        assert(success)
    }

    @Test
    fun remoteStorageGet() = runTest {
        val success = when(APIRatesDataSource().getRates()) {
            is Result.Success -> true
            is Result.Error -> false
        }
        assert(success)
    }
}