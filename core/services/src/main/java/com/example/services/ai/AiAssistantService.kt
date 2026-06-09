package com.example.services.ai

interface AiAssistantService {
    val isConfigured: Boolean

    suspend fun createSearchAdvice(description: String, circumstances: String): Result<String>
}
