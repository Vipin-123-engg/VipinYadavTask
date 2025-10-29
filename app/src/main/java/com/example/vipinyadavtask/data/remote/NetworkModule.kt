package com.example.vipinyadavtask.data.remote

import android.Manifest
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import com.example.vipinyadavtask.App
import androidx.room.Room
import com.example.vipinyadavtask.data.local.db.AppDatabase
import com.example.vipinyadavtask.data.local.db.HoldingEntity
import com.example.vipinyadavtask.data.local.LocalDataSource
import com.example.vipinyadavtask.data.repository.HoldingsRepository
import com.example.vipinyadavtask.data.repository.HoldingsRepositoryImpl
import com.example.vipinyadavtask.domain.usecase.GetPortfolioUseCase
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File

object Injector {
    val json: Json by lazy { Json { ignoreUnknownKeys = true } }

    private val cacheDir by lazy { App.instance.cacheDir }

    private val okHttpCache: Cache by lazy { Cache(File(cacheDir, "http_cache"), 10L * 1024L * 1024L) }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun hasNetwork(): Boolean {
        val cm = App.instance.getSystemService(ConnectivityManager::class.java)
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private val offlineInterceptor = Interceptor { chain ->
        var request = chain.request()
        if (!hasNetwork()) {
            // Serve cached data up to 7 days old when offline
            val maxStale = 60 * 60 * 24 * 7
            request = request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
        chain.proceed(request)
    }

    private val rewriteResponseCacheControl = Interceptor { chain ->
        val originalResponse: Response = chain.proceed(chain.request())
        // Cache responses for 1 minute by default
        val maxAge = 60
        originalResponse.newBuilder()
            .header("Cache-Control", "public, max-age=$maxAge")
            .build()
    }

    val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        OkHttpClient.Builder()
            .cache(okHttpCache)
            .addInterceptor(offlineInterceptor)
            .addNetworkInterceptor(rewriteResponseCacheControl)
            .addInterceptor(logging)
            .build()
    }

    val retrofit: Retrofit by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    val api: HoldingsApi by lazy { retrofit.create(HoldingsApi::class.java) }
    val remoteDataSource: RemoteDataSource by lazy { RemoteDataSource(api) }

    val database: AppDatabase by lazy {
        Room.databaseBuilder(App.instance, AppDatabase::class.java, "app.db").build()
    }
    val localDataSource: LocalDataSource by lazy { LocalDataSource(database.holdingsDao()) }

    val repository: HoldingsRepository by lazy { HoldingsRepositoryImpl(remoteDataSource, localDataSource) }

    val useCase: GetPortfolioUseCase by lazy { GetPortfolioUseCase(repository) }
}


