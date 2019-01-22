package com.gergelydaniel.jogjegyzet.persistence.favorite

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity(tableName = "favorite", indices = [Index(value = ["doc_id"], unique = true)])
class FavoriteEntity(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "order") var order: Int = 0,
        @ColumnInfo(name = "doc_id") var docId: String,
        @ColumnInfo(name = "category") var categoryId: String?,
        @ColumnInfo(name = "url") var fileUrl: String,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "date") var date: LocalDateTime,
        @ColumnInfo(name = "size") var size: Long,
        @ColumnInfo(name = "downloads") var downloads: Int,
        @ColumnInfo(name = "desc") var desc: String?,
        @ColumnInfo(name = "pos_ratings") var posRatings: Int,
        @ColumnInfo(name = "neg_ratings") var negRatings: Int
)
