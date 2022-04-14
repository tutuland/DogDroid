package com.tutuland.dogdroid.data.source.local

import app.cash.turbine.test
import com.tutuland.dogdroid.fixDog
import com.tutuland.dogdroid.fixDogEntity
import com.tutuland.dogdroid.fixListOfDogEntities
import com.tutuland.dogdroid.fixListOfDogs
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class DogLocalSourceTest {
    @MockK lateinit var database: DogDatabase
    lateinit var source: DogLocalSource

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        source = DogLocalSource.FromDatabase(database)
    }

    @Test
    fun `when getDogs called and database throws, fail`() = runTest {
        coEvery { database.getDogs() } throws IllegalStateException()

        assertFailsWith<IllegalStateException> { source.getDogs() }

        coVerifyOrder {
            database.getDogs()
        }
        confirmVerified(database)
    }

    @Test
    fun `when getDogs called and database returns entities, process them and emit`() = runTest {
        coEvery { database.getDogs() } returns flowOf(fixListOfDogEntities)

        source.getDogs().test {
            assertEquals(fixListOfDogs, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerifyOrder {
            database.getDogs()
        }
        confirmVerified(database)
    }

    @Test
    fun `when getDogs called and database returns an empty list, emit an empty list`() = runTest {
        coEvery { database.getDogs() } returns flowOf(emptyList())

        source.getDogs().test {
            assertEquals(emptyList(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerifyOrder {
            database.getDogs()
        }
        confirmVerified(database)
    }

    @Test
    fun `when saveDog called, map to entity and delegate to database`() = runTest {
        source.saveDog(fixDog)

        coVerifyOrder {
            database.saveDogs(fixDogEntity)
        }
        confirmVerified(database)
    }

    @Test
    fun `when saveDog deleteDogs, delegate to database`() = runTest {
        source.deleteDogs()

        coVerifyOrder {
            database.deleteDogs()
        }
        confirmVerified(database)
    }
}
