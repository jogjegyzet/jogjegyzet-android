package com.gergelydaniel.jogjegyzet.service

import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.persistence.JogjegyzetDatabase
import com.gergelydaniel.jogjegyzet.persistence.favorite.mapFromEntity
import com.gergelydaniel.jogjegyzet.persistence.favorite.mapToEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(private val db: JogjegyzetDatabase) {
    fun getFavorites(): Single<List<Document>> =
            Single.fromCallable { db.favoriteDao().getAll().map(::mapFromEntity) }

    fun getById(id: String): Maybe<Document> =
            Maybe.fromCallable { db.favoriteDao().getById(id)?.let(::mapFromEntity) }

    fun insert(document: Document): Completable =
            Completable.fromAction { db.favoriteDao().insert(document.let(::mapToEntity)) }

    fun updateIfExists(document: Document): Completable =
            Completable.fromAction { db.favoriteDao().updateIfContains(document.let(::mapToEntity)) }

    fun updateIfExists(documents: Collection<Document>): Completable =
            Completable.fromAction { db.favoriteDao().updateAllIfContains(documents.map(::mapToEntity)) }
}