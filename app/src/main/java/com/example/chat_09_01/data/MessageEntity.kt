package com.example.chat_09_01.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val text: String,
    val isFromMe: Boolean,
    val isSystem: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)