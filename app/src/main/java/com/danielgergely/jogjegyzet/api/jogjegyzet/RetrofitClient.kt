package com.danielgergely.jogjegyzet.api.jogjegyzet

import com.danielgergely.jogjegyzet.domain.*
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitClient {
    @GET("categories")
    fun getCategories(): Single<Response<List<Category>>>

    @GET("categories/{docId}")
    fun getCategory(@Path("docId") id: String): Single<Response<Category?>>

    @GET("categories/{docId}/documents")
    fun getDocumentsInCategory(@Path("docId") id: String): Single<Response<List<Document>>>

    @GET("documents/{docId}")
    fun getDocument(@Path("docId") id: String): Single<Response<Document>>

    @GET("search")
    fun search(@Query("q") query: String): Single<Response<List<SearchResult>>>

    @GET("users/{docId}")
    fun getUser(@Path("docId") id: String): Single<Response<User>>

    @GET("documents/{docId}/comments")
    fun getCommentsForDocument(@Path("docId") documentId: String) : Single<Response<List<Comment>>>
}