package com.example.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Category
import com.example.domain.model.Item
import com.example.domain.model.Status
import com.example.domain.usecase.AddItemUseCase
import com.example.services.crash.CrashReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddViewModel(
    private val addItemUseCase: AddItemUseCase,
    private val crashReporter: CrashReporter
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    fun addItem(
        title: String,
        description: String,
        category: Category,
        status: Status,
        location: String,
        contact: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _isSuccess.value = false

            val item = Item(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                category = category,
                location = location,
                status = status,
                date = System.currentTimeMillis(),
                imageUrl = null,
                authorId = "temp_user_id",
                authorName = "Текущий пользователь",
                authorContact = contact
            )

            try {
                addItemUseCase(item)
                    .onSuccess { _isSuccess.value = true }
                    .onFailure(::handleError)
            } catch (exception: Exception) {
                handleError(exception)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSuccess() {
        _isSuccess.value = false
    }

    private fun handleError(exception: Throwable) {
        crashReporter.setKey("screen", "add")
        crashReporter.recordNonFatal(exception)
        _error.value = exception.message ?: "Не удалось добавить объявление"
    }
}
