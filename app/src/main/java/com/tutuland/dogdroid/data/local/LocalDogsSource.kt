package com.tutuland.dogdroid.data.local

import com.tutuland.dogdroid.data.Dog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LocalDogsSource {
    fun getDogs(): Flow<List<Dog>>
    suspend fun saveDog(dog: Dog)
    suspend fun deleteDogs()

    class FromDatabase(
        private val database: DogDatabase,
    ) : LocalDogsSource {
        override fun getDogs(): Flow<List<Dog>> = database.getDogs()
            .map { processDogEntities(it) }

        override suspend fun saveDog(dog: Dog) = database.saveDogs(
            DogEntity(
                breed = dog.breed,
                imageUrl = dog.imageUrl,
                isFavorite = dog.isFavorite,
            )
        )

        override suspend fun deleteDogs() = database.deleteDogs()

        private fun processDogEntities(list: List<DogEntity>): List<Dog> {
            return list.map { entity ->
                Dog(
                    breed = entity.breed,
                    imageUrl = entity.imageUrl,
                    isFavorite = entity.isFavorite,
                )
            }
        }
    }
}
