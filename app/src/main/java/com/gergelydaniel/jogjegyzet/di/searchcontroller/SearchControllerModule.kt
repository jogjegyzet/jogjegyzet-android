package com.gergelydaniel.jogjegyzet.di.searchcontroller

import com.bluelinelabs.conductor.Controller
import com.christianbahl.conductor.ControllerKey
import com.gergelydaniel.jogjegyzet.ui.search.SearchController
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [SearchControllerComponent::class])
internal abstract class SearchControllerModule {
    @Binds
    @IntoMap
    @ControllerKey(SearchController::class)
    abstract fun bind(builder: SearchControllerComponent.Builder) : AndroidInjector.Factory<out Controller>
}