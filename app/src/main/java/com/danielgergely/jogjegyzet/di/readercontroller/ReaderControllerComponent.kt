package com.danielgergely.jogjegyzet.di.readercontroller

import com.danielgergely.jogjegyzet.ui.reader.ReaderController
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface ReaderControllerComponent : AndroidInjector<ReaderController> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ReaderController>()
}