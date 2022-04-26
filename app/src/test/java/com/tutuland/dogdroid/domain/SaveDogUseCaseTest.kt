package com.tutuland.dogdroid.domain

import com.tutuland.dogdroid.DogInfoRepositoryException
import com.tutuland.dogdroid.DogPreferencesRepositoryException
import com.tutuland.dogdroid.data.info.DogInfoRepository
import com.tutuland.dogdroid.data.preferences.DogPreferencesRepository
import com.tutuland.dogdroid.fixBreed
import com.tutuland.dogdroid.fixDog
import com.tutuland.dogdroid.fixDogInfo
import com.tutuland.dogdroid.fixDogPrefDefault
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest

class SaveDogUseCaseTest {
    @MockK lateinit var infoRepo: DogInfoRepository
    @MockK lateinit var prefRepo: DogPreferencesRepository
    private lateinit var saveDog: SaveDogUseCase

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        saveDog = SaveDogUseCase(infoRepo, prefRepo)
    }

    @Test
    fun `when saveDog called and prefRepo throws, fail`() = runTest {
        coEvery { prefRepo.setPreferencesFor(fixBreed, fixDogPrefDefault) } throws DogPreferencesRepositoryException

        assertFailsWith<DogPreferencesRepositoryException> { saveDog(fixDog) }

        coVerify { prefRepo.setPreferencesFor(fixBreed, fixDogPrefDefault) }
        confirmVerified(prefRepo, infoRepo)
    }

    @Test
    fun `when saveDog called and infoRepo throws, fail`() = runTest {
        coEvery { prefRepo.setPreferencesFor(fixBreed, fixDogPrefDefault) } returns Unit
        coEvery { infoRepo.saveDog(fixDogInfo) } throws DogInfoRepositoryException

        assertFailsWith<DogInfoRepositoryException> { saveDog(fixDog) }

        coVerifyOrder {
            prefRepo.setPreferencesFor(fixBreed, fixDogPrefDefault)
            infoRepo.saveDog(fixDogInfo)
        }
        confirmVerified(prefRepo, infoRepo)
    }

    @Test
    fun `when saveDog called, delegate to infoRepo`() = runTest {
        coEvery { prefRepo.setPreferencesFor(fixBreed, fixDogPrefDefault) } returns Unit
        coEvery { infoRepo.saveDog(fixDogInfo) } returns Unit

        saveDog(fixDog)

        coVerifyOrder {
            prefRepo.setPreferencesFor(fixBreed, fixDogPrefDefault)
            infoRepo.saveDog(fixDogInfo)
        }
        confirmVerified(prefRepo, infoRepo)
    }
}
