package com.tutuland.dogdroid

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.work.WorkManager
import com.tutuland.dogdroid.data.info.DogInfoRepository
import com.tutuland.dogdroid.data.info.local.DogInfoLocalSource
import com.tutuland.dogdroid.data.info.local.DogRoomDatabase
import com.tutuland.dogdroid.data.info.local.makeDogInfoDatabase
import com.tutuland.dogdroid.data.info.remote.DogInfoRemoteSource
import com.tutuland.dogdroid.data.info.remote.makeDogInfoApi
import com.tutuland.dogdroid.data.preferences.DogPreferencesRepository
import com.tutuland.dogdroid.domain.GetDogsUseCase
import com.tutuland.dogdroid.domain.RefreshDataUseCase
import com.tutuland.dogdroid.domain.SaveDogUseCase
import com.tutuland.dogdroid.domain.service.RetrieveDogsService
import com.tutuland.dogdroid.domain.service.RetrieveDogsWorker
import com.tutuland.dogdroid.ui.model.DogListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module

class DogDroidApp : Application() {
    private lateinit var scope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        scope = MainScope()
        startKoin {
            androidLogger()
            androidContext(this@DogDroidApp)
            workManagerFactory()
            modules(dogDroidModule, appModule)
        }
    }

    private val appModule = module {
        factory { scope }
    }
}

val dogDroidModule = module {
    /* data layer - info */
    factory { makeDogInfoApi() }
    single { makeDogInfoDatabase(get()) }
    factory { get<DogRoomDatabase>().dogDao() } // DogDatabase
    factory<DogInfoLocalSource> { DogInfoLocalSource.FromDatabase(get()) }
    factory<DogInfoRemoteSource> { DogInfoRemoteSource.FromApi(get()) }
    factory<DogInfoRepository> { DogInfoRepository.WithLocalCaching(get(), get(), get()) }

    /* data layer - preferences */
    single { PreferenceDataStoreFactory.create { get<Context>().preferencesDataStoreFile("DogPreferences") } }
    factory<DogPreferencesRepository> { DogPreferencesRepository.FromStorage(get()) }

    /* domain layer */
    factory { GetDogsUseCase(get(), get()) }
    factory { SaveDogUseCase(get(), get()) }
    factory { RefreshDataUseCase(get()) }

    /* domain layer - service */
    factory { WorkManager.getInstance(get()) }
    factory<RetrieveDogsService> { RetrieveDogsService.FromWorker(get()) }
    worker { RetrieveDogsWorker(get(), androidContext(), get()) }

    /* ui layer */
    viewModel { DogListViewModel(get(), get(), get()) }
}
