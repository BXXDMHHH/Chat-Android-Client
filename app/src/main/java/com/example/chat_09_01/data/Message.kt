package com.example.chat_09_01.data

data class Message (
    val username: String,
    val text: String,
    val isFromMe: Boolean,
    val isSystem: Boolean = false
)