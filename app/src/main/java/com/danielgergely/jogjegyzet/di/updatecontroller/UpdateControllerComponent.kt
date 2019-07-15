package com.danielgergely.jogjegyzet.di.updatecontroller

import com.danielgergely.jogjegyzet.ui.update.UpdateController
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface UpdateControllerComponent: AndroidInjector<UpdateController> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<UpdateController>()
}