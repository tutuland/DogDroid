package com.tutuland.dogdroid.data.source.remote

import app.cash.turbine.test
import com.tutuland.dogdroid.fixBreed
import com.tutuland.dogdroid.fixBreedsResult
import com.tutuland.dogdroid.fixDog
import com.tutuland.dogdroid.fixImageResult
import com.tutuland.dogdroid.fixInvalidBreedsResult
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class DogRemoteSourceTest {
    @MockK lateinit var api: DogApi
    lateinit var source: DogRemoteSource

    private object GetBreedsException : Exception()
    private object GetImageException : Exception()

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        source = DogRemoteSource.FromApi(api)
    }

    @Test
    fun `when getBreeds called and api throws, fail`() = runTest {
        coEvery { api.getBreeds() } throws GetBreedsException

        source.getDogs().test {
            assertEquals(GetBreedsException, awaitError())
            expectNoEvents()
        }

        coVerifyOrder { api.getBreeds() }
        confirmVerified(api)
    }

    @Test
    fun `when getBreeds called and api returns no breeds, fail with BreedsNotFoundException`() = runTest {
        coEvery { api.getBreeds() } returns fixInvalidBreedsResult

        source.getDogs().test {
            assertEquals(BreedsNotFoundException, awaitError())
            expectNoEvents()
        }

        coVerify { api.getBreeds() }
        confirmVerified(api)
    }

    @Test
    fun `when getImageFor called and api throws, fail`() = runTest {
        coEvery { api.getBreeds() } returns fixBreedsResult
        coEvery { api.getImageFor(fixBreed) } throws GetImageException

        source.getDogs().test {
            assertEquals(GetImageException, awaitError())
            expectNoEvents()
        }

        coVerify { api.getBreeds() }
        coVerify(exactly = 1) { api.getImageFor(fixBreed) }
        confirmVerified(api)
    }

    @Test
    fun `when all requests return results, emmit dog list`() = runTest {
        coEvery { api.getBreeds() } returns fixBreedsResult
        coEvery { api.getImageFor(fixBreed) } returns fixImageResult

        source.getDogs().test {
            assertEquals(fixDog, awaitItem())
            assertEquals(fixDog, awaitItem())
            assertEquals(fixDog, awaitItem())
            awaitComplete()
        }

        coVerify { api.getBreeds() }
        coVerify(exactly = 3) { api.getImageFor(fixBreed) }
        confirmVerified(api)
    }
}
