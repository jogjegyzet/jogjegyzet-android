package com.danielgergely.jogjegyzet.di

import androidx.room.Room
import android.content.Context
import com.danielgergely.jogjegyzet.persistence.JogjegyzetDatabase
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