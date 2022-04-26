package com.tutuland.dogdroid.data.info.remote

import android.util.Log
import com.tutuland.dogdroid.data.info.DogInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

private const val TAG = "DogRemoteSource"

interface DogInfoRemoteSource {
    fun getDogs(): Flow<DogInfo>

    class FromApi(
        private val api: DogInfoApi,
    ) : DogInfoRemoteSource {
        override fun getDogs(): Flow<DogInfo> = flow {
            Log.d(TAG, "Requesting dogs from the api")
            val listOfBreeds = getBreedsFromApi()
            listOfBreeds
                .asSequence()
                .forEach { breed -> fetchImageAndEmitDog(breed) }
            Log.d(TAG, "Retrieved ${listOfBreeds.size} dogs from  api")
        }

        private suspend fun getBreedsFromApi(): List<String> {
            val breedsResult = api.getBreeds()
            if (breedsResult.isSuccessful.not()) throw BreedsNotFoundException
            return breedsResult.breeds.orEmpty()
        }

        private suspend fun FlowCollector<DogInfo>.fetchImageAndEmitDog(breed: String) {
            val imageResult = api.getImageFor(breed)
            val dog = DogInfo(
                breed = breed,
                imageUrl = imageResult.imageUrl,
            )
            emit(dog)
        }
    }
}

object BreedsNotFoundException : Exception("Dog Api failed to return breeds")
