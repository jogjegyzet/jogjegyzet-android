package com.gergelydaniel.jogjegyzet.di.documentcontroller

import com.gergelydaniel.jogjegyzet.ui.document.DocumentController
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface DocumentControllerComponent : AndroidInjector<DocumentController> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DocumentController>()
}