package com.tutuland.dogdroid.domain

import com.tutuland.dogdroid.data.info.DogInfoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RefreshDataUseCase(
    private val infoRepo: DogInfoRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    suspend operator fun invoke() =
        withContext(dispatcher) {
            infoRepo.refreshData()
        }
}
