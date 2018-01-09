package com.laquysoft.bernini.di

import com.laquysoft.bernini.Bernini
import dagger.Component
import javax.inject.Singleton

/**
 * Created by joaobiriba on 09/01/2018.
 */
@Singleton
@Component(modules = arrayOf(NetworkModule::class, PolyAPIModule::class))
interface PolyComponent {
    fun inject(bernini: Bernini)
}