package com.example.domain.usecase

import com.example.domain.model.Filters
import com.example.domain.model.Item
import com.example.domain.repository.ILostFoundRepository
import javax.inject.Inject

class SearchItemsUseCase @Inject constructor(
    private val repository: ILostFoundRepository
) {
    suspend operator fun invoke(query: String): Result<List<Item>> {
        return try {
            require(query.isNotBlank()) { "Search query cannot be empty" }
            repository.getItems(Filters(query = query))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}