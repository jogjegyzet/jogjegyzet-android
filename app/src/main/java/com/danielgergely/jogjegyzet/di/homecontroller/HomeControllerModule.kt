package com.danielgergely.jogjegyzet.di.homecontroller

import com.bluelinelabs.conductor.Controller
import com.christianbahl.conductor.ControllerKey
import com.danielgergely.jogjegyzet.ui.home.HomeController
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [HomeControllerComponent::class])
internal abstract class HomeControllerModule {
    @Binds
    @IntoMap
    @ControllerKey(HomeController::class)
    abstract fun bind(builder: HomeControllerComponent.Builder) : AndroidInjector.Factory<out Controller>
}