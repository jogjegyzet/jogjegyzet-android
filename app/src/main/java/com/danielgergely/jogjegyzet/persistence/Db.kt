package com.danielgergely.jogjegyzet.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.danielgergely.jogjegyzet.persistence.favorite.FavoriteEntity
import com.danielgergely.jogjegyzet.persistence.favorite.FavouriteDAO

@Database(entities = [FavoriteEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class JogjegyzetDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavouriteDAO
}