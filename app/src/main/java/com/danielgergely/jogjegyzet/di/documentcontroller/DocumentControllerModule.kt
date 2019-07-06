package com.danielgergely.jogjegyzet.di.documentcontroller

import com.bluelinelabs.conductor.Controller
import com.christianbahl.conductor.ControllerKey
import com.danielgergely.jogjegyzet.ui.document.DocumentController
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