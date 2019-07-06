package com.danielgergely.jogjegyzet.api.gson

import com.danielgergely.jogjegyzet.domain.Category
import com.danielgergely.jogjegyzet.domain.Document
import com.danielgergely.jogjegyzet.domain.SearchResult
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type


class SearchResultDeserializer : JsonDeserializer<SearchResult> {

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): SearchResult {
        val jsonObject = json.asJsonObject

        val typeString = jsonObject["type"].asString
        val data = jsonObject["data"]

        return when (typeString) {
            "doc" -> SearchResult.DocumentResult(context.deserialize<Document>(data, Document::class.java))
            "cat" -> SearchResult.CategoryResult(context.deserialize<Category>(data, Category::class.java))

            else -> throw IllegalArgumentException()
        }
    }

}