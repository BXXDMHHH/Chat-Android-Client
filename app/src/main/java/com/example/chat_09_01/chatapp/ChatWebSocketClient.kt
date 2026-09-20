package com.example.chat_09_01.chatapp

import com.example.chat_09_01.data.Message
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class ChatWebSocketClient {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var myUsername: String = ""

    // 收到的消息按顺序抛给 ViewModel，由 ViewModel 负责写库
    private val _incomingMessages = MutableSharedFlow<Message>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val incomingMessages: SharedFlow<Message> = _incomingMessages.asSharedFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    fun connect(url: String, username: String) {
        myUsername = username
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                _isConnected.value = true
                emitSystem("已经连接到服务器")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                parseAndEmit(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                _isConnected.value = false
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _isConnected.value = false
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _isConnected.value = false
                emitSystem("Error: ${t.message}")
            }
        })
    }

    fun sendMessage(text: String) {
        webSocket?.send(text)
    }

    fun disconnect() {
        webSocket?.close(1000, "Client closing")
        webSocket = null
        _isConnected.value = false
    }

    private fun parseAndEmit(text: String) {
        try {
            val json = JSONObject(text)
            val type = json.optString("type", "message")

            if (type == "system") {
                emitSystem(json.optString("text"))
            } else {
                val username = json.optString("username")
                val content = json.optString("text")
                _incomingMessages.tryEmit(
                    Message(
                        username = username,
                        text = content,
                        isFromMe = (username == myUsername),
                        isSystem = false
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun emitSystem(content: String) {
        _incomingMessages.tryEmit(
            Message(username = "", text = content, isFromMe = false, isSystem = true)
        )
    }
}