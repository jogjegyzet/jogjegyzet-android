package com.danielgergely.jogjegyzet.api.gson

import com.danielgergely.jogjegyzet.api.update.MessageType
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class MessageTypeDeserializer: JsonDeserializer<MessageType> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): MessageType =
            when(json.asString) {
                "opt" -> MessageType.OPTIONAL
                "obl" -> MessageType.MUST
                else -> throw IllegalArgumentException()
            }
}