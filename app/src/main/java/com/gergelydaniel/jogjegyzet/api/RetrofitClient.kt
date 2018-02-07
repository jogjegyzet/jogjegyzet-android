package com.gergelydaniel.jogjegyzet.api

import com.gergelydaniel.jogjegyzet.domain.Category
import com.gergelydaniel.jogjegyzet.domain.Document
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitClient {
    @GET("categories")
    fun getCategories() : Single<Response<List<Category>>>

    @GET("categories/{id}")
    fun getCategory(@Path("id") id : String) : Single<Response<Category>>

    @GET("categories/{id}/documents")
    fun getDocumentsInCategory(@Path("id") id : String) : Single<Response<List<Document>>>

    @GET("documents/{id}")
    fun getDocument(@Path("id") id : String) : Single<Response<Document>>
}