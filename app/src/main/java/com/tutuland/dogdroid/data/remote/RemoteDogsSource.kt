package com.tutuland.dogdroid.data.remote

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager


private const val FETCH_DOGS_TASK = "FetchDogsTask"

interface RemoteDogsSource {
    fun requestDogsRemotely()
    fun cancelRequestingDogs()

    class FromWorker(
        private val workManager: WorkManager
    ) : RemoteDogsSource {
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
