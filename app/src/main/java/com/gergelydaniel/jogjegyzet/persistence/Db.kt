package com.gergelydaniel.jogjegyzet.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.gergelydaniel.jogjegyzet.persistence.favorite.FavoriteEntity
import com.gergelydaniel.jogjegyzet.persistence.favorite.FavouriteDAO

@Database(entities = [FavoriteEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class JogjegyzetDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavouriteDAO
}