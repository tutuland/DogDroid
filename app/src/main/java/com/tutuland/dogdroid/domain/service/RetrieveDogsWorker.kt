package com.tutuland.dogdroid.domain.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tutuland.dogdroid.data.info.DogInfoRepository

class RetrieveDogsWorker(
    private val infoRepository: DogInfoRepository,
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = try {
        infoRepository.refreshData()
        Result.success()
    } catch (error: Throwable) {
        Result.failure()
    }
}
