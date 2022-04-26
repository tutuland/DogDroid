package com.tutuland.dogdroid.domain

import com.tutuland.dogdroid.data.info.DogInfoRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RefreshDataUseCaseTest {
    @MockK lateinit var infoRepo: DogInfoRepository
    private lateinit var refreshData: RefreshDataUseCase

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        refreshData = RefreshDataUseCase(infoRepo)
    }

    @Test
    fun `when refreshData called and infoRepo throws, fail`() = runTest {
        coEvery { infoRepo.refreshData() } throws IllegalStateException()

        assertFailsWith<IllegalStateException> { refreshData() }

        coVerify { infoRepo.refreshData() }
        confirmVerified(infoRepo)
    }

    @Test
    fun `when refreshData called, delegate to infoRepo`() = runTest {
        coEvery { infoRepo.refreshData() } returns Unit

        refreshData()

        coVerify { infoRepo.refreshData() }
        confirmVerified(infoRepo)
    }
}
