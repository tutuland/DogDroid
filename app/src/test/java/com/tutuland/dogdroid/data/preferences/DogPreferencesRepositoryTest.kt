package com.tutuland.dogdroid.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import com.tutuland.dogdroid.fixBreed
import com.tutuland.dogdroid.fixDogPrefDefault
import com.tutuland.dogdroid.fixDogPrefFav
import com.tutuland.dogdroid.fixPrefsKey
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class DogPreferencesRepositoryTest {
    @MockK lateinit var dataStore: DataStore<Preferences>
    @MockK lateinit var prefs: MutablePreferences
    private lateinit var repository: DogPreferencesRepository

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        repository = DogPreferencesRepository.FromStorage(dataStore)
    }

    @Test
    fun `when dataStore data throws, fail`() = runTest {
        coEvery { dataStore.data } throws IllegalStateException()

        assertFailsWith<IllegalStateException> { repository.getPreferencesFor(fixBreed) }

        coVerify { dataStore.data }
        confirmVerified(dataStore, prefs)
    }

    @Test
    fun `when dataStore data returns nothing, fail`() = runTest {
        coEvery { dataStore.data } returns flow { }

        assertFailsWith<NoSuchElementException> { repository.getPreferencesFor(fixBreed) }

        coVerify { dataStore.data }
        confirmVerified(dataStore, prefs)
    }

    @Test
    fun `when dataStore has no prefs for breed, return default prefs`() = runTest {
        coEvery { dataStore.data } returns flowOf(prefs)
        coEvery { prefs[fixPrefsKey] } returns null

        val result = repository.getPreferencesFor(fixBreed)

        assertEquals(fixDogPrefDefault, result)
        coVerifyOrder {
            dataStore.data
            prefs[fixPrefsKey]
        }
        confirmVerified(dataStore, prefs)
    }

    @Test
    fun `when dataStore has prefs for breed, return stored prefs`() = runTest {
        coEvery { dataStore.data } returns flowOf(prefs)
        coEvery { prefs[fixPrefsKey] } returns true

        val result = repository.getPreferencesFor(fixBreed)

        assertEquals(fixDogPrefFav, result)
        coVerifyOrder {
            dataStore.data
            prefs[fixPrefsKey]
        }
        confirmVerified(dataStore, prefs)
    }

    @Test
    fun `when setPreferencesFor breed, store it on dataStore`() = runTest {
        coEvery { dataStore.updateData(any()) } returns prefs

        repository.setPreferencesFor(fixBreed, fixDogPrefFav)

        coVerifyOrder {
            dataStore.updateData(any())
        }
        confirmVerified(dataStore, prefs)
    }
}
