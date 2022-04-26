package com.tutuland.dogdroid.domain.service

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

private const val FETCH_DOGS_TASK = "FetchDogsTask"

interface RetrieveDogsService {
    fun requestDogsRemotely()
    fun cancelRequestingDogs()

    class FromWorker(
        private val workManager: WorkManager
    ) : RetrieveDogsService {
        override fun requestDogsRemotely() {
            val fetchDogsRequest = OneTimeWorkRequestBuilder<RetrieveDogsWorker>()
            workManager.enqueueUniqueWork(
                FETCH_DOGS_TASK,
                ExistingWorkPolicy.REPLACE,
                fetchDogsRequest.build()
            )
        }

        override fun cancelRequestingDogs() {
            workManager.cancelUniqueWork(FETCH_DOGS_TASK)
        }
    }
}
