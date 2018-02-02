package com.gergelydaniel.jogjegyzet

import android.app.Activity
import android.app.Application
import com.christianbahl.conductor.HasControllerInjector
import com.gergelydaniel.jogjegyzet.di.AppComponent
import com.gergelydaniel.jogjegyzet.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject
import com.bluelinelabs.conductor.Controller



class App : Application(), HasActivityInjector, HasControllerInjector {


    @Inject
    internal lateinit var androidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    internal lateinit var controllerInjector: DispatchingAndroidInjector<Controller>

    private lateinit var component: AppComponent

    override fun activityInjector() = androidInjector
    override fun controllerInjector() = controllerInjector

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent
                .builder()
                .application(this)
                .build()

        component.inject(this)
    }
}