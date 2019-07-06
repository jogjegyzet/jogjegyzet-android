package com.danielgergely.jogjegyzet.di

import com.danielgergely.jogjegyzet.api.RetrofitClient
import com.danielgergely.jogjegyzet.api.gson.LocalDateTimeTypeAdapter
import com.danielgergely.jogjegyzet.api.gson.SearchResultDeserializer
import com.danielgergely.jogjegyzet.domain.SearchResult
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import org.threeten.bp.LocalDateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class RetrofitModule {
    @Provides
    @Singleton
    fun provideGson(): Gson
            = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
            .registerTypeAdapter(SearchResult::class.java, SearchResultDeserializer())
            .create()

    @Provides
    @Singleton
    fun provideRetrofit(@Named(CONFIG_BASE_URL) baseUrl: String, gson: Gson): Retrofit
            = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiClient(retrofit: Retrofit): RetrofitClient
            = retrofit.create(RetrofitClient::class.java)
}