package com.example.tp_mvvm.model.data

import kotlinx.coroutines.flow.Flow

interface ChatsRepository {

    fun getAllChatsStream(): Flow<List<Chat>> //
    fun getAllChatLinesStream(chatId: Long): Flow<List<Line>> //

    fun getLineStream(id: Long): Flow<Line?> //

    fun getAllLines(): Flow<List<Line>> //

    fun getChatsWithIds(ids: Set<Long>): Flow<List<Chat>> //

    fun getFirstLines(): Flow<List<Line>> //

    fun deleteSelectedChats(chatIds: List<Long>) //

    fun deleteLinesFromChats(chatIds: List<Long>) //

    suspend fun insertChat(chat: Chat): Long //
    suspend fun insertLine(line: Line): Long //

    suspend fun deleteChat(chat: Chat)
    suspend fun deleteLine(line: Line)

    suspend fun updateChat(chat: Chat) //
    suspend fun updateLine(line: Line) //

    fun nukeTable1() //
    fun nukeTable2() //
}
