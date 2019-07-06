package com.danielgergely.jogjegyzet.api

import com.danielgergely.jogjegyzet.domain.*
import io.reactivex.Maybe
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor(private val retrofitClient: RetrofitClient) {
    fun getCategory(id: String): Maybe<Category> = retrofitClient.getCategory(id).flatMapMaybe {
        when {
            it.isSuccessful -> Maybe.just(it.body()!!)
            it.code() == 404 -> Maybe.empty()
            else -> throw Exception() //TODO
        }
    }

    private fun <T> handleError(response: Response<T>) : Single<T>
        = if (response.isSuccessful) {
            Single.just(response.body())
        } else {
            Single.error(Exception("Network error ${response.code()}"))
        }


    fun getCategories(): Single<List<Category>> =
            retrofitClient.getCategories().flatMap { handleError(it) }

    fun getDocumentsInCategory(categoryId: String): Single<List<Document>> =
            retrofitClient.getDocumentsInCategory(categoryId).flatMap { handleError(it) }


    fun getDocument(id: String): Single<Document> =
            retrofitClient.getDocument(id).flatMap { handleError(it) }

    fun search(query: String): Single<List<SearchResult>> =
            retrofitClient.search(query).flatMap { handleError(it) }


    fun getUser(id: String): Single<User> =
            retrofitClient.getUser(id).flatMap { handleError(it) }

    fun getCommentsForDocument(documentId: String) : Single<List<Comment>>
        = retrofitClient.getCommentsForDocument(documentId)
            .flatMap { handleError(it) }
}