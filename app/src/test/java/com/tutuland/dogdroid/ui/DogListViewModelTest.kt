package com.tutuland.dogdroid.ui

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import com.tutuland.dogdroid.data.Dog
import com.tutuland.dogdroid.data.DogRepository
import com.tutuland.dogdroid.fixDog
import com.tutuland.dogdroid.fixListOfDogs
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = Application::class)
class DogListViewModelTest {
    @MockK lateinit var repository: DogRepository
    private lateinit var viewModel: DogListViewModel
    private lateinit var currState: DogListViewState

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        viewModel = DogListViewModel(repository)
        currState = initialState()
    }

    /**
     * NOTE:
     * repository.getDogs() is always verified, because fetchDogs() is called on the viewModel init() function.
     * On tests only, fetchDogs() is called a second time so we can verify the whole sequence of states changes
     * after we subscribe to viewModel state.
     * **/

    @Test
    fun `when refreshData is called, delegate it to repository`() = runTest {
        viewModel.refreshData()

        coVerify {
            repository.getDogs()
            repository.refreshData()
        }
        confirmVerified(repository)
    }

    @Test
    fun `when toggleFavorite is called, invert favorite state and save it to repository`() = runTest {
        val invertedDog = fixDog.copy(isFavorite = fixDog.isFavorite.not())

        viewModel.toggleFavorite(fixDog)

        coVerify {
            repository.getDogs()
            repository.saveDog(invertedDog)
        }
        confirmVerified(repository)
    }

    @Test
    fun `when fetchDogs is called, and repository throws, showError`() = runTest {
        coEvery { repository.getDogs() } returns flow { throw Exception() }

        viewModel.state.test {
            viewModel.fetchDogs()
            expect(fetchingStarted())
            expect(errorReceived())
            expectNoEvents()
        }

        coVerify(exactly = 2) { repository.getDogs() }
        confirmVerified(repository)
    }

    @Test
    fun `when fetchDogs is called, and repository emits nothing, display nothing`() = runTest {
        coEvery { repository.getDogs() } returns flowOf()

        viewModel.state.test {
            viewModel.fetchDogs()
            expect(fetchingStarted())
            expectNoEvents()
        }

        coVerify(exactly = 2) { repository.getDogs() }
        confirmVerified(repository)
    }

    @Test
    fun `when fetchDogs is called, and repository emits dogs, display dogs`() = runTest {
        coEvery { repository.getDogs() } returns flowOf(fixListOfDogs)

        viewModel.state.test {
            viewModel.fetchDogs()
            expect(fetchingStarted())
            expect(dogListReceived(fixListOfDogs))
            expectNoEvents()
        }

        coVerify(exactly = 2) { repository.getDogs() }
        confirmVerified(repository)
    }

    private fun initialState() = DogListViewState()
    private fun fetchingStarted() = currState.copy(listOf(), isLoading = true, showError = false)
    private fun errorReceived() = currState.copy(listOf(), isLoading = false, showError = true)
    private fun dogListReceived(list: List<Dog>) = currState.copy(list, isLoading = list.isEmpty(), showError = false)
    private suspend fun FlowTurbine<DogListViewState>.expect(expectedState: DogListViewState) {
        currState = awaitItem()
        assertEquals(expectedState, currState)
    }
}
