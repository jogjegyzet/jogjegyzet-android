package com.gergelydaniel.jogjegyzet.di.readercontroller

import com.bluelinelabs.conductor.Controller
import com.christianbahl.conductor.ControllerKey
import com.gergelydaniel.jogjegyzet.ui.reader.ReaderController
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [ReaderControllerComponent::class])
internal abstract class ReaderControllerModule {
    @Binds
    @IntoMap
    @ControllerKey(ReaderController::class)
    abstract fun bind(builder: ReaderControllerComponent.Builder) : AndroidInjector.Factory<out Controller>
}