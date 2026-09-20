package com.example.chat_09_01.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_09_01.chatapp.ChatWebSocketClient
import com.example.chat_09_01.data.ChatDatabase
import com.example.chat_09_01.data.MessageEntity
import com.example.chat_09_01.network.AuthApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(app: Application) : AndroidViewModel(app) {

    private val chatClient = ChatWebSocketClient()
    private val dao = ChatDatabase.get(app).messageDao()

    val messages: StateFlow<List<MessageEntity>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val isConnected: StateFlow<Boolean> = chatClient.isConnected

    // 登录状态
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    var username: String = ""
        private set
    private var token: String = ""

    init {
        viewModelScope.launch {
            chatClient.incomingMessages.collect { msg ->
                dao.insert(MessageEntity(
                    username = msg.username,
                    text = msg.text,
                    isFromMe = msg.isFromMe,
                    isSystem = msg.isSystem
                ))
            }
        }
    }

    /** 登录 */
    fun login(username: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = AuthApi.login(username, password)
            if (result.ok && result.token != null) {
                this@ChatViewModel.username = result.username ?: username
                this@ChatViewModel.token = result.token
                _authState.value = AuthState.Success
                connectWebSocket()
            } else {
                _authState.value = AuthState.Error(result.message ?: "登录失败")
            }
        }
    }

    /** 注册 */
    fun register(username: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = AuthApi.register(username, password)
            if (result.ok) {
                // 注册成功后自动登录
                login(username, password)
            } else {
                _authState.value = AuthState.Error(result.message ?: "注册失败")
            }
        }
    }

    private fun connectWebSocket() {
        val url = "ws://10.0.2.2:8080/?token=$token"
        chatClient.connect(url, username)
    }

    fun disconnect() {
        chatClient.disconnect()
        _authState.value = AuthState.Idle
    }

    fun sendMessage(text: String) = chatClient.sendMessage(text)

    fun clearHistory() {
        viewModelScope.launch { dao.clearAll() }
    }

    override fun onCleared() {
        super.onCleared()
        chatClient.disconnect()
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }
}