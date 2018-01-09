package com.laquysoft.bernini.di

import com.laquysoft.bernini.api.PolyAPI
import com.laquysoft.bernini.api.PolyRestAPI
import com.laquysoft.bernini.api.PolyService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by joaobiriba on 09/01/2018.
 */
@Module
class PolyAPIModule {

    @Provides
    @Singleton
    fun providePolyAPI(polyService: PolyService): PolyAPI = PolyRestAPI(polyService)

    @Provides
    @Singleton
    fun providePolyService(retrofit: Retrofit): PolyService = retrofit.create(PolyService::class.java)

}