package com.tutuland.dogdroid

import androidx.datastore.preferences.core.booleanPreferencesKey
import com.tutuland.dogdroid.data.info.DogInfo
import com.tutuland.dogdroid.data.info.local.DogInfoEntity
import com.tutuland.dogdroid.data.info.remote.BreedsResult
import com.tutuland.dogdroid.data.info.remote.ImageResult
import com.tutuland.dogdroid.data.preferences.DogPreferences
import com.tutuland.dogdroid.domain.Dog

const val fixBreed = "akita"
const val fixImageUrl = "https://images.dog.ceo/breeds/akita/Akita_inu_blanc.jpg"

val fixDogInfo = DogInfo(
    breed = fixBreed,
    imageUrl = fixImageUrl,
)

val fixDogEntity = DogInfoEntity(
    breed = fixBreed,
    imageUrl = fixImageUrl,
    modifiedAt = 0,
)

val fixListOfDogInfo = listOf(fixDogInfo, fixDogInfo, fixDogInfo)

val fixListOfDogEntities = listOf(fixDogEntity, fixDogEntity, fixDogEntity)

val fixInvalidBreedsResult = BreedsResult()

val fixBreedsResult = BreedsResult(
    status = "success",
    breeds = listOf(fixBreed, fixBreed, fixBreed)
)

val fixImageResult = ImageResult(
    status = "success",
    message = fixImageUrl
)

val fixDogPrefDefault = DogPreferences(isFavorite = false)

val fixDogPrefFav = fixDogPrefDefault.copy(isFavorite = true)

val fixDog = Dog(fixDogInfo, fixDogPrefDefault)

val fixDogFav = Dog(fixDogInfo, fixDogPrefFav)

val fixListOfDogs = listOf(fixDog, fixDogFav, fixDog)

val fixPrefsKey = booleanPreferencesKey(fixBreed)

object DogInfoRepositoryException : Exception()

object DogPreferencesRepositoryException : Exception()
