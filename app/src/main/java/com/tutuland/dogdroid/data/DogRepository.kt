package com.tutuland.dogdroid.data

import com.tutuland.dogdroid.data.source.local.DogLocalSource
import com.tutuland.dogdroid.data.source.remote.DogRemoteSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface DogRepository {
    fun getDogs(): Flow<List<Dog>>
    suspend fun saveDog(dog: Dog)
    suspend fun refreshData()

    class WithLocalCaching(
        private val localSource: DogLocalSource,
        private val remoteSource: DogRemoteSource,
        private val externalScope: CoroutineScope,
    ) : DogRepository {
        private var refreshJob: Job? = null

        override fun getDogs(): Flow<List<Dog>> =
            localSource.getDogs()
                .onEach(::checkIfRefreshIsNeeded)

        override suspend fun saveDog(dog: Dog) =
            localSource.saveDog(dog)

        override suspend fun refreshData() {
            refreshJob?.cancel()
            refreshJob = externalScope.launch {
                localSource.deleteDogs()
                fetchDogsAndSaveThem()
            }
        }

        private suspend inline fun fetchDogsAndSaveThem() =
            remoteSource.getDogs()
                .collect(::saveDog)

        private suspend inline fun checkIfRefreshIsNeeded(list: List<Dog>) {
            if (list.isEmpty()) refreshData()
        }
    }
}
