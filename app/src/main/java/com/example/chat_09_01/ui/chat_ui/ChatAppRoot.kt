package com.example.chat_09_01.ui.chat_ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.chat_09_01.viewmodel.ChatViewModel

@Composable
fun ChatAppRoot(chatViewModel: ChatViewModel) {
    var goChatRoom by remember { mutableStateOf(false) }

    val isConnected by chatViewModel.isConnected.collectAsState()

    LaunchedEffect(isConnected) {
        if (!isConnected && goChatRoom) {
            goChatRoom = false
        }
    }

    if (!goChatRoom) {
        LoginPage(
            onLoginSuccess = {
                goChatRoom = true
            },
            viewModel = chatViewModel
        )
    }else {
        ChatRoomPage(
            onExit = {
                goChatRoom = false
            },
            viewModel = chatViewModel
        )
    }

}