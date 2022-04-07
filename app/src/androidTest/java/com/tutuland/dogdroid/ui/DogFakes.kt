package com.tutuland.dogdroid.ui

import com.tutuland.dogdroid.data.Dog
import com.tutuland.dogdroid.data.DogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

var fakeRepositoryReturnsError = false
var fakeRepositoryMap = mutableMapOf<String, Dog>()

fun initFakeRepository() {
    fakeRepositoryReturnsError = false
    fakeRepositoryMap = mutableMapOf(
        "akita" to Dog(
            breed = "akita",
            imageUrl = "https://images.dog.ceo/breeds/akita/Akita_inu_blanc.jpg",
            isFavorite = false,
        ),
        "corgi" to Dog(
            breed = "corgi",
            imageUrl = "https://images.dog.ceo/breeds/corgi-cardigan/n02113186_10505.jpg",
            isFavorite = false,
        ),
        "dalmatian" to Dog(
            breed = "dalmatian",
            imageUrl = "https://images.dog.ceo/breeds/dalmatian/cooper1.jpg",
            isFavorite = false,
        ),
        "doberman" to Dog(
            breed = "doberman",
            imageUrl = "https://images.dog.ceo/breeds/doberman/n02107142_10070.jpg",
            isFavorite = false,
        ),
        "germanshepherd" to Dog(
            breed = "labrador",
            imageUrl = "https://images.dog.ceo/breeds/germanshepherd/Bagira_site.jpg",
            isFavorite = false,
        ),
        "husky" to Dog(
            breed = "husky",
            imageUrl = "https://images.dog.ceo/breeds/husky/n02110185_10047.jpg",
            isFavorite = false,
        ),
        "labrador" to Dog(
            breed = "labrador",
            imageUrl = "https://images.dog.ceo/breeds/labrador/IMG_4709.jpg",
            isFavorite = false,
        ),
        "poodle" to Dog(
            breed = "poodle",
            imageUrl = "https://images.dog.ceo/breeds/poodle-miniature/n02113712_10433.jpghttps://images.dog.ceo/breeds/doberman/n02107142_10070.jpg",
            isFavorite = false,
        ),
        "pug" to Dog(
            breed = "pug",
            imageUrl = "https://images.dog.ceo/breeds/pug/IMG_8459.jpeg",
            isFavorite = false,
        ),
    )
}

const val firstDogBreed = "akita"
const val lastDogBreed = "pug"

object FakeRepositoryException : Exception("FakeRepositoryException")

val fakeRepository = object : DogRepository {
    val state = MutableStateFlow(fakeRepositoryMap.values.toList())

    override fun getDogs(): Flow<List<Dog>> {
        if (fakeRepositoryReturnsError) return flow { throw FakeRepositoryException }
        state.value = fakeRepositoryMap.values.toList()
        return state
    }

    override suspend fun saveDog(dog: Dog) {
        fakeRepositoryMap[dog.breed] = dog
        state.value = fakeRepositoryMap.values.toList()
    }

    override suspend fun refreshData() {
        fakeRepositoryMap.clear()
        state.value = emptyList()
    }
}
