package com.example.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.services.ai.AiAssistantService
import com.example.services.crash.CrashReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssistantViewModel(
    private val assistantService: AiAssistantService,
    private val crashReporter: CrashReporter
) : ViewModel() {
    private val _state = MutableStateFlow(AssistantUiState(isConfigured = assistantService.isConfigured))
    val state = _state.asStateFlow()

    fun updateDescription(value: String) {
        _state.value = _state.value.copy(description = value, error = null)
    }

    fun updateCircumstances(value: String) {
        _state.value = _state.value.copy(circumstances = value, error = null)
    }

    fun analyze() {
        val current = _state.value
        if (!current.canAnalyze) return

        viewModelScope.launch {
            _state.value = current.copy(isLoading = true, error = null, result = "")
            assistantService.createSearchAdvice(current.description,
                current.circumstances)
                .onSuccess { result -> _state.value = _state.value.copy(result = result) }
                .onFailure { error ->
                    crashReporter.recordNonFatal(error)
                    _state.value = _state.value.copy(
                        error = error.message ?: "Не удалось получить ответ"
                    )
                }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun clear() {
        _state.value = AssistantUiState(isConfigured = assistantService.isConfigured)
    }
}

data class AssistantUiState(
    val description: String = "",
    val circumstances: String = "",
    val result: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val isConfigured: Boolean = false
) {
    val canAnalyze: Boolean
        get() = description.isNotBlank() && circumstances.isNotBlank() && !isLoading && isConfigured
}
