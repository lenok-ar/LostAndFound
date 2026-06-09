package com.example.domain.model

data class Item(
    val id: String,
    val title: String,
    val description: String,
    val category: Category,
    val location: String,
    val status: Status,
    val date: Long,
    val imageUrl: String?,
    val authorId: String,
    val authorName: String,
    val authorContact: String
)

enum class Category {
    ELECTRONICS,
    DOCUMENTS,
    ACCESSORIES,
    OTHER
}

enum class Status {
    LOST,
    FOUND,
    RETURNED
}

data class Filters(
    val category: Category? = null,
    val status: Status? = null,
    val location: String? = null,
    val query: String? = null
)