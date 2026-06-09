package com.example.domain.usecase

import com.example.domain.model.Item
import com.example.domain.repository.ILostFoundRepository
import javax.inject.Inject

class GetItemByIdUseCase @Inject constructor(
    private val repository: ILostFoundRepository
) {
    suspend operator fun invoke(id: String): Result<Item?> {
        return try {
            repository.getItemById(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}