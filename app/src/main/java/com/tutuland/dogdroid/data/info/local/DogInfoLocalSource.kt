package com.tutuland.dogdroid.data.info.local

import android.util.Log
import com.tutuland.dogdroid.data.info.DogInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "DogLocalSource"

interface DogInfoLocalSource {
    fun getDogs(): Flow<List<DogInfo>>
    suspend fun saveDog(dog: DogInfo)
    suspend fun deleteDogs()

    class FromDatabase(
        private val database: DogInfoDatabase,
    ) : DogInfoLocalSource {
        override fun getDogs(): Flow<List<DogInfo>> {
            Log.d(TAG, "Getting dogs from the db")
            return database.getDogs()
                .map(::processDogEntities)
        }

        override suspend fun saveDog(dog: DogInfo) =
            database.saveDogs(
                DogInfoEntity(
                    breed = dog.breed,
                    imageUrl = dog.imageUrl,
                    modifiedAt = System.currentTimeMillis(),
                )
            )

        override suspend fun deleteDogs() {
            Log.d(TAG, "Deleting dogs from the db")
            database.deleteDogs()
        }

        private fun processDogEntities(list: List<DogInfoEntity>): List<DogInfo> =
            list.map { entity ->
                DogInfo(
                    breed = entity.breed,
                    imageUrl = entity.imageUrl,
                )
            }
    }
}
