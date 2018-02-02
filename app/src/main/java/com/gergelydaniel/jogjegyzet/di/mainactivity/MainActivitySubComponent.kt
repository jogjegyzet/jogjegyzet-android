package com.gergelydaniel.jogjegyzet.di

import com.gergelydaniel.jogjegyzet.ui.MainActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [NavigationModule::class])
interface MainActivitySubComponent : AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}