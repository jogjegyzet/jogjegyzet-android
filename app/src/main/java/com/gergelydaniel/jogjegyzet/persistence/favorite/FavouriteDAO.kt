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

    fun containsDocs(ids: List<String>) : List<Boolean> {
        val current = getAll().map { it.docId }

        return ids.map { current.contains(it) }
    }

    @Transaction
    open fun updateIfContains(entity: FavoriteEntity) : Boolean {
        val prev = getById(entity.docId) ?: return false

        insert(entity)

        return true
    }

    @Transaction
    open fun updateAllIfContains(entities: Collection<FavoriteEntity>): List<Boolean> {
        val current = getAll().map { it.docId }

        return entities.map {
            if (current.contains(it.docId) ) {
                insert(it)
                true
            } else false
        }
    }
}