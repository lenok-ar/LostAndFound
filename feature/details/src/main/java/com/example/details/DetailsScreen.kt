package com.example.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.model.Status

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    itemId: String,
    onBack: () -> Unit,
    viewModel: DetailsViewModel
) {
    val itemState by viewModel.item.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    // Сохраняем в локальную переменную для безопасного использования
    val item = itemState

    val statusText = when (item?.status) {
        Status.LOST -> "ПОТЕРЯНО"
        Status.FOUND -> "НАЙДЕНО"
        Status.RETURNED -> "ВОЗВРАЩЕНО"
        else -> ""
    }

    val categoryNames = mapOf(
        "ELECTRONICS" to "Электроника",
        "DOCUMENTS" to "Документы",
        "ACCESSORIES" to "Аксессуары",
        "OTHER" to "Другое"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали объявления") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize().wrapContentSize()
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Ошибка: $error", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadItem(itemId) }) {
                            Text("Повторить")
                        }
                    }
                }
                item != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Статус
                        val color = when (item.status) {
                            Status.LOST -> MaterialTheme.colorScheme.error
                            Status.FOUND -> MaterialTheme.colorScheme.primary
                            Status.RETURNED -> MaterialTheme.colorScheme.tertiary
                        }
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = color.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.labelMedium,
                                color = color,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        HorizontalDivider()

                        InfoRow(
                            label = "Категория",
                            value = categoryNames[item.category.name] ?: item.category.name
                        )
                        InfoRow(label = "Место", value = item.location)
                        InfoRow(label = "Дата", value = item.date.toDateString())

                        HorizontalDivider()

                        Text(
                            text = "Контактная информация",
                            style = MaterialTheme.typography.titleMedium
                        )
                        InfoRow(label = "Имя", value = item.authorName)
                        InfoRow(label = "Контакт", value = item.authorContact)

                        Button(
                            onClick = { /* Contact action */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Связаться")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun Long.toDateString(): String {
    val date = java.util.Date(this)
    val format = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale("ru"))
    return format.format(date)
}