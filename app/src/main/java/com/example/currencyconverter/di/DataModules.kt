package com.example.currencyconverter.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.currencyconverter.data.RatesRepository
import com.example.currencyconverter.data.source.local.StoredRatesDataSource
import com.example.currencyconverter.data.source.remote.APIRatesDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class StoredDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PreferenceDataStore

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRatesRepository(
        @StoredDataSource storedDataSource: StoredRatesDataSource,
        @RemoteDataSource remoteDataSource: APIRatesDataSource
    ): RatesRepository {
        return RatesRepository(storedDataSource,remoteDataSource)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @RemoteDataSource
    @Provides
    fun provideRemoteDataSource(): APIRatesDataSource = APIRatesDataSource()

    @Singleton
    @StoredDataSource
    @Provides
    fun provideStoredDataSource(@PreferenceDataStore dataStore: DataStore<Preferences>)
    : StoredRatesDataSource = StoredRatesDataSource(dataStore)
}

private const val USER_PREFERENCES = "user_preferences"

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {

    @Singleton
    @PreferenceDataStore
    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }
}

