package com.example.chat_09_01.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    // UI 观察这个 Flow，数据库一变，UI 自动刷新
    @Query("SELECT * FROM messages ORDER BY timestamp ASC, id ASC")
    fun getAll(): Flow<List<MessageEntity>>

    @Insert
    suspend fun insert(msg: MessageEntity): Long

    @Query("DELETE FROM messages")
    suspend fun clearAll(): Int
}