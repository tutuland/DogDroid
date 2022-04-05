package com.tutuland.dogdroid

import com.tutuland.dogdroid.data.Dog
import com.tutuland.dogdroid.data.local.DogEntity

const val fixBreed = "akita"
const val fixImageUrl = "https://images.dog.ceo/breeds/akita/Akita_inu_blanc.jpg"

val fixDog = Dog(
    breed = fixBreed,
    imageUrl = fixImageUrl,
    isFavorite = false,
)

val fixDogEntity = DogEntity(
    breed = fixBreed,
    imageUrl = fixImageUrl,
    isFavorite = false,
)

val fixListOfDogs = listOf(fixDog, fixDog, fixDog)

val fixListOfDogEntities = listOf(fixDogEntity, fixDogEntity, fixDogEntity)
