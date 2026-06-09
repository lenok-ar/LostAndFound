package com.example.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Item
import com.example.domain.usecase.GetItemByIdUseCase
import com.example.services.crash.CrashReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val getItemByIdUseCase: GetItemByIdUseCase,
    private val crashReporter: CrashReporter
) : ViewModel() {

    private val _item = MutableStateFlow<Item?>(null)
    val item = _item.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadItem(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                getItemByIdUseCase(id)
                    .onSuccess { item -> _item.value = item }
                    .onFailure { exception -> handleError(exception, id) }
            } catch (exception: Exception) {
                handleError(exception, id)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleError(exception: Throwable, itemId: String) {
        crashReporter.setKey("screen", "details")
        crashReporter.setKey("item_id_present", itemId.isNotBlank().toString())
        crashReporter.recordNonFatal(exception)
        _error.value = exception.message ?: "Не удалось загрузить объявление"
    }
}
