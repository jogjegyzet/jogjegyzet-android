package com.gergelydaniel.jogjegyzet.di.categorycontroller

import com.gergelydaniel.jogjegyzet.ui.category.CategoryController
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface CategoryControllerComponent : AndroidInjector<CategoryController> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<CategoryController>()
}