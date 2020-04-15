package prj.adityasnl.theandroidapp.di

import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import prj.adityasnl.theandroidapp.api.ApiService
import prj.adityasnl.theandroidapp.repository.Repository
import prj.adityasnl.theandroidapp.utils.Utils.Companion.isNetworkAvailable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@ExperimentalCoroutinesApi
val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient(androidContext()))
            .build()
            .create(ApiService::class.java)
    }

    single {
        Repository(get())
    }
}

fun getOkHttpClient(context: Context): OkHttpClient {
    val cacheSize = (10 * 1024 * 1024).toLong()
    val myCache = Cache(context.cacheDir, cacheSize)

    return OkHttpClient.Builder()
        .cache(myCache)
        .addInterceptor { chain ->
            var request = chain.request()
            request = if (isNetworkAvailable(context)!!)
                request.newBuilder().header(
                    "Cache-Control",
                    "public, max-age=" + 5
                ).build()
            else
                request.newBuilder().header(
                    "Cache-Control",
                    "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                ).build()
            chain.proceed(request)
        }
        .build()
}