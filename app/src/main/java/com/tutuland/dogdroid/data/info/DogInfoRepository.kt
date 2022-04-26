package com.tutuland.dogdroid.data.info

import com.tutuland.dogdroid.data.info.local.DogInfoLocalSource
import com.tutuland.dogdroid.data.info.remote.DogInfoRemoteSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface DogInfoRepository {
    fun getDogs(): Flow<List<DogInfo>>
    suspend fun saveDog(dog: DogInfo)
    suspend fun refreshData()

    class WithLocalCaching(
        private val localSource: DogInfoLocalSource,
        private val remoteSource: DogInfoRemoteSource,
        private val externalScope: CoroutineScope,
    ) : DogInfoRepository {
        private var refreshJob: Job? = null

        override fun getDogs(): Flow<List<DogInfo>> =
            localSource.getDogs()
                .onEach(::checkIfRefreshIsNeeded)

        override suspend fun saveDog(dog: DogInfo) =
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

        private suspend inline fun checkIfRefreshIsNeeded(list: List<DogInfo>) {
            if (list.isEmpty()) refreshData()
        }
    }
}
