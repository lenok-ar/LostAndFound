package com.example.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.services.map.MapService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit, mapService: MapService) {
    val context = LocalContext.current
    var routeError by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("О нас") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Сервис Потерял-Нашёл")
            Text("Помощь в нахождении потерянных вещей в КемГУ.")
            Text("Адрес: г. Кемерово, улица Красная, дом 6")
            Text("Телефон: 8 800 555 33 55")
            Text("Email: kemgu@gmail.com")
            Text("Режим работы: с 08:00 до 21:00")

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    routeError = !mapService.openRouteToOffice(context)
                }
            ) {
                Text("Проложить маршрут")
            }

            if (routeError) {
                Text("Не удалось открыть маршрут. Установите Яндекс Карты или проверьте браузер.")
            }

            if (!mapService.isConfigured) {
                Text("Для карты добавьте YANDEX_MAPKIT_API_KEY в local.properties")
            } else {
                OfficeMap(mapService)
            }

            Button(modifier = Modifier.fillMaxWidth(), onClick = onBack) {
                Text("Назад")
            }
        }
    }
}

@Composable
private fun OfficeMap(mapService: MapService) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, mapService) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapService.onStart()
                Lifecycle.Event.ON_STOP -> mapService.onStop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapService.onStop()
        }
    }

    AndroidView(
        factory = {
            mapService.createOfficeMapView(context).also { mapService.onStart() }
        },
        modifier = Modifier.fillMaxWidth().height(240.dp)
    )
}
