package com.tutuland.dogdroid.data

import com.tutuland.dogdroid.data.local.LocalDogsSource
import com.tutuland.dogdroid.data.remote.RemoteDogsSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

interface DogRepository {
    fun getDogs(): StateFlow<List<Dog>>
    suspend fun saveDog(dog: Dog)
    suspend fun refreshData()

    class WithLocalCaching(
        private val scope: CoroutineScope,
        private val localSource: LocalDogsSource,
        private val remoteSource: RemoteDogsSource,
        private val initialList: List<Dog> = listOf(),
    ) : DogRepository {

        override fun getDogs(): StateFlow<List<Dog>> =
            localSource
                .getDogs()
                .onEach { requestFromApiIfNeeded(it) }
                .stateIn(scope, SharingStarted.Lazily, initialList)

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
