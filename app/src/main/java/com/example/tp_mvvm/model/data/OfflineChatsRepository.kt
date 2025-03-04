package com.example.tp_mvvm.model.data

import kotlinx.coroutines.flow.Flow

class OfflineChatsRepository (
    private val chatsDao: ChatsDao,
    private val linesDao: LinesDao
) : ChatsRepository {
    override fun getAllChatsStream(): Flow<List<Chat>> = chatsDao.getAllChats()
    override fun getAllChatLinesStream(
        chatId: Long
    ): Flow<List<Line>> = linesDao.getAllChatLines(chatId)

    override fun getLineStream(id: Long): Flow<Line?> = linesDao.getLine(id)

    override fun getAllLines(): Flow<List<Line>> = linesDao.getAllLines()

    override fun getChatsWithIds(ids: Set<Long>): Flow<List<Chat>> = chatsDao.getChatsWithIds(ids)

    override fun nukeTable1() {
        chatsDao.nukeTable1()
    }
    override fun nukeTable2() {
        chatsDao.nukeTable2()
    }

    override fun getFirstLines(): Flow<List<Line>> = linesDao.getFirstLines()

    override fun deleteSelectedChats(chatIds: List<Long>) = chatsDao.deleteSelectedChats(chatIds)
    override fun deleteLinesFromChats(chatIds: List<Long>) = linesDao.deleteLinesFromChats(chatIds)

    override suspend fun insertChat(chat: Chat): Long = chatsDao.insert(chat)
    override suspend fun insertLine(line: Line): Long = linesDao.insert(line)

    override suspend fun deleteChat(chat: Chat) = chatsDao.delete(chat)
    override suspend fun deleteLine(line: Line) = linesDao.delete(line)

    override suspend fun updateChat(chat: Chat) = chatsDao.update(chat)
    override suspend fun updateLine(line: Line) = linesDao.update(line)
}