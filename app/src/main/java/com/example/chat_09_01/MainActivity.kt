package com.example.chat_09_01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_09_01.viewmodel.ChatViewModel
import com.example.chat_09_01.ui.chat_ui.ChatAppRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val chatViewModel: ChatViewModel = viewModel()
            ChatAppRoot(chatViewModel)
        }
    }
}

/*
@Composable
fun ChatApp(chatViewModel: ChatViewModel) {
    // 从 ViewModel 收集状态
    val messages by chatViewModel.messages.collectAsState()
    val isConnected by chatViewModel.isConnected.collectAsState()
    var inputText by remember { mutableStateOf("") }

    var usernameInput by remember { mutableStateOf("") }
    var isLoggedIn by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(isConnected) {
        if (!isConnected && isLoggedIn) {
            isLoggedIn = false
        }
    }

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


            OutlinedTextField(
                value = usernameInput,
                onValueChange = { usernameInput = it },
                modifier = Modifier
                    .height(64.dp)
                    .defaultMinSize(minHeight = 40.dp),
                placeholder = { Text("用户名") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
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
            ) {
                items(messages.reversed()) { msg ->
                    IOKEWdMessageBubble(msg)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 输入区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message") },
                    enabled = isConnected,
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp)
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

@Composable
fun IOKEWdMessageBubble(message: Message) {


    if (message.isSystem){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message.text,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        return
    }

    // 根据发送者决定气泡颜色和对齐方式
    val (backgroundColor, textColor) = if (message.isFromMe) {
        Triple(Color(0xFF95EC69), Color.White, Alignment.CenterEnd) // 紫色气泡在右
    } else {
        Triple(Color(0xFFFFFFFF), Color.Black, Alignment.CenterStart) // 灰色气泡在左
    }

    // ---------- 一行：头像 + 用户名 + 气泡 ----------
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // 别人的头像（在左）
        UserAvatar(message.text, size = 36.dp)
        Spacer(modifier = Modifier.width(8.dp))
    }

    // 中间：用户名 + 气泡
    Column(
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    ) {
        if (!message.isFromMe) {
            Text(
                text = message.username,
                fontSize = 12.sp,
                color = Color.Green,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        shadowElevation = 1.dp,
        modifier = Modifier.widthIn(max = 260.dp)
    ) {
        Text(
            text = message.text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(12.dp)
        )
    }

    if (message.isFromMe) {
        Spacer(modifier = Modifier.width(8.dp))
        UserAvatar(message.username, size = 36.dp)
    }
}

*/
