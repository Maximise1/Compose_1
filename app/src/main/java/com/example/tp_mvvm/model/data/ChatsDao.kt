package com.example.tp_mvvm.model.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: Chat): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(chat: Chat)

    @Delete
    suspend fun delete(chat: Chat)

    @Query("SELECT * from chats ORDER BY lastModified DESC")
    fun getAllChats(): Flow<List<Chat>>

    @Query("DELETE from chats WHERE id in (:chatIds)")
    fun deleteSelectedChats(chatIds: List<Long>)

    @Query("DELETE FROM chats")
    fun nukeTable1()

    @Query("DELETE FROM lines")
    fun nukeTable2()

    @Query("SELECT * from chats WHERE id in (:ids) ORDER BY lastModified DESC")
    fun getChatsWithIds(ids: Set<Long>): Flow<List<Chat>>
}