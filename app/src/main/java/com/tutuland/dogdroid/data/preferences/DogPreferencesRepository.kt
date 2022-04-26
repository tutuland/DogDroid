package com.tutuland.dogdroid.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

interface DogPreferencesRepository {
    suspend fun getPreferencesFor(breed: String): DogPreferences
    suspend fun setPreferencesFor(breed: String, preferences: DogPreferences)

    class FromStorage(private val dataStore: DataStore<Preferences>) : DogPreferencesRepository {
        override suspend fun getPreferencesFor(breed: String): DogPreferences {
            val isFavoriteKey = booleanPreferencesKey(breed)
            val dogPreferences: DogPreferences = dataStore.data.first().let { prefs ->
                val isFavorite = prefs[isFavoriteKey] ?: false
                DogPreferences(isFavorite)
            }
            return dogPreferences
        }

        override suspend fun setPreferencesFor(breed: String, preferences: DogPreferences) {
            val isFavoriteKey = booleanPreferencesKey(breed)
            dataStore.edit { prefs ->
                prefs[isFavoriteKey] = preferences.isFavorite
            }
        }
    }
}
