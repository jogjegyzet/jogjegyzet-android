package com.danielgergely.jogjegyzet.di

import com.christianbahl.conductor.ConductorInjectionModule
import com.danielgergely.jogjegyzet.App
import com.danielgergely.jogjegyzet.di.categorycontroller.CategoryControllerModule
import com.danielgergely.jogjegyzet.di.documentcontroller.DocumentControllerModule
import com.danielgergely.jogjegyzet.di.homecontroller.HomeControllerModule
import com.danielgergely.jogjegyzet.di.mainactivity.MainActivityModule
import com.danielgergely.jogjegyzet.di.readercontroller.ReaderControllerModule
import com.danielgergely.jogjegyzet.di.searchcontroller.SearchControllerModule
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
    ReaderControllerModule::class,
    SearchControllerModule::class,
    HomeControllerModule::class,
    ConfigModule::class,
    RetrofitModule::class,
    DbModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}