package com.example.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Item
import com.example.domain.usecase.GetItemsUseCase
import com.example.services.crash.CrashReporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase,
    private val crashReporter: CrashReporter
) : ViewModel() {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                getItemsUseCase()
                    .onSuccess { items -> _items.value = items }
                    .onFailure(::handleError)
            } catch (exception: Exception) {
                handleError(exception)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleError(exception: Throwable) {
        crashReporter.setKey("screen", "feed")
        crashReporter.recordNonFatal(exception)
        _error.value = exception.message ?: "Не удалось загрузить объявления"
    }
}
