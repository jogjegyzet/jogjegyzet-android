package com.danielgergely.jogjegyzet.service

import com.danielgergely.jogjegyzet.domain.Document
import com.danielgergely.jogjegyzet.persistence.JogjegyzetDatabase
import com.danielgergely.jogjegyzet.persistence.favorite.mapFromEntity
import com.danielgergely.jogjegyzet.persistence.favorite.mapToEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

private val PUBLISH_DATA = Any()

@Singleton
class FavoriteRepository @Inject constructor(private val db: JogjegyzetDatabase) {
    private val refreshSubject = PublishSubject.create<Any>()

    fun getFavorites(): Observable<List<Document>> =
            Observable.fromCallable { db.favoriteDao().getAll().map(::mapFromEntity) }
                    .repeatWhen { refreshSubject }

    fun containsItems(ids: List<String>): Single<List<Boolean>> = Single.fromCallable { db.favoriteDao().containsDocs(ids) }

    fun getById(id: String): Maybe<Document> =
            Maybe.fromCallable { db.favoriteDao().getById(id)?.let(::mapFromEntity) }

    /**
     * Deletes an entity, returns its old index
     */
    fun deleteById(id: String) = Single.fromCallable {
        db.favoriteDao().deleteByIdReturnIndex(id)
    }.doOnSuccess { refreshSubject.onNext(PUBLISH_DATA) }

    fun insert(document: Document): Completable =
            Completable.fromAction { db.favoriteDao().insert(document.let(::mapToEntity)) }
                    .doOnComplete { refreshSubject.onNext(PUBLISH_DATA) }

    fun insert(document: Document, index: Int): Completable =
            Completable.fromAction {
                val entity = document.let(::mapToEntity)
                entity.order = index
                db.favoriteDao().insertWithIndex(entity)
            }.doOnComplete { refreshSubject.onNext(PUBLISH_DATA) }

    fun updateIfExists(document: Document): Single<Boolean> =
            Single.fromCallable { db.favoriteDao().updateIfContains(document.let(::mapToEntity)) }
                    .doOnSuccess { refreshSubject.onNext(PUBLISH_DATA) }

    fun updateIfExists(documents: Collection<Document>): Single<List<Boolean>> =
            Single.fromCallable { db.favoriteDao().updateAllIfContains(documents.map(::mapToEntity)) }
                    .doOnSuccess { refreshSubject.onNext(PUBLISH_DATA) }
}