package com.gergelydaniel.jogjegyzet.di

import com.christianbahl.conductor.ConductorInjectionModule
import com.gergelydaniel.jogjegyzet.App
import com.gergelydaniel.jogjegyzet.di.categorycontroller.CategoryControllerModule
import com.gergelydaniel.jogjegyzet.di.documentcontroller.DocumentControllerModule
import com.gergelydaniel.jogjegyzet.di.mainactivity.MainActivityModule
import com.gergelydaniel.jogjegyzet.di.searchcontroller.SearchControllerModule
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
    CategoryControllerModule::class,
    DocumentControllerModule::class,
    SearchControllerModule::class,
    ConfigModule::class,
    RetrofitModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}