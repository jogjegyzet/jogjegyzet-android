package com.danielgergely.jogjegyzet.service

import android.util.LruCache
import com.danielgergely.jogjegyzet.api.ApiClient
import com.danielgergely.jogjegyzet.domain.User
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val apiClient: ApiClient) {
    private val cache = LruCache<String, User>(40)

    fun getUser(id: String): Observable<User> {
        return Observable.defer {
            val cached: User? = cache.get(id)
            if (cached != null) {
                Observable.just(cached)
            } else {
                apiClient.getUser(id).toObservable()
                        .doOnNext { cache.put(it.id, it) }
            }
        }
    }
}