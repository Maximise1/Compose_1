package com.example.tp_mvvm

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tp_mvvm.model.data.Chat
import com.example.tp_mvvm.model.data.ChatsDao
import com.example.tp_mvvm.model.data.ChatsDatabase
import com.example.tp_mvvm.model.data.Line
import com.example.tp_mvvm.model.data.LinesDao
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class ModelTest {
    private lateinit var chatsDao: ChatsDao
    private lateinit var linesDao: LinesDao
    private lateinit var db: ChatsDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ChatsDatabase::class.java).allowMainThreadQueries().build()
        chatsDao = db.chatsDao()
        linesDao = db.linesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testWriteAndReadToChats() = runBlocking {
        val chat = Chat(
            lastModified = System.currentTimeMillis() / 1000
        )
        chatsDao.insert(chat)
        val newChat = chatsDao.getAllChats().first()[0]
        assertEquals(newChat.lastModified, chat.lastModified)
    }

    @Test
    fun testWriteAndReadToLines() = runBlocking {
        val line = Line(
            lastModified = System.currentTimeMillis() / 1000,
            chatId = 0L,
            image = "",
            line = "Test line",
            sender = 0
        )
        linesDao.insert(line)
        val newLine = linesDao.getAllLines().first()[0]
        assertTrue(
            (newLine.lastModified == line.lastModified) && (newLine.line == line.line) &&
            (newLine.sender == line.sender) && (newLine.image == line.image) &&
            (newLine.chatId == line.chatId)
        )
    }

    @Test
    fun testCountChatsWithIds() = runBlocking {
        val chat = Chat(
            lastModified = System.currentTimeMillis() / 1000
        )
        chatsDao.insert(chat)
        chatsDao.insert(chat)
        assertTrue(
            (chatsDao.countChatsWithId(2L) == 1) && (chatsDao.countChatsWithId(3L) == 0)
        )
    }

    @Test
    fun testGetFirstLines() = runBlocking {
        val line1 = Line(
            lastModified = System.currentTimeMillis() / 1000,
            chatId = 0L,
            image = "",
            line = "Test line",
            sender = 0
        )
        val line2 = Line(
            lastModified = System.currentTimeMillis() / 1000 + 30,
            chatId = 0L,
            image = "",
            line = "Test line2",
            sender = 1
        )
        linesDao.insert(line1)
        linesDao.insert(line2)
        val firstLine = linesDao.getFirstLines().first()[0]
        assertTrue(
            (firstLine.lastModified == line2.lastModified) && (firstLine.line == line2.line) &&
                    (firstLine.sender == line2.sender) && (firstLine.image == line2.image) &&
                    (firstLine.chatId == line2.chatId)
        )
    }
}