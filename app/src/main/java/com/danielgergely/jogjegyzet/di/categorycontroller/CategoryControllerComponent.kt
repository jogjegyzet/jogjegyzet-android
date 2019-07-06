package com.danielgergely.jogjegyzet.di.categorycontroller

import com.danielgergely.jogjegyzet.ui.category.CategoryController
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface CategoryControllerComponent : AndroidInjector<CategoryController> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<CategoryController>()
}