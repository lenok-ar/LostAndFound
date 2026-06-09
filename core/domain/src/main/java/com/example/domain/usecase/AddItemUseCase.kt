package com.example.domain.usecase

import com.example.domain.model.Item
import com.example.domain.repository.ILostFoundRepository
import javax.inject.Inject

class AddItemUseCase @Inject constructor(
    private val repository: ILostFoundRepository
) {
    suspend operator fun invoke(item: Item): Result<String> {
        return try {
            require(item.title.isNotBlank()) { "Title cannot be empty" }
            require(item.description.isNotBlank()) { "Description cannot be empty" }
            repository.addItem(item)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}