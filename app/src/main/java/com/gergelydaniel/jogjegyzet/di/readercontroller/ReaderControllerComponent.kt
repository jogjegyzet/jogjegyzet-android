package com.gergelydaniel.jogjegyzet.di.readercontroller

import com.gergelydaniel.jogjegyzet.ui.reader.ReaderController
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface ReaderControllerComponent : AndroidInjector<ReaderController> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ReaderController>()
}