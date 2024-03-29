package com.danielgergely.jogjegyzet.domain

sealed class SearchResult {
    class CategoryResult(val category: Category) : SearchResult()
    class DocumentResult(val document: Document) : SearchResult()
}