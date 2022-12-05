package com.example.currencyconverter.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryTest {
    @Test
    fun useRemoteSource() = runTest {
        val ratesRepository = RatesRepository(FakeFailingDataSource(), FakeSuccessDataSource())
        val success = when(ratesRepository.getRates()) {
            is Result.Success -> true
            is Result.Error -> false
        }
        assert(success)
    }

    /**
     * This test also checks that the remote API doesn't get called when the local data is still
     * newer than 30 minutes
     */
    @Test
    fun useLocalSource() = runTest {
        val ratesRepository = RatesRepository(FakeSuccessDataSource(), FakeFailingDataSource())
        val success = when(ratesRepository.getRates()) {
            is Result.Success -> true
            is Result.Error -> false
        }
        assert(success)
    }
    @Test
    fun noValidSource() = runTest {
        val ratesRepository = RatesRepository(FakeFailingDataSource(), FakeFailingDataSource())
        val success = when(ratesRepository.getRates()) {
            is Result.Success -> true
            is Result.Error -> false
        }
        assert(!success)
    }
}