package com.danielgergely.jogjegyzet.di.homecontroller

import com.danielgergely.jogjegyzet.ui.home.HomeController
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface HomeControllerComponent : AndroidInjector<HomeController> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<HomeController>()
}