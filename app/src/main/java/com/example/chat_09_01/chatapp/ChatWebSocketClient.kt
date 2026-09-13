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
import org.json.JSONObject
import kotlin.math.log


class ChatWebSocketClient {


    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    //当前登录用户名字，用来判断消息是不是自己发的（`isFromMe`）
    private var myUsername: String = ""
    // 用于 UI 的消息列表
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    // 连接状态
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    fun connect(url: String,username: String) {
        myUsername = username
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _isConnected.value = true
                addSystemMessage("已经连接到服务器")
            }

            //客户端收到服务器消息r
            override fun onMessage(webSocket: WebSocket, text: String) {
                parseAndAdd(text)
            }

/*            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                // 不处理二进制消息
            }*/

            //服务端告知要关闭连接（握手关闭）
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                _isConnected.value = false
            }

            //连接已经完全关闭
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _isConnected.value = false
            }

            //连接异常（网络错误、断网、服务宕机)
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _isConnected.value = false
                addSystemMessage("Error: ${t.message}")
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


    private fun parseAndAdd(text: String) {
        try {
            val json = JSONObject(text)
            val type = json.optString("type","user")

            if (type == "system") {
                addSystemMessage(json.optString("text"))
            }else {
                val username = json.optString("username")
                val content = json.optString("text")
                val msg = Message(
                    username = username,
                    text = content,
                    isFromMe = (username == myUsername),
                    isSystem = false
                )
                addMessage(msg)
            }

        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addMessage(message: Message) {
        _messages.value += message
    }

    private fun addSystemMessage(content: String) {
        addMessage(Message(
            username = "",
            text = content,
            isFromMe = false,
            isSystem = true
        ))
    }


}