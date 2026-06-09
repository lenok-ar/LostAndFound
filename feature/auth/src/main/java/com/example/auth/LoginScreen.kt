package com.example.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.services.auth.AuthResult

@Composable
fun LoginScreen(
    activity: Activity,
    viewModel: LoginViewModel,
    onLoggedIn: () -> Unit
) {
    var message by remember { mutableStateOf<String?>(null) }

    fun handleResult(result: AuthResult) {
        when (result) {
            is AuthResult.Success -> onLoggedIn()
            is AuthResult.Error -> message = result.message
            AuthResult.Cancelled -> message = "Вход отменён"
        }
    }

    val yandexLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleResult(viewModel.handleYandexResult(result.resultCode, result.data))
    }

    LaunchedEffect(Unit) {
        viewModel.onScreenOpened()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Вход в Потерял-Нашёл", style = MaterialTheme.typography.headlineSmall)
        Button(
            modifier = Modifier.padding(top = 24.dp),
            onClick = {
                val intent = viewModel.createYandexLoginIntent()
                if (intent == null) message = "Укажите YANDEX_CLIENT_ID в local.properties"
                else yandexLauncher.launch(intent)
            }
        ) {
            Text("Войти через Яндекс")
        }
        OutlinedButton(
            modifier = Modifier.padding(top = 8.dp),
            onClick = { viewModel.loginWithVk(activity, ::handleResult) }
        ) {
            Text("Войти через VK")
        }
        message?.let {
            Text(it, modifier = Modifier.padding(top = 16.dp), color = MaterialTheme.colorScheme.error)
        }
    }
}
