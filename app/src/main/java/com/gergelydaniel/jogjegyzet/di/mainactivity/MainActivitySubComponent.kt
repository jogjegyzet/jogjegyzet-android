package com.gergelydaniel.jogjegyzet.di.mainactivity

import com.gergelydaniel.jogjegyzet.ui.MainActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent()
interface MainActivitySubComponent : AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}