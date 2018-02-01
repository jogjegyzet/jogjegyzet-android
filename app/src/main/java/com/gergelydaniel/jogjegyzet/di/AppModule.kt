package com.gergelydaniel.jogjegyzet.di

import android.content.Context
import com.gergelydaniel.app.routing.RouteConfig
import com.gergelydaniel.jogjegyzet.App
import com.gergelydaniel.jogjegyzet.myRouteConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideContext(app: App) : Context = app.applicationContext

    @Provides
    @Singleton
    fun provideRouteConfig() : RouteConfig = myRouteConfig
}
