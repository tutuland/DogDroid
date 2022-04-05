package com.tutuland.dogdroid.data

import com.tutuland.dogdroid.data.local.LocalDogsSource
import com.tutuland.dogdroid.data.remote.RemoteDogsSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

interface DogRepository {
    fun getDogs(): Flow<List<Dog>>
    suspend fun saveDog(dog: Dog)
    suspend fun refreshData()

    class WithLocalCaching(
        private val localSource: LocalDogsSource,
        private val remoteSource: RemoteDogsSource,
    ) : DogRepository {

        override fun getDogs(): Flow<List<Dog>> =
            localSource
                .getDogs()
                .onEach { requestFromApiIfNeeded(it) }

        override suspend fun saveDog(dog: Dog) {
            localSource.saveDog(dog)
        }

        override suspend fun refreshData() {
            remoteSource.cancelRequestingDogs()
            localSource.deleteDogs()
        }

        private fun requestFromApiIfNeeded(list: List<Dog>) {
            if (list.isEmpty()) remoteSource.requestDogsRemotely()
        }
    }
}

data class Dog(
    val breed: String,
    val imageUrl: String,
    val isFavorite: Boolean,
)
