package com.danielgergely.jogjegyzet.service

import com.danielgergely.jogjegyzet.api.ApiClient
import com.danielgergely.jogjegyzet.domain.Comment
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(private val apiClient: ApiClient) {

    fun getCommentsForDocument(documentId: String) : Observable<List<Comment>> {
        return apiClient
                .getCommentsForDocument(documentId)
                .repeatWhen { it.delay(10, TimeUnit.SECONDS) }
                .distinctUntilChanged()
                .toObservable()
    }
}