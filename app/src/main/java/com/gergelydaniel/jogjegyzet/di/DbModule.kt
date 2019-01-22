package com.gergelydaniel.jogjegyzet.di

import android.arch.persistence.room.Room
import android.content.Context
import com.gergelydaniel.jogjegyzet.persistence.JogjegyzetDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {
    @Provides
    @Singleton
    fun provideDb(context: Context): JogjegyzetDatabase =
        Room.databaseBuilder(context.applicationContext, JogjegyzetDatabase::class.java, "jogjegyzet").build()


}