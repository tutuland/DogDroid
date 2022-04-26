package com.tutuland.dogdroid.ui

import com.tutuland.dogdroid.data.info.DogInfo
import com.tutuland.dogdroid.data.info.DogInfoRepository
import com.tutuland.dogdroid.data.preferences.DogPreferences
import com.tutuland.dogdroid.data.preferences.DogPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

var fakeReposScope: CoroutineScope? = null
var fakeInfoRepositoryReturnsError = false
val fakeInfoRepositoryMap = mutableMapOf<String, DogInfo>()
val fakePrefsRepositoryMap = mutableMapOf<String, DogPreferences>()

fun initFakeRepositories(scope: CoroutineScope) {
    fakeReposScope = scope
    fakeInfoRepositoryReturnsError = false
    val info = listOf(
        "akita" to DogInfo(
            breed = "akita",
            imageUrl = "https://images.dog.ceo/breeds/akita/Akita_inu_blanc.jpg",
        ),
        "corgi" to DogInfo(
            breed = "corgi",
            imageUrl = "https://images.dog.ceo/breeds/corgi-cardigan/n02113186_10505.jpg",
        ),
        "dalmatian" to DogInfo(
            breed = "dalmatian",
            imageUrl = "https://images.dog.ceo/breeds/dalmatian/cooper1.jpg",
        ),
        "doberman" to DogInfo(
            breed = "doberman",
            imageUrl = "https://images.dog.ceo/breeds/doberman/n02107142_10070.jpg",
        ),
        "germanshepherd" to DogInfo(
            breed = "labrador",
            imageUrl = "https://images.dog.ceo/breeds/germanshepherd/Bagira_site.jpg",
        ),
        "husky" to DogInfo(
            breed = "husky",
            imageUrl = "https://images.dog.ceo/breeds/husky/n02110185_10047.jpg",
        ),
        "labrador" to DogInfo(
            breed = "labrador",
            imageUrl = "https://images.dog.ceo/breeds/labrador/IMG_4709.jpg",
        ),
        "poodle" to DogInfo(
            breed = "poodle",
            imageUrl = "https://images.dog.ceo/breeds/poodle-miniature/n02113712_10433.jpghttps://images.dog.ceo/breeds/doberman/n02107142_10070.jpg",
        ),
        "pug" to DogInfo(
            breed = "pug",
            imageUrl = "https://images.dog.ceo/breeds/pug/IMG_8459.jpeg",
        ),
    )
    fakeInfoRepositoryMap.clear()
    fakeInfoRepositoryMap.putAll(info)

    val prefs = fakeInfoRepositoryMap.map { it.key to DogPreferences(isFavorite = false) }
    fakePrefsRepositoryMap.clear()
    fakePrefsRepositoryMap.putAll(prefs)
}

const val firstDogBreed = "akita"
const val lastDogBreed = "pug"

object FakeInfoRepositoryException : Exception("FakeInfoRepositoryException")

val fakeInfoRepository = object : DogInfoRepository {
    val dogsFlow = MutableSharedFlow<List<DogInfo>>(replay = 1)

    override fun getDogs(): Flow<List<DogInfo>> {
        if (fakeInfoRepositoryReturnsError) return flow { throw FakeInfoRepositoryException }
        fakeReposScope?.launch { dogsFlow.emit(fakeInfoRepositoryMap.values.toList()) }
        return dogsFlow
    }

    override suspend fun saveDog(dog: DogInfo) {
        fakeInfoRepositoryMap[dog.breed] = dog
        dogsFlow.emit(fakeInfoRepositoryMap.values.toList())
    }

    override suspend fun refreshData() {
        fakeInfoRepositoryMap.clear()
        dogsFlow.emit(emptyList())
    }
}

val fakePrefsRepository = object : DogPreferencesRepository {
    override suspend fun getPreferencesFor(breed: String): DogPreferences {
        val pref = fakePrefsRepositoryMap[breed] ?: DogPreferences(isFavorite = false)
        fakePrefsRepositoryMap[breed] = pref
        return pref
    }

    override suspend fun setPreferencesFor(breed: String, preferences: DogPreferences) {
        fakePrefsRepositoryMap[breed] = preferences
    }
}
