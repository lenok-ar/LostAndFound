package com.example.domain.repository

import com.example.domain.model.Filters
import com.example.domain.model.Item

interface ILostFoundRepository {
    suspend fun getItems(filters: Filters? = null): Result<List<Item>>
    suspend fun getItemById(id: String): Result<Item?>
    suspend fun addItem(item: Item): Result<String>
    suspend fun updateItem(item: Item): Result<Unit>
    suspend fun deleteItem(id: String): Result<Unit>
}