package com.tutuland.dogdroid.data.remote

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
class RemoteDogsSourceTest {
    @MockK lateinit var workManager: WorkManager
    @MockK lateinit var operation: Operation
    lateinit var source: RemoteDogsSource

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        source = RemoteDogsSource.FromWorker(workManager)
    }

    @Test
    fun `when requestDogsRemotely called, delegate to workManager to enqueue work`() = runTest {
        every { workManager.enqueueUniqueWork(any(), any(), any<OneTimeWorkRequest>()) } returns operation

        source.requestDogsRemotely()

        verifyOrder {
            workManager.enqueueUniqueWork(any(), any(), any<OneTimeWorkRequest>())
        }
        confirmVerified(workManager, operation)
    }

    @Test
    fun `when cancelRequestingDogs called, delegate to workManager`() = runTest {
        every { workManager.cancelUniqueWork(any()) } returns operation

        source.cancelRequestingDogs()

        verifyOrder {
            workManager.cancelUniqueWork(any())
        }
        confirmVerified(workManager, operation)
    }
}
