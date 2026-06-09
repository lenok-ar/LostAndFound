package com.example.domain.usecase

import com.example.domain.model.Filters
import com.example.domain.model.Item
import com.example.domain.repository.ILostFoundRepository
import javax.inject.Inject

class GetItemsUseCase @Inject constructor(
    private val repository: ILostFoundRepository
) {
    suspend operator fun invoke(filters: Filters? = null): Result<List<Item>> {
        return try {
            repository.getItems(filters)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}