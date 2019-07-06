package com.danielgergely.jogjegyzet.di

import android.content.Context
import com.danielgergely.jogjegyzet.R
import dagger.Module
import dagger.Provides
import javax.inject.Named

const val CONFIG_BASE_URL = "baseUrl"

@Module
class ConfigModule {
    @Provides
    @Named(CONFIG_BASE_URL)
    fun baseUrl(context: Context) = context.getString(R.string.base_url)
}