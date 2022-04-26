package com.tutuland.dogdroid.domain.service

import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.WorkManager
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verifyOrder
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class RetrieveDogsServiceTest {
    @MockK lateinit var workManager: WorkManager
    @MockK lateinit var operation: Operation
    lateinit var service: RetrieveDogsService

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        service = RetrieveDogsService.FromWorker(workManager)
    }

    @Test
    fun `when requestDogsRemotely called, delegate to workManager to enqueue work`() = runTest {
        every { workManager.enqueueUniqueWork(any(), any(), any<OneTimeWorkRequest>()) } returns operation

        service.requestDogsRemotely()

        verifyOrder {
            workManager.enqueueUniqueWork(any(), any(), any<OneTimeWorkRequest>())
        }
        confirmVerified(workManager, operation)
    }

    @Test
    fun `when cancelRequestingDogs called, delegate to workManager`() = runTest {
        every { workManager.cancelUniqueWork(any()) } returns operation

        service.cancelRequestingDogs()

        verifyOrder {
            workManager.cancelUniqueWork(any())
        }
        confirmVerified(workManager, operation)
    }
}
