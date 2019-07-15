package com.danielgergely.jogjegyzet.api.update

import com.danielgergely.jogjegyzet.domain.UpdateMessage
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateApiClient @Inject constructor(private val client: UpdateRetrofitClient) {
    fun getMessage(version: Int): Single<UpdateMessage> = client.getUpdateMessage(version.toString())
            .map(::mapEntity)
            .toSingle(UpdateMessage.None)
}

private fun mapEntity(entity: UpdateMessageEntity) = when (entity.type) {
    MessageType.OPTIONAL -> UpdateMessage.OptionalUpdate(entity.message)
    MessageType.MUST -> UpdateMessage.MustUpdate(entity.message)
}