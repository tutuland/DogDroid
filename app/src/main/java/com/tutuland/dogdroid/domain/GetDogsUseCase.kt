package com.tutuland.dogdroid.domain

import com.tutuland.dogdroid.data.info.DogInfo
import com.tutuland.dogdroid.data.info.DogInfoRepository
import com.tutuland.dogdroid.data.preferences.DogPreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetDogsUseCase(
    private val infoRepo: DogInfoRepository,
    private val prefRepo: DogPreferencesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    suspend operator fun invoke(): Flow<List<Dog>> =
        withContext(dispatcher) {
            infoRepo.getDogs()
                .map(::withPrefs)
        }

    private suspend fun withPrefs(list: List<DogInfo>): List<Dog> = list.map { info ->
        val preferences = prefRepo.getPreferencesFor(info.breed)
        Dog(info, preferences)
    }
}
