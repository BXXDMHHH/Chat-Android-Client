package com.example.chat_09_01.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chat_09_01.chatapp.ChatWebSocketClient
import kotlinx.coroutines.flow.StateFlow
import java.net.URLEncoder


class ChatViewModel : ViewModel() {

    // 持有 WebSocket 客户端
    private val chatClient = ChatWebSocketClient()

    // 暴露给 UI 的状态
    val messages: StateFlow<List<String>> = chatClient.messages
    val isConnected: StateFlow<Boolean> = chatClient.isConnected

    var username: String = ""
        private set

    // 连接服务器（模拟器访问宿主机用 10.0.2.2，真机用局域网 IP）
    fun connect(username: String) {
        this.username = username
        val url = "ws://10.0.2.2:8080/?username=${java.net.URLEncoder.encode(username, "UTF-8")}"
        chatClient.connect(url)
    }

    fun disconnect() {
        chatClient.disconnect()
    }

    fun sendMessage(text: String) {
        chatClient.sendMessage(text)
    }

    // ViewModel 销毁时断开连接，释放资源
    override fun onCleared() {
        super.onCleared()
        chatClient.disconnect()
    }
}