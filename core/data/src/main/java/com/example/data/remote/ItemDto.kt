package com.example.data.remote

import com.example.domain.model.Category
import com.example.domain.model.Item
import com.example.domain.model.Status

data class ItemDto(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val location: String,
    val status: String,
    val date: Long,
    val imageUrl: String?,
    val authorId: String,
    val authorName: String,
    val authorContact: String
) {
    fun toDomain(): Item = Item(
        id = id,
        title = title,
        description = description,
        category = Category.valueOf(category),
        location = location,
        status = Status.valueOf(status),
        date = date,
        imageUrl = imageUrl,
        authorId = authorId,
        authorName = authorName,
        authorContact = authorContact
    )

    companion object {
        fun fromDomain(item: Item): ItemDto = ItemDto(
            id = item.id,
            title = item.title,
            description = item.description,
            category = item.category.name,
            location = item.location,
            status = item.status.name,
            date = item.date,
            imageUrl = item.imageUrl,
            authorId = item.authorId,
            authorName = item.authorName,
            authorContact = item.authorContact
        )
    }
}