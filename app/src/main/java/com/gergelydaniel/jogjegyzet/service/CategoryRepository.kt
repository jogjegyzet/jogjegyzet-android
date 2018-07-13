package com.gergelydaniel.jogjegyzet.service

import com.gergelydaniel.jogjegyzet.api.ApiClient
import com.gergelydaniel.jogjegyzet.domain.Category
import com.gergelydaniel.jogjegyzet.util.maybeFromNullable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val apiClient: ApiClient) {
    private val cache: BehaviorSubject<List<Category>> = BehaviorSubject.createDefault(listOf())

    init {
        update()
    }

    fun update() {
        apiClient.getCategories()
                .subscribeOn(Schedulers.io())
                .subscribe { cats -> cache.onNext(cats) }
    }

    fun getRootCategories(): Observable<List<Category>>
            = cache.map { it.filter { it.parentId.nullOrEmpty() } }

    fun getSubCategories(parentId: String): Observable<List<Category>>
            = cache.map { it.filter { parentId == it.parentId } }

    fun getCategory(id: String): Maybe<Category> {
        return cache.flatMapMaybe {
            maybeFromNullable(it.firstOrNull { it.id == id })
        }.switchIfEmpty(apiClient.getCategory(id).toObservable()).firstElement()

    }
}

internal fun String?.nullOrEmpty() = this == null || this.isEmpty()