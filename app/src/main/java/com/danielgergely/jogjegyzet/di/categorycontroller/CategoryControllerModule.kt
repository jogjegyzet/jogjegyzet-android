package com.danielgergely.jogjegyzet.di.categorycontroller

import com.bluelinelabs.conductor.Controller
import com.christianbahl.conductor.ControllerKey
import com.danielgergely.jogjegyzet.ui.category.CategoryController
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [CategoryControllerComponent::class])
internal abstract class CategoryControllerModule {
    @Binds
    @IntoMap
    @ControllerKey(CategoryController::class)
    abstract fun bind(builder: CategoryControllerComponent.Builder) : AndroidInjector.Factory<out Controller>
}