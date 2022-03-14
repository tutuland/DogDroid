package com.tutuland.dogdroid.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutuland.dogdroid.data.Dog
import com.tutuland.dogdroid.data.DogRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

private const val TAG = "DogListViewModel"

class DogListViewModel(
    private val repo: DogRepository
) : ViewModel() {
    private var fetchJob: Job? = null
    private val state = MutableStateFlow(DogListViewState())
    val loadingState: Boolean get() = state.value.isLoading

    fun flowState(): StateFlow<DogListViewState> {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            repo.getDogs()
                .onStart { fetchingStarted() }
                .catch { errorReceived(it) }
                .collect { dogListReceived(it) }
        }
        return state.asStateFlow()
    }

    fun refreshData() {
        Log.d(TAG, "refreshData")
        viewModelScope.launch { repo.refreshData() }
    }

    fun toggleFavorite(dog: Dog) {
        val toggledDog = dog.copy(isFavorite = dog.isFavorite.not())
        Log.d(TAG, "Set favorite on ${toggledDog.breed} to ${toggledDog.isFavorite}")
        viewModelScope.launch { repo.saveDog(toggledDog) }
    }

    private fun fetchingStarted() {
        Log.d(TAG, "fetchingStarted")
        state.value = state.value.copy(dogList = listOf(), isLoading = true, showError = false)
    }

    private fun errorReceived(error: Throwable) {
        Log.d(TAG, "errorReceived: $error")
        state.value = state.value.copy(dogList = listOf(), isLoading = false, showError = true)
    }

    private fun dogListReceived(dogList: List<Dog>) {
        state.value = state.value.copy(dogList = dogList, isLoading = dogList.isEmpty(), showError = false)
    }
}

data class DogListViewState(
    var dogList: List<Dog> = listOf(),
    val isLoading: Boolean = false,
    val showError: Boolean = false,
)
