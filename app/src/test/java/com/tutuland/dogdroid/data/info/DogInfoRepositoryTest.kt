package com.tutuland.dogdroid.data.info

import app.cash.turbine.test
import com.tutuland.dogdroid.data.info.local.DogInfoLocalSource
import com.tutuland.dogdroid.data.info.remote.DogInfoRemoteSource
import com.tutuland.dogdroid.fixDogInfo
import com.tutuland.dogdroid.fixListOfDogInfo
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
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

class DogInfoRepositoryTest {
    @MockK lateinit var localSource: DogInfoLocalSource
    @MockK lateinit var remoteSource: DogInfoRemoteSource
    private lateinit var infoRepository: DogInfoRepository
    private lateinit var scope: TestScope

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        scope = TestScope()
        infoRepository = DogInfoRepository.WithLocalCaching(localSource, remoteSource, scope)
    }

    @Test
    fun `when getDogs called and localSource throws, fail`() = runTest {
        coEvery { localSource.getDogs() } throws IllegalStateException()

        assertFailsWith<IllegalStateException> { infoRepository.getDogs() }

        coVerify { localSource.getDogs() }
        confirmVerified(localSource, remoteSource)
    }

    @Test
    fun `when getDogs called and localSource returns results, emit them`() = runTest {
        coEvery { localSource.getDogs() } returns flowOf(fixListOfDogInfo)

        infoRepository.getDogs().test {
            assertEquals(fixListOfDogInfo, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { localSource.getDogs() }
        confirmVerified(localSource, remoteSource)
    }

    @Test
    fun `when getDogs called and localSource returns no results, request from remoteSource`() = runTest {
        coEvery { localSource.getDogs() } returns flowOf(emptyList())
        coEvery { remoteSource.getDogs() } returns emptyFlow()

        infoRepository.getDogs().test {
            scope.advanceUntilIdle()
            cancelAndIgnoreRemainingEvents()
        }

        coVerifyOrder {
            localSource.getDogs()
            localSource.deleteDogs()
            remoteSource.getDogs()
        }
        confirmVerified(localSource, remoteSource)
    }

    @Test
    fun `when saveDog called, save it to localSource`() = runTest {
        infoRepository.saveDog(fixDogInfo)

        coVerify { localSource.saveDog(fixDogInfo) }
        confirmVerified(localSource, remoteSource)
    }

    @Test
    fun `when refreshData called, deleteDogs on localSource and getDogs from remoteSource`() = runTest {
        infoRepository.refreshData()
        scope.advanceUntilIdle()

        coVerifyOrder {
            localSource.deleteDogs()
            remoteSource.getDogs()
        }
        confirmVerified(localSource, remoteSource)
    }
}
