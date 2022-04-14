package com.tutuland.dogdroid.ui.model

import com.tutuland.dogdroid.data.Dog

data class DogListViewState(
    val dogList: List<Dog> = listOf(),
    val isLoading: Boolean = false,
    val showError: Boolean = false,
)
