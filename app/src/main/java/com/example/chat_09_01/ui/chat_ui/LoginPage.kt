package com.example.chat_09_01.ui.chat_ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.chat_09_01.viewmodel.ChatViewModel

@Composable
fun LoginPage(
    onLoginSuccess: () -> Unit,
    viewModel: ChatViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    // 登录成功且 WebSocket 已连上才切页面
    LaunchedEffect(isConnected) {
        if (isConnected) onLoginSuccess()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("聊天室登录", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名") },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // 错误提示
        (authState as? ChatViewModel.AuthState.Error)?.let {
            Text(it.message, color = Color.Red)
            Spacer(Modifier.height(8.dp))
        }

        val loading = authState is ChatViewModel.AuthState.Loading

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { viewModel.login(username.trim(), password) },
                enabled = !loading && username.isNotBlank() && password.isNotBlank()
            ) { Text(if (loading) "登录中…" else "登录") }

            OutlinedButton(
                onClick = { viewModel.register(username.trim(), password) },
                enabled = !loading && username.isNotBlank() && password.isNotBlank()
            ) { Text("注册") }
        }
    }
}