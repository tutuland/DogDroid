package com.tutuland.dogdroid

import android.app.Application
import androidx.work.WorkManager
import com.tutuland.dogdroid.data.DogRepository
import com.tutuland.dogdroid.data.local.DogRoomDatabase
import com.tutuland.dogdroid.data.local.LocalDogsSource
import com.tutuland.dogdroid.data.local.makeDogDatabase
import com.tutuland.dogdroid.data.remote.RemoteDogsSource
import com.tutuland.dogdroid.data.remote.RetrieveDogsWorker
import com.tutuland.dogdroid.data.remote.RetrieveDogsWorkerDelegate
import com.tutuland.dogdroid.data.remote.makeDogApi
import com.tutuland.dogdroid.ui.DogListViewModel
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
    single { makeDogApi() }
    single { makeDogDatabase(get()) }
    single { get<DogRoomDatabase>().dogDao() } // DogDatabase
    single<LocalDogsSource> { LocalDogsSource.FromDatabase(get()) }
    single<RemoteDogsSource> { RemoteDogsSource.FromWorker(get()) }
    single<DogRepository> { DogRepository.WithLocalCaching(get(), get()) }
    single { RetrieveDogsWorkerDelegate(get(), get()) }
    worker { RetrieveDogsWorker(get(), androidContext(), get()) }
    viewModel { DogListViewModel(get()) }
}
