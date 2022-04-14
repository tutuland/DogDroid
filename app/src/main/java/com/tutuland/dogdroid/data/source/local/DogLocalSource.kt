package com.tutuland.dogdroid.data.source.local

import android.util.Log
import com.tutuland.dogdroid.data.Dog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "DogLocalSource"

interface DogLocalSource {
    fun getDogs(): Flow<List<Dog>>
    suspend fun saveDog(dog: Dog)
    suspend fun deleteDogs()

    class FromDatabase(
        private val database: DogDatabase,
    ) : DogLocalSource {
        override fun getDogs(): Flow<List<Dog>> {
            Log.d(TAG, "Getting dogs from the db")
            return database.getDogs()
                .map(::processDogEntities)
        }

        override suspend fun saveDog(dog: Dog) =
            database.saveDogs(
                DogEntity(
                    breed = dog.breed,
                    imageUrl = dog.imageUrl,
                    isFavorite = dog.isFavorite,
                )
            )

        override suspend fun deleteDogs() {
            Log.d(TAG, "Deleting dogs from the db")
            database.deleteDogs()
        }

        private fun processDogEntities(list: List<DogEntity>): List<Dog> =
            list.map { entity ->
                Dog(
                    breed = entity.breed,
                    imageUrl = entity.imageUrl,
                    isFavorite = entity.isFavorite,
                )
            }
    }
}
