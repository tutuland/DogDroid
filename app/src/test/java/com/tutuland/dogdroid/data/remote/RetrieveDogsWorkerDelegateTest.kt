package com.tutuland.dogdroid.data.remote

import com.tutuland.dogdroid.data.local.DogDatabase
import com.tutuland.dogdroid.fixBreed
import com.tutuland.dogdroid.fixBreedsResult
import com.tutuland.dogdroid.fixDogEntity
import com.tutuland.dogdroid.fixImageResult
import com.tutuland.dogdroid.fixInvalidBreedsResult
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest

class RetrieveDogsWorkerDelegateTest {
    @MockK lateinit var api: DogApi
    @MockK lateinit var database: DogDatabase
    private lateinit var delegate: RetrieveDogsWorkerDelegate

    object GetBreedsException : Exception()
    object GetImageException : Exception()

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        delegate = RetrieveDogsWorkerDelegate(api, database)
    }

    @Test
    fun `when getBreeds called and api throws, fail`() = runTest {
        coEvery { api.getBreeds() } throws GetBreedsException

        assertFailsWith<GetBreedsException> { delegate.doWork() }

        coVerify { api.getBreeds() }
        confirmVerified(api, database)
    }

    @Test
    fun `when getBreeds called and api returns no breeds, fail with BreedsNotFoundException`() = runTest {
        coEvery { api.getBreeds() } returns fixInvalidBreedsResult

        assertFailsWith<BreedsNotFoundException> { delegate.doWork() }

        coVerify { api.getBreeds() }
        confirmVerified(api, database)
    }

    @Test
    fun `when getImageFor called and api throws, fail`() = runTest {
        coEvery { api.getBreeds() } returns fixBreedsResult
        coEvery { api.getImageFor(fixBreed) } throws GetImageException

        assertFailsWith<GetImageException> { delegate.doWork() }

        coVerify { api.getBreeds() }
        coVerify(exactly = 1) { api.getImageFor(fixBreed) }
        confirmVerified(api, database)
    }

    @Test
    fun `when both images return results, store them on the database`() = runTest {
        coEvery { api.getBreeds() } returns fixBreedsResult
        coEvery { api.getImageFor(fixBreed) } returns fixImageResult

        delegate.doWork()

        coVerify { api.getBreeds() }
        coVerify(exactly = 3) { api.getImageFor(fixBreed) }
        coVerify(exactly = 3) { database.saveDogs(fixDogEntity) }
        confirmVerified(api, database)
    }
}
