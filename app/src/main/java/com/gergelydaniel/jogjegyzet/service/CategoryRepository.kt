package com.gergelydaniel.jogjegyzet.service

import com.gergelydaniel.jogjegyzet.api.ApiClient
import com.gergelydaniel.jogjegyzet.domain.Category
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
}

internal fun String?.nullOrEmpty() = this == null || this.isEmpty()