package com.tutuland.dogdroid.ui

import android.app.Application
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.tutuland.dogdroid.R
import com.tutuland.dogdroid.dogDroidModule
import com.tutuland.dogdroid.ui.DogListAdapter.DogHolder
import kotlin.test.Test
import kotlinx.coroutines.MainScope
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

@MediumTest
@RunWith(AndroidJUnit4::class)
class DogListActivityTest : KoinTest {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val intent = Intent(context, DogListActivity::class.java)
    private lateinit var scenario: ActivityScenario<DogListActivity>

    private val scope = MainScope()

    private val testModule = module {
        factory { scope }
        single { fakeRepository }
    }

    @Before
    fun setup() {
        stopKoin()
        initFakeRepository()
        startKoin {
            androidLogger()
            androidContext(context)
            modules(dogDroidModule, testModule)
        }
    }

    @After
    fun cleanup() {
        scenario.close()
    }

    @Test
    fun if_repository_returns_empty_list_loading_should_be_visible() {
        fakeRepositoryMap.clear()
        scenario = ActivityScenario.launch(intent)

        onView(withId(R.id.swipe_refresh))
            .check(matches(isDisplayed()))
    }

    @Test
    fun if_repository_throws_error_state_be_visible() {
        fakeRepositoryReturnsError = true
        scenario = ActivityScenario.launch(intent)

        onView(withId(R.id.error_state))
            .check(matches(isDisplayed()))
    }

    @Test
    fun check_that_first_dog_is_displayed_but_last_is_not() {
        scenario = ActivityScenario.launch(intent)

        onView(withText(firstDogBreed))
            .check(matches(isDisplayed()))

        onView(withText(lastDogBreed))
            .check(doesNotExist())
    }

    @Test
    fun check_that_clicking_a_dog_makes_it_favorite() {
        scenario = ActivityScenario.launch(intent)

        onView(withId(R.id.dog_list))
            .perform(scrollTo<DogHolder>(hasDescendant(withText(lastDogBreed))))
            .check(matches(hasDescendant(withTagValue(equalTo(R.drawable.ic_favorite_false)))))
            .perform(actionOnItem<DogHolder>(hasDescendant(withText(lastDogBreed)), click()))
            .check(matches(hasDescendant(withTagValue(equalTo(R.drawable.ic_favorite_true)))))
    }
}
