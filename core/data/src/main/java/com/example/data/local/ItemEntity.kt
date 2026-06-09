package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Category
import com.example.domain.model.Item
import com.example.domain.model.Status

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
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
)

fun ItemEntity.toDomain(): Item = Item(
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

fun Item.toEntity(): ItemEntity = ItemEntity(
    id = id,
    title = title,
    description = description,
    category = category.name,
    location = location,
    status = status.name,
    date = date,
    imageUrl = imageUrl,
    authorId = authorId,
    authorName = authorName,
    authorContact = authorContact
)
