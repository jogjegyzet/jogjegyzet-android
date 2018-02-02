package com.gergelydaniel.jogjegyzet.di

import com.christianbahl.conductor.ConductorInjectionModule
import com.gergelydaniel.jogjegyzet.App
import com.gergelydaniel.jogjegyzet.di.homecontroller.HomeControllerModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AndroidInjectionModule::class,
    AppModule::class,
    ConductorInjectionModule::class,
    MainActivityModule::class,
    HomeControllerModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}