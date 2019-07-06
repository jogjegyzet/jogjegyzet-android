package com.danielgergely.jogjegyzet.di

import android.content.Context
import com.danielgergely.jogjegyzet.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideContext(app: App) : Context = app.applicationContext
}
