package com.example.chat_09_01.chatapp

import com.example.chat_09_01.data.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString


class ChatWebSocketClient {

    private var pendingUsername: String? = null

    fun setPendingUsername(username: String){
        pendingUsername = username
    }

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    // 用于 UI 的消息列表
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    // 连接状态
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    fun connect(url: String) {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _isConnected.value = true
                addMessage("System: Connected to server",)

                pendingUsername?.let{
                    webSocket.send("join:$it")
                    pendingUsername = null
                }
            }

            //客户端收到服务器消息raw
            override fun onMessage(webSocket: WebSocket, text: String) {
                addMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                // 不处理二进制消息
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
                addMessage("Error: ${t.message}")
            }
        })
    }

    fun sendMessage(text: String,myUsername: String) {
        webSocket?.send(text)
        addMessage(Message(text = "$myUsername: $text", isFromMe = true))
    }

    private fun addMessage(message: Message) {
        _messages.value = _messages.value + message
        println("【更新Flow】消息列表size=${_messages.value.size}, msg=${message.text}")
    }

    private fun addMessage(rawText: String) {
        println("【addMessage入口】收到rawText = $rawText")

        if (rawText.startsWith("System")) {
            addMessage(Message(text = rawText, isFromMe = false))
            return
        }
        if (rawText.startsWith("Error: ")) {
            addMessage(Message(text = rawText, isFromMe = false))
            return
        }


        val splitIndex = rawText.indexOf(':')
        println("【addMessage】splitIndex=$splitIndex")
        if(splitIndex > 0){
            val senderName = rawText.substring(0, splitIndex)
            val content = rawText.substring(splitIndex + 1)
            val msg = Message(text = "$senderName: $content", isFromMe = false)
            addMessage(msg)
            println("【addMessage】解析带冒号消息 $msg")
        }else{
            // 后端纯文本消息走这里！
            val msg = Message(text = rawText, isFromMe = false)
            addMessage(msg)
            println("【addMessage】无冒号原始消息，直接添加 $msg")
        }


    }

    fun disconnect() {
        webSocket?.close(1000, "Client closing")
        webSocket = null
        _isConnected.value = false
    }

    fun join(username: String){
        webSocket?.send("join:$username")
    }

    fun sendChatMessage(content: String){
        webSocket?.send("chat:$content")
    }



}