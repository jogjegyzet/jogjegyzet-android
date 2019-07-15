package com.danielgergely.jogjegyzet.api.update

import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Query

interface UpdateRetrofitClient {
    @GET("updatemessage")
    fun getUpdateMessage(@Query("v") v: String): Maybe<UpdateMessageEntity>
}

enum class MessageType {
    OPTIONAL,
    MUST
}

class UpdateMessageEntity(val type: MessageType,
                          val message: String,
                          val maxVersion: Int)