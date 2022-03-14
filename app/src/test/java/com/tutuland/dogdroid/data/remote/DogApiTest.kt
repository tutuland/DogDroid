package com.tutuland.dogdroid.data.remote

import com.tutuland.dogdroid.data.fixBreed
import com.tutuland.dogdroid.data.fixImageUrl
import io.mockk.MockKAnnotations
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.HttpException

@ExperimentalCoroutinesApi
class DogApiTest {
    private lateinit var server: MockWebServer
    private lateinit var api: DogApi

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        server = MockWebServer()
        api = makeDogApi(server.url("/"))
    }

    @AfterTest
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `when getBreeds returns not found, throw HttpException`() = runTest {
        apiResponds(404, "resource_not_found.json")
        assertFailsWith<HttpException> { api.getBreeds() }
    }

    @Test
    fun `when getBreeds succeed, return a BreedsResponse`() = runTest {
        apiResponds(200, "get_breeds_success.json")
        val result = api.getBreeds()
        assertTrue { result.isSuccessful }
        assertTrue { result.breeds.orEmpty().size == 95 }
    }

    @Test
    fun `when getImageFor returns not found, throw HttpException`() = runTest {
        apiResponds(404, "resource_not_found.json")
        assertFailsWith<HttpException> { api.getImageFor(fixBreed) }
    }

    @Test
    fun `when getImageFor succeed, return a BreedsResponse`() = runTest {
        apiResponds(200, "get_image_for_success.json")
        val result = api.getImageFor(fixBreed)
        assertEquals(fixImageUrl, result.imageUrl)
    }

    private fun apiResponds(code: Int, response: String) = server.enqueue(
        MockResponse()
            .setResponseCode(code)
            .setBody(readJson(response))
    )

    private fun readJson(
        file: String
    ) = (this::class.java.classLoader ?: throw IllegalStateException("Failed to get the classLoader"))
        .getResourceAsStream(file)
        .bufferedReader()
        .use { it.readText() }
}
