package com.example.chat_09_01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_09_01.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val chatViewModel: ChatViewModel = viewModel()
            ChatApp(chatViewModel)
        }
    }
}

@Composable
fun ChatApp(chatViewModel: ChatViewModel) {
    // 从 ViewModel 收集状态
    val messages by chatViewModel.messages.collectAsState()
    val isConnected by chatViewModel.isConnected.collectAsState()
    var inputText by remember { mutableStateOf("") }

    var usernameInput by remember { mutableStateOf("") }
    var isLoggedIn by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    if (!isLoggedIn){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("输入用户名进入聊天室", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = usernameInput,
                onValueChange = { usernameInput = it },
                label = { Text("用户名") },
                singleLine = true
            )
            Spacer( modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (usernameInput.isNotBlank()) {
                        chatViewModel.connect(usernameInput.trim())
                        isLoggedIn = true
                    }
                },
                enabled = usernameInput.isNotBlank()
            ) {
                Text("进入聊天室")
            }
        }
    }else{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 连接状态指示
            Text(
                text = if (isConnected) "Connected" else "Disconnected",
                color = if (isConnected) Color.Green else Color.Red
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 消息列表
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                state = listState,
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    Text(
                        text = message,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 输入区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message") },
                    enabled = isConnected
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            chatViewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    enabled = isConnected
                ) {
                    Text("Send")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 连接/断开按钮
            Button(
                onClick = {
                    if (isConnected) {
                        chatViewModel.disconnect()
                        isLoggedIn = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("退出聊天室")
            }
        }
    }


}
