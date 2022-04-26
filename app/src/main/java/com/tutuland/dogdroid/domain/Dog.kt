package com.tutuland.dogdroid.domain

import com.tutuland.dogdroid.data.info.DogInfo
import com.tutuland.dogdroid.data.preferences.DogPreferences

data class Dog(
    val info: DogInfo,
    val preferences: DogPreferences,
)
