package com.danielgergely.jogjegyzet

import android.app.Activity
import android.app.Application
import com.bluelinelabs.conductor.Controller
import com.christianbahl.conductor.HasControllerInjector
import com.danielgergely.jogjegyzet.di.AppComponent
import com.danielgergely.jogjegyzet.di.DaggerAppComponent
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

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

        setupPicasso()
    }

    private fun setupPicasso() {
        val builder = Picasso.Builder(this)

        builder.downloader(OkHttp3Downloader(this, Integer.MAX_VALUE.toLong()))
        val built = builder.build()
        built.setIndicatorsEnabled(true)
        built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)
    }
}