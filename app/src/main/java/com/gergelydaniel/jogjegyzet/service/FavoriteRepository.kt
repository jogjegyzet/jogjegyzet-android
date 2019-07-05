package com.gergelydaniel.jogjegyzet.service

import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.persistence.JogjegyzetDatabase
import com.gergelydaniel.jogjegyzet.persistence.favorite.mapFromEntity
import com.gergelydaniel.jogjegyzet.persistence.favorite.mapToEntity
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

    fun deleteById(id: String) = Completable.fromAction {
        db.favoriteDao().deleteById(id)
    }.doOnComplete { refreshSubject.onNext(PUBLISH_DATA) }

    fun insert(document: Document): Completable =
            Completable.fromAction { db.favoriteDao().insert(document.let(::mapToEntity)) }
                    .doOnComplete { refreshSubject.onNext(PUBLISH_DATA) }

    fun updateIfExists(document: Document): Single<Boolean> =
            Single.fromCallable { db.favoriteDao().updateIfContains(document.let(::mapToEntity)) }
                    .doOnSuccess { refreshSubject.onNext(PUBLISH_DATA) }

    fun updateIfExists(documents: Collection<Document>): Single<List<Boolean>> =
            Single.fromCallable { db.favoriteDao().updateAllIfContains(documents.map(::mapToEntity)) }
                    .doOnSuccess { refreshSubject.onNext(PUBLISH_DATA) }
}