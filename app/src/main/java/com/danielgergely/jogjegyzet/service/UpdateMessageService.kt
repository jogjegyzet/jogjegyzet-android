package com.danielgergely.jogjegyzet.service

import com.danielgergely.jogjegyzet.BuildConfig
import com.danielgergely.jogjegyzet.api.update.UpdateApiClient
import com.danielgergely.jogjegyzet.domain.UpdateMessage
import com.danielgergely.jogjegyzet.persistence.update.UpdateMessageStorage
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateMessageService @Inject constructor(private val apiClient: UpdateApiClient,
                                               private val storage: UpdateMessageStorage) {

    fun getUpdateMessageStatus(): Observable<UpdateMessage> = Observable.concat(
            Observable.fromCallable {
                storage.getMessage() ?: UpdateMessage.None
            },
            apiClient.getMessage(BuildConfig.VERSION_CODE)
                    .doOnSuccess { storage.storeMessage(it) }
                    .toObservable()
                    .onErrorResumeNext { _: Throwable -> Observable.empty() }
    ).map(::filterByCode).subscribeOn(Schedulers.io())

    private fun filterByCode(message: UpdateMessage): UpdateMessage {
        return when (message) {
            is UpdateMessage.None -> UpdateMessage.None
            is UpdateMessage.OptionalUpdate -> {
                if (message.maxVersion >= BuildConfig.VERSION_CODE) {
                    message
                } else {
                    UpdateMessage.None
                }
            }
            is UpdateMessage.MustUpdate -> {
                if (message.maxVersion >= BuildConfig.VERSION_CODE) {
                    message
                } else {
                    UpdateMessage.None
                }
            }
        }
    }
}
