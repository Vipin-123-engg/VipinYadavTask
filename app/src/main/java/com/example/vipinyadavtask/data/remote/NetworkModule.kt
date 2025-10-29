package com.example.vipinyadavtask.data.remote

import androidx.room.Room
import com.example.vipinyadavtask.base.App
import com.example.vipinyadavtask.data.local.LocalDataSource
import com.example.vipinyadavtask.data.local.db.AppDatabase
import com.example.vipinyadavtask.data.repository.HoldingsRepository
import com.example.vipinyadavtask.data.repository.HoldingsRepositoryImpl
import com.example.vipinyadavtask.domain.usecase.GetPortfolioUseCase
import com.example.vipinyadavtask.utils.APIConstants.BASE_URL
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injector {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: HoldingsApi by lazy { retrofit.create(HoldingsApi::class.java) }
    val remoteDataSource: RemoteDataSource by lazy { RemoteDataSource(api) }

    val database: AppDatabase by lazy {
        Room.databaseBuilder(App.instance, AppDatabase::class.java, "app.db").build()
    }
    val localDataSource: LocalDataSource by lazy { LocalDataSource(database.holdingsDao()) }

    val repository: HoldingsRepository by lazy {
        HoldingsRepositoryImpl(
            remoteDataSource,
            localDataSource
        )
    }

    val useCase: GetPortfolioUseCase by lazy { GetPortfolioUseCase(repository) }
}


