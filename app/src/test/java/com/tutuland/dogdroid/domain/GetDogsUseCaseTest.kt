package com.tutuland.dogdroid.domain

import app.cash.turbine.test
import com.tutuland.dogdroid.DogInfoRepositoryException
import com.tutuland.dogdroid.DogPreferencesRepositoryException
import com.tutuland.dogdroid.data.info.DogInfoRepository
import com.tutuland.dogdroid.data.preferences.DogPreferencesRepository
import com.tutuland.dogdroid.fixDogPrefDefault
import com.tutuland.dogdroid.fixListOfDogInfo
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class GetDogsUseCaseTest {
    @MockK lateinit var infoRepo: DogInfoRepository
    @MockK lateinit var prefRepo: DogPreferencesRepository
    private lateinit var getDogs: GetDogsUseCase

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        getDogs = GetDogsUseCase(infoRepo, prefRepo)
    }

    @Test
    fun `when getDogs called and infoRepo throws, fail`() = runTest {
        coEvery { infoRepo.getDogs() } returns flow { throw DogInfoRepositoryException }

        getDogs().test {
            assertIs<DogInfoRepositoryException>(awaitError())
            expectNoEvents()
        }

        coVerify { infoRepo.getDogs() }
        confirmVerified(prefRepo, infoRepo)
    }

    @Test
    fun `when getDogs called and prefRepo throws, fail`() = runTest {
        coEvery { infoRepo.getDogs() } returns flowOf(fixListOfDogInfo)
        coEvery { prefRepo.getPreferencesFor(any()) } throws DogPreferencesRepositoryException

        getDogs().test {
            assertIs<DogPreferencesRepositoryException>(awaitError())
            expectNoEvents()
        }

        coVerify { infoRepo.getDogs() }
        coVerify(exactly = 1) { prefRepo.getPreferencesFor(any()) }
        confirmVerified(prefRepo, infoRepo)
    }

    @Test
    fun `when getDogs called and repos return data, map it to dog list`() = runTest {
        coEvery { infoRepo.getDogs() } returns flowOf(fixListOfDogInfo)
        coEvery { prefRepo.getPreferencesFor(any()) } returns fixDogPrefDefault

        getDogs().test {
            assertIs<List<Dog>>(awaitItem())
            awaitComplete()
        }

        coVerify { infoRepo.getDogs() }
        coVerify(exactly = 3) { prefRepo.getPreferencesFor(any()) }
        confirmVerified(prefRepo, infoRepo)
    }
}
