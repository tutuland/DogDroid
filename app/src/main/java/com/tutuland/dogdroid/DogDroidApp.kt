package com.tutuland.dogdroid

import android.app.Application
import androidx.work.WorkManager
import com.tutuland.dogdroid.data.DogRepository
import com.tutuland.dogdroid.data.service.RetrieveDogsService
import com.tutuland.dogdroid.data.service.RetrieveDogsWorker
import com.tutuland.dogdroid.data.source.local.DogLocalSource
import com.tutuland.dogdroid.data.source.local.DogRoomDatabase
import com.tutuland.dogdroid.data.source.local.makeDogDatabase
import com.tutuland.dogdroid.data.source.remote.DogRemoteSource
import com.tutuland.dogdroid.data.source.remote.makeDogApi
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
    factory { WorkManager.getInstance(get()) }
    factory { makeDogApi() }
    single { makeDogDatabase(get()) }
    factory { get<DogRoomDatabase>().dogDao() } // DogDatabase
    factory<DogLocalSource> { DogLocalSource.FromDatabase(get()) }
    factory<DogRemoteSource> { DogRemoteSource.FromApi(get()) }
    factory<DogRepository> { DogRepository.WithLocalCaching(get(), get(), get()) }
    factory<RetrieveDogsService> { RetrieveDogsService.FromWorker(get()) }
    worker { RetrieveDogsWorker(get(), androidContext(), get()) }
    viewModel { DogListViewModel(get()) }
}
