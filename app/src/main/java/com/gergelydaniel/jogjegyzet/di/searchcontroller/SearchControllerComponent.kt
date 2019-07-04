package com.gergelydaniel.jogjegyzet.di.searchcontroller

import com.gergelydaniel.jogjegyzet.ui.search.SearchController
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface SearchControllerComponent: AndroidInjector<SearchController> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SearchController>()
}