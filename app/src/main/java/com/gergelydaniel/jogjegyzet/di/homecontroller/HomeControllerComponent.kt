package com.gergelydaniel.jogjegyzet.di.homecontroller

import com.gergelydaniel.jogjegyzet.ui.home.HomeController
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface HomeControllerComponent : AndroidInjector<HomeController> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<HomeController>()
}