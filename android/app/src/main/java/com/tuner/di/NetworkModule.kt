package com.tuner.di

import com.tuner.api.RadioApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://de1.api.radio-browser.info/json/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRadioApi(retrofit: Retrofit): RadioApi {
        return retrofit.create(RadioApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGitHubApi(): com.tuner.api.GitHubApi {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(com.tuner.api.GitHubApi::class.java)
    }
}
