package com.example.tp_mvvm.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tp_mvvm.model.data.Chat
import com.example.tp_mvvm.model.data.ChatsRepository
import com.example.tp_mvvm.model.data.Line
import com.example.tp_mvvm.model.data.UserPreferencesRepository
import com.example.tp_mvvm.model.network.MedApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat

class AppViewModel(
    private val chatsRepository: ChatsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState(
        currentChats = chatsRepository.getAllChatsStream(),
        lastLines = chatsRepository.getFirstLines(),
        isDarkTheme = userPreferencesRepository.isDarkTheme
    ))
    val uiState: StateFlow<AppUiState> = _uiState

    suspend fun askModel(
        question: String,
        image: Bitmap?,
        chatId: Long,
    ) {
        viewModelScope.launch(Dispatchers.Default) {

            val lineId = createLine(
                chatId = chatId,
                sender = 1,
                text = "Sending request...",
                inc = 1L,
            )

            var withImage = "0"
            var height = "0"
            var width = "0"

            val encodedImage = if (image != null) {
                withImage = "1"
                height = image.height.toString()
                width = image.width.toString()
                encodeImage(image)
            }
            else {
                ""
            }

            val line = chatsRepository.getLineStream(lineId).first()
            var request: RequestState by mutableStateOf(RequestState.Loading)

            request = try {
                val body = mapOf(
                    "question" to question,
                    "file" to encodedImage,
                    "height" to height,
                    "width" to width,
                    "withImage" to withImage,
                )
                val answer = MedApi.retrofitService.askModel(body)
                updateLine(
                    oldLine = line!!,
                    text = answer
                )
                RequestState.Success("Success: $answer")
            } catch (e: IOException) {
                updateLine(
                    oldLine = line!!,
                    text = e.toString()
                )
                RequestState.Error
            }
        }
    }

    fun changeTheme(isDarkTheme: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.saveThemePreference(isDarkTheme)
        }
    }

    private fun encodeImage(image: Bitmap): String {
        val im = mutableListOf<String>()
        for (h in 0..<image.height) {
            for (w in 0..<image.width) {
                val pixel = image.getPixel(w, h)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)
                im.add(
                    blue.toString().toProperRGB() +
                    green.toString().toProperRGB() +
                    red.toString().toProperRGB())
            }
        }
        return im.joinToString(separator = "")
    }

    fun setAllChatsBack() {
        _uiState.update {
            it.copy(
                currentChats = chatsRepository.getAllChatsStream()
            )
        }
    }

    fun toggleBottomBar() {
        _uiState.update {
            it.copy(
                isBottomBarHidden = !it.isBottomBarHidden
            )
        }
    }

    fun toggleSearchState() {
        _uiState.update {
            it.copy(
                isSearchPressed = !it.isSearchPressed
            )
        }
    }

    fun toggleDeleteState() {
        _uiState.update {
            it.copy(
                isDeleteState = !it.isDeleteState
            )
        }
    }

    fun addChatToDeletion(chatId: Long) {
        _uiState.update {
            it.copy(
                chatsToDelete = it.chatsToDelete.addElement(chatId)
                //chatsToDelete = (it.chatsToDelete + chatId).toMutableList()
            )
        }
    }

    fun removeChatFromDeletion(chatId: Long) {
        val newList = uiState.value.chatsToDelete.toMutableList()
        newList.remove(chatId)

        _uiState.update {
            it.copy(
                chatsToDelete = newList
            )
        }
    }

    fun clearDeletionList() {
        _uiState.update {
            it.copy(
                isDeleteState = !it.isDeleteState,
                chatsToDelete = mutableListOf()
            )
        }
    }

    fun clearDatabase() {
        chatsRepository.nukeTable1()
        chatsRepository.nukeTable2()
    }

    suspend fun deleteChats(chatIds: List<Long>) {
        chatsRepository.deleteSelectedChats(chatIds)
        chatsRepository.deleteLinesFromChats(chatIds)
    }

    suspend fun deleteSelectedChats() {
        deleteChats(uiState.value.chatsToDelete)
        clearDeletionList()
    }

    fun getAllChatLines(chatId: Long): Flow<List<Line>> =
        chatsRepository.getAllChatLinesStream(chatId)

    suspend fun updateChat(chatId: Long) {
        val chat = Chat(
            id = chatId,
            lastModified = System.currentTimeMillis() / 1000
        )
        chatsRepository.updateChat(chat)
    }

    suspend fun updateLine(
        text: String,
        oldLine: Line
    ) {
        val line = Line(
            chatId = oldLine.chatId,
            id = oldLine.id,
            line = text,
            image = oldLine.image,
            lastModified = System.currentTimeMillis() / 1000,
            sender = oldLine.sender
        )
        chatsRepository.updateLine(line)
    }

    suspend fun updateCurrentChatsOnQuery(query: String) {
        val lines = chatsRepository.getAllLines().first()
        if (lines.isNotEmpty()) {
            val chatIds = mutableSetOf<Long>()
            val filteredLines = lines.filter { it.line.contains(query) }
            for (line in filteredLines) {
                chatIds.add(line.chatId)
            }
            _uiState.update {
                it.copy(
                    currentChats = chatsRepository.getChatsWithIds(chatIds)
                )
            }
        }
    }

    fun toggleTopBar() {
        _uiState.update {
            it.copy(
                isTopBarHidden = !it.isTopBarHidden
            )
        }
    }

    fun updateSelectedChatId(chatId: Long) {
        _uiState.update {
            it.copy(
                selectedChatId = chatId
            )
        }
    }

    fun parseDateTime(seconds: Long): String {
        val time = System.currentTimeMillis() / 1000 - seconds

        if (time < 60) {
            return "$time seconds ago"
        } else if (time / 60 < 60) {
            return (time / 60).toString() + " minutes ago"
        } else if (time / 3600 < 24) {
            return (time / 3600).toString() + " hours ago"
        } else if (time / 3600 / 24 < 2) {
            return "yesterday"
        } else if (time / 3600 / 24 < 365) {
            val formatter = SimpleDateFormat("dd MMM");
            return formatter.format(seconds*1000);
        } else {
            val formatter = SimpleDateFormat("dd MMM yyyy");
            return formatter.format(seconds*1000);
        }
    }

    suspend fun createChat(): Long {
        val chat = Chat(
            lastModified = System.currentTimeMillis() / 1000
        )
        val chatId = saveChat(chat)
        updateSelectedChatId(chatId)
        return chatId
    }

    suspend fun createLine(
        chatId: Long,
        sender: Int,
        text: String,
        inc: Long = 0L,
        image: String? = ""
    ): Long {
        val line = Line(
            chatId = chatId,
            lastModified = System.currentTimeMillis() / 1000 + inc,
            sender = sender,
            image = image,
            line = text
        )
        val lineId = chatsRepository.insertLine(line)
        return lineId
    }

    private suspend fun saveChat(chat: Chat): Long {
        return chatsRepository.insertChat(chat)
    }
}

fun <T:Any> MutableList<T>.addElement(value: T): MutableList<T> {
    val newList = this + value
    return newList.toMutableList()
}

fun String.toProperRGB(): String {
    var newString = this
    while (newString.length < 3) {
        newString = "0$newString"
    }
    return newString
}

data class AppUiState (
    val isBottomBarHidden: Boolean = false,
    val isTopBarHidden: Boolean = false,
    val isSearchPressed: Boolean = false,
    val isDeleteState: Boolean = false,
    val selectedChatId: Long = 0L,
    val selectedChat: Chat = Chat(lastModified = 0L),
    val currentChats: Flow<List<Chat>>,
    val chatsToDelete: MutableList<Long> = mutableListOf(),
    val lastLines: Flow<List<Line>>,
    val isDarkTheme: Flow<Boolean>,
)

sealed interface RequestState {
    data class Success(val answer: String) : RequestState
    object Error : RequestState
    object Loading : RequestState
}