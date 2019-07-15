package com.danielgergely.jogjegyzet.di.updatecontroller

import com.bluelinelabs.conductor.Controller
import com.christianbahl.conductor.ControllerKey
import com.danielgergely.jogjegyzet.ui.update.UpdateController
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [UpdateControllerComponent::class])
internal abstract class UpdateControllerModule {
    @Binds
    @IntoMap
    @ControllerKey(UpdateController::class)
    abstract fun bind(builder: UpdateControllerComponent.Builder) : AndroidInjector.Factory<out Controller>
}