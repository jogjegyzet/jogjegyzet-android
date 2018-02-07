package com.gergelydaniel.jogjegyzet.api.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.threeten.bp.LocalDateTime

class LocalDateTimeTypeAdapter : TypeAdapter<LocalDateTime>() {
    override fun write(out: JsonWriter?, value: LocalDateTime?) {
        TODO("not implemented")
    }

    override fun read(reader: JsonReader): LocalDateTime {
        val s = reader.nextString()
        return LocalDateTime.parse(s)
    }

}