package com.gergelydaniel.jogjegyzet.di.documentcontroller

import com.bluelinelabs.conductor.Controller
import com.christianbahl.conductor.ControllerKey
import com.gergelydaniel.jogjegyzet.ui.category.CategoryController
import com.gergelydaniel.jogjegyzet.ui.document.DocumentController
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [DocumentControllerComponent::class])
internal abstract class DocumentControllerModule {
    @Binds
    @IntoMap
    @ControllerKey(DocumentController::class)
    abstract fun bind(builder: DocumentControllerComponent.Builder) : AndroidInjector.Factory<out Controller>
}