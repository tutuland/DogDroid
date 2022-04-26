package com.tutuland.dogdroid.ui.model

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutuland.dogdroid.domain.Dog
import com.tutuland.dogdroid.domain.GetDogsUseCase
import com.tutuland.dogdroid.domain.RefreshDataUseCase
import com.tutuland.dogdroid.domain.SaveDogUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

private const val TAG = "DogListViewModel"

class DogListViewModel(
    private val getDogs: GetDogsUseCase,
    private val saveDog: SaveDogUseCase,
    private val refresh: RefreshDataUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(DogListViewState())
    val state: StateFlow<DogListViewState> get() = _state.asStateFlow()
    val loadingState: Boolean get() = _state.value.isLoading

    init {
        viewModelScope.launch { fetchDogs() }
    }

    fun refreshData() {
        Log.d(TAG, "refreshData")
        viewModelScope.launch { refresh() }
    }

    fun toggleFavorite(dog: Dog) {
        val toggledPrefs = dog.preferences.copy(isFavorite = dog.preferences.isFavorite.not())
        val toggledDog = dog.copy(preferences = toggledPrefs)
        Log.d(TAG, "Set favorite on ${toggledDog.info.breed} to ${toggledPrefs.isFavorite}")
        viewModelScope.launch { saveDog(toggledDog) }
    }

    @VisibleForTesting
    suspend fun fetchDogs() {
        getDogs()
            .onStart { fetchingStarted() }
            .catch { errorReceived(it) }
            .collect { dogListReceived(it) }
    }

    private fun fetchingStarted() {
        Log.d(TAG, "fetchingStarted")
        _state.value = _state.value.copy(dogList = listOf(), isLoading = true, showError = false)
    }

    private fun errorReceived(error: Throwable) {
        Log.d(TAG, "errorReceived: $error")
        _state.value = _state.value.copy(dogList = listOf(), isLoading = false, showError = true)
    }

    private fun dogListReceived(dogList: List<Dog>) {
        _state.value = _state.value.copy(dogList = dogList, isLoading = dogList.isEmpty(), showError = false)
    }
}
