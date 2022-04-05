package com.tutuland.dogdroid.data

import app.cash.turbine.test
import com.tutuland.dogdroid.data.local.LocalDogsSource
import com.tutuland.dogdroid.data.remote.RemoteDogsSource
import com.tutuland.dogdroid.fixDog
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
class DogRepositoryTest {
    @MockK lateinit var localSource: LocalDogsSource
    @MockK lateinit var remoteSource: RemoteDogsSource
    private lateinit var repository: DogRepository

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        repository = DogRepository.WithLocalCaching(localSource, remoteSource)
    }

    @Test
    fun `when getDogs called and localSource throws, fail`() = runTest {
        coEvery { localSource.getDogs() } throws IllegalStateException()

        assertFailsWith<IllegalStateException> { repository.getDogs() }

        coVerifyOrder {
            localSource.getDogs()
        }
        confirmVerified(localSource, remoteSource)
    }

    @Test
    fun `when getDogs called and localSource returns results, emit them`() = runTest {
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
    fun `when getDogs called and localSource returns no results, request from remoteSource`() = runTest {
        coEvery { localSource.getDogs() } returns flowOf(emptyList())

        repository.getDogs().test {
            assertEquals(emptyList(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerifyOrder {
            localSource.getDogs()
            remoteSource.requestDogsRemotely()
        }
        confirmVerified(localSource, remoteSource)
    }

    @Test
    fun `when saveDog called, save it to localSource`() = runTest {
        repository.saveDog(fixDog)

        coVerifyOrder {
            localSource.saveDog(fixDog)
        }
        confirmVerified(localSource, remoteSource)
    }

    @Test
    fun `when refreshData called, cancelRequestingDogs and deleteDogs`() = runTest {
        repository.refreshData()

        coVerifyOrder {
            remoteSource.cancelRequestingDogs()
            localSource.deleteDogs()
        }
        confirmVerified(localSource, remoteSource)
    }
}
