package com.danielgergely.jogjegyzet.persistence.favorite

import androidx.room.*

@Dao
abstract class FavouriteDAO {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    protected abstract fun insertInner(entity: FavoriteEntity)

    @Query("DELETE FROM favorite WHERE doc_id = :id")
    protected abstract fun deleteByIdInner(id: String)

    @Query("SELECT * FROM favorite ORDER BY `order`")
    abstract fun getAll(): Array<FavoriteEntity>

    @Query("SELECT * FROM favorite WHERE doc_id = :id")
    abstract fun getById(id: String): FavoriteEntity?

    @Query("UPDATE favorite SET `order` = `order` - 1 WHERE `order` >= :from")
    protected abstract fun decreaseOrder(from: Int)

    @Query("UPDATE favorite SET `order` = `order` + 1 WHERE `order` >= :from")
    protected abstract fun increaseOrder(from: Int)

    @Query("SELECT MAX(`order`) FROM favorite")
    protected abstract fun getHighestIndex(): Int?

    fun containsDocs(ids: List<String>) : List<Boolean> {
        val current = getAll().map { it.docId }

        return ids.map { current.contains(it) }
    }

    @Transaction
    open fun insert(entity: FavoriteEntity) {
        val highestIndex = getHighestIndex() ?: 0
        insertInner(entity.copy(order = highestIndex + 1))
    }

    @Transaction
    open fun insertWithIndex(entity: FavoriteEntity) {
        increaseOrder(entity.order)
        insertInner(entity)
    }

    @Transaction
    open fun deleteByIdReturnIndex(id: String): Int {
        val entity = getById(id) ?: return -1
        val index = entity.order

        deleteByIdInner(id)
        decreaseOrder(index)

        return index
    }

    @Transaction
    open fun updateIfContains(entity: FavoriteEntity) : Boolean {
        val prev = getById(entity.docId) ?: return false

        deleteByIdInner(entity.docId)
        insertInner(entity.copy(order = prev.order))

        return true
    }

    @Transaction
    open fun updateAllIfContains(entities: Collection<FavoriteEntity>): List<Boolean> {
        val current = getAll().map { it.docId }

        return entities.map {
            if (current.contains(it.docId) ) {
                val entity = getById(it.docId)!!
                deleteByIdInner(it.docId)
                insertInner(it.copy(order = entity.order))
                true
            } else false
        }
    }
}