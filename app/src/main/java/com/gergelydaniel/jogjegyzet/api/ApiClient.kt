package com.gergelydaniel.jogjegyzet.api

import com.gergelydaniel.jogjegyzet.domain.Category
import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.domain.SearchResult
import io.reactivex.Maybe
import io.reactivex.Single
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

    fun getCategories(): Single<List<Category>> =
            retrofitClient.getCategories().map {
                if (it.isSuccessful) {
                    it.body()!!
                } else {
                    throw Exception() //TODO
                }
            }

    fun getDocumentsInCategory(categoryId: String): Single<List<Document>> =
            retrofitClient.getDocumentsInCategory(categoryId).map {
                if (it.isSuccessful) {
                    it.body()!!
                } else {
                    throw Exception() //TODO
                }
            }


    fun getDocument(id: String): Single<Document> =
            retrofitClient.getDocument(id).map {
                if (it.isSuccessful) {
                    it.body()!!
                } else {
                    throw Exception() //TODO
                }
            }

    fun search(query: String): Single<List<SearchResult>> =
            retrofitClient.search(query).map {
                if (it.isSuccessful) {
                    it.body()!!
                } else {
                    throw Exception() //TODO
                }
            }

}