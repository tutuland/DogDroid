package com.tutuland.dogdroid.data.remote

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tutuland.dogdroid.data.local.DogDatabase
import com.tutuland.dogdroid.data.local.DogEntity

private const val TAG = "RetrieveDogsWorker"

class RetrieveDogsWorker(
    private val delegate: RetrieveDogsWorkerDelegate,
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = try {
        delegate.doWork()
        Result.success()
    } catch (error: Throwable) {
        Result.failure()
    }
}

class RetrieveDogsWorkerDelegate(
    private val api: DogApi,
    private val database: DogDatabase,
) {
    suspend fun doWork() {
        Log.d(TAG, "Requesting dogs from the api")
        val listOfBreeds = getBreedsFromApi().breeds.orEmpty()
        listOfBreeds
            .asSequence()
            .forEach { createAndSaveDogEntityFor(it) }
        Log.d(TAG, "Saved ${listOfBreeds.size} dogs to the database")
    }

    private suspend fun getBreedsFromApi(): BreedsResult {
        val breedsResult = api.getBreeds()
        if (breedsResult.isSuccessful.not()) throw BreedsNotFoundException
        return breedsResult
    }

    private suspend fun createAndSaveDogEntityFor(breed: String) {
        val dogEntity = fetchImageAndMapToDogEntity(breed)
        database.saveDogs(dogEntity)
    }

    private suspend fun fetchImageAndMapToDogEntity(breed: String): DogEntity {
        val imageResult = api.getImageFor(breed)
        return DogEntity(
            breed = breed,
            imageUrl = imageResult.imageUrl,
            isFavorite = false,
        )
    }
}

object BreedsNotFoundException : Exception("Dog Api failed to return breeds")
