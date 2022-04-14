package com.tutuland.dogdroid.data.source.remote

import com.google.gson.annotations.SerializedName
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

fun makeDogApi(baseUrl: HttpUrl = defaultUrl): DogApi = Retrofit.Builder()
    .baseUrl(baseUrl)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(DogApi::class.java)

interface DogApi {
    @GET("breeds/list")
    suspend fun getBreeds(): BreedsResult

    @GET("breed/{name}/images/random")
    suspend fun getImageFor(@Path("name") name: String): ImageResult
}

data class BreedsResult(
    @SerializedName("status") private val status: String? = null,
    @SerializedName("message") val breeds: List<String>? = null,
) {
    val isSuccessful: Boolean get() = "success" == status && breeds.isNullOrEmpty().not()
}

data class ImageResult(
    @SerializedName("status") private val status: String? = null,
    @SerializedName("message") private val message: String? = null,
) {
    val imageUrl: String get() = if ("success" == status && message.isNullOrBlank().not()) message.orEmpty() else ""
}

private val defaultUrl = "https://dog.ceo/api/".toHttpUrl()
