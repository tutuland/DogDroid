package com.tutuland.dogdroid.data.source.remote

import android.util.Log
import com.tutuland.dogdroid.data.Dog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

private const val TAG = "DogRemoteSource"

interface DogRemoteSource {
    fun getDogs(): Flow<Dog>

    class FromApi(
        private val api: DogApi,
    ) : DogRemoteSource {
        override fun getDogs(): Flow<Dog> = flow {
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

        private suspend fun FlowCollector<Dog>.fetchImageAndEmitDog(breed: String) {
            val imageResult = api.getImageFor(breed)
            val dog = Dog(
                breed = breed,
                imageUrl = imageResult.imageUrl,
                isFavorite = false,
            )
            emit(dog)
        }
    }
}

object BreedsNotFoundException : Exception("Dog Api failed to return breeds")
