package com.tutuland.dogdroid.data.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tutuland.dogdroid.data.DogRepository

class RetrieveDogsWorker(
    private val repository: DogRepository,
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = try {
        repository.refreshData()
        Result.success()
    } catch (error: Throwable) {
        Result.failure()
    }
}
