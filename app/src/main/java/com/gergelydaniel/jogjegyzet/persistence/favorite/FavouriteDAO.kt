package com.gergelydaniel.jogjegyzet.persistence.favorite

import android.arch.persistence.room.*

@Dao
abstract class FavouriteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entity: FavoriteEntity)

    @Query("SELECT * FROM favorite ORDER BY `order`")
    abstract fun getAll(): Array<FavoriteEntity>

    @Query("SELECT * FROM favorite WHERE doc_id = :id")
    abstract fun getById(id: String): FavoriteEntity?

    @Transaction
    open fun updateIfContains(entity: FavoriteEntity) {
        val prev = getById(entity.docId) ?: return

        insert(entity)
    }

    @Transaction
    open fun updateAllIfContains(entities: Collection<FavoriteEntity>) {
        val current = getAll().map { it.docId }

        entities.filter { current.contains(it.docId) }.forEach(::insert)
    }
}