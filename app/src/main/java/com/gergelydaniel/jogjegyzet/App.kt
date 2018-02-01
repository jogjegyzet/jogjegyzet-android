package com.gergelydaniel.jogjegyzet

import android.app.Activity
import android.app.Application
import com.gergelydaniel.jogjegyzet.di.AppComponent
import com.gergelydaniel.jogjegyzet.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Activity>

    private lateinit var component: AppComponent

    override fun activityInjector(): AndroidInjector<Activity> = androidInjector

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent
                .builder()
                .application(this)
                .build()

        component.inject(this)
    }
}