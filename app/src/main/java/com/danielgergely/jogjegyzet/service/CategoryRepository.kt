package com.danielgergely.jogjegyzet.service

import com.danielgergely.jogjegyzet.api.ApiClient
import com.danielgergely.jogjegyzet.domain.Category
import com.danielgergely.jogjegyzet.domain.NoInternetException
import com.danielgergely.jogjegyzet.util.maybeFromNullable
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val apiClient: ApiClient) {
    private val cache: BehaviorSubject<List<Category>> = BehaviorSubject.createDefault(listOf())

    init {
        update().subscribe({}, {}) // We ignore results here
    }

    private fun downloadCategories() = apiClient.getCategories()
            .onErrorResumeNext { error ->
                when (error) {
                    is UnknownHostException -> Single.error(NoInternetException())
                    else -> Single.error(error)
                }
            }
            .subscribeOn(Schedulers.io())
            .doOnSuccess { cache.onNext(it) }

    fun update(): Completable = downloadCategories()
            .ignoreElement()


    private fun getCategories() = cache.switchIfEmpty(downloadCategories().toObservable())
            .switchMap {
                if (it.isEmpty()) {
                    downloadCategories().toObservable()
                } else {
                    Observable.just(it)
                }
            }

    fun getRootCategories(): Observable<List<Category>> = getCategories()
            .map { it.filter { it.parentId.nullOrEmpty() } }


    fun getSubCategories(parentId: String): Observable<List<Category>> = getCategories().map { it.filter { parentId == it.parentId } }

    fun getCategory(id: String): Maybe<Category> {
        return getCategories().flatMapMaybe {
            maybeFromNullable(it.firstOrNull { it.id == id })
        }.switchIfEmpty(apiClient.getCategory(id).toObservable()).firstElement()

    }
}

internal fun String?.nullOrEmpty() = this == null || this.isEmpty()