package com.tutuland.dogdroid.data

import app.cash.turbine.test
import com.tutuland.dogdroid.data.source.local.DogLocalSource
import com.tutuland.dogdroid.data.source.remote.DogRemoteSource
import com.tutuland.dogdroid.fixDog
import com.tutuland.dogdroid.fixListOfDogs
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

class DogRepositoryTest {
    @MockK lateinit var localSource: DogLocalSource
    @MockK lateinit var remoteSource: DogRemoteSource
    private lateinit var repository: DogRepository

    private fun prepareAndRunTest(testBody: suspend TestScope.() -> Unit) = runTest {
        MockKAnnotations.init(this@DogRepositoryTest, relaxed = true)
        repository = DogRepository.WithLocalCaching(localSource, remoteSource, this)
    }

    @Test
    fun `when getDogs called and localSource throws, fail`() = prepareAndRunTest {
        coEvery { localSource.getDogs() } throws IllegalStateException()

        assertFailsWith<IllegalStateException> { repository.getDogs() }

        coVerifyOrder {
            localSource.getDogs()
        }
        confirmVerified(localSource, remoteSource)
    }

    @Test
    fun `when getDogs called and localSource returns results, emit them`() = prepareAndRunTest {
        coEvery { localSource.getDogs() } returns flowOf(fixListOfDogs)

        repository.getDogs().test {
            assertEquals(fixListOfDogs, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerifyOrder {
            localSource.getDogs()
        }
        confirmVerified(localSource, remoteSource)
    }

    @Test
    fun `when getDogs called and localSource returns no results, request from remoteSource`() = prepareAndRunTest {
        coEvery { localSource.getDogs() } returns flowOf(emptyList())

        repository.getDogs().test {
            assertEquals(emptyList(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerifyOrder {
            localSource.getDogs()
            remoteSource.getDogs()
        }
        confirmVerified(localSource, remoteSource)
    }

    @Test
    fun `when saveDog called, save it to localSource`() = prepareAndRunTest {
        repository.saveDog(fixDog)

        coVerifyOrder {
            localSource.saveDog(fixDog)
        }
        confirmVerified(localSource, remoteSource)
    }

    @Test
    fun `when refreshData called, cancelRequestingDogs and deleteDogs`() = prepareAndRunTest {
        repository.refreshData()

        coVerifyOrder {
            localSource.deleteDogs()
            remoteSource.getDogs()
        }
        confirmVerified(localSource, remoteSource)
    }
}
