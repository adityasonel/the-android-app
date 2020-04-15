package prj.adityasnl.theandroidapp

import android.app.Application
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import prj.adityasnl.theandroidapp.di.networkModule
import prj.adityasnl.theandroidapp.di.viewModelModule

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class TheAndroidApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            modules(networkModule, viewModelModule)
        }
    }
}