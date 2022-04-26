package com.tutuland.dogdroid.domain

import com.tutuland.dogdroid.data.info.DogInfoRepository
import com.tutuland.dogdroid.data.preferences.DogPreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveDogUseCase(
    private val infoRepo: DogInfoRepository,
    private val prefRepo: DogPreferencesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    suspend operator fun invoke(dog: Dog) =
        withContext(dispatcher) {
            prefRepo.setPreferencesFor(dog.info.breed, dog.preferences)
            infoRepo.saveDog(dog.info)
        }
}
