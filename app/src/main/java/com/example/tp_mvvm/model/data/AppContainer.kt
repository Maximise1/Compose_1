package com.example.tp_mvvm.model.data

import android.content.Context

interface AppContainer {
    val chatsRepository: ChatsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val chatsRepository: ChatsRepository by lazy {
        OfflineChatsRepository(
            ChatsDatabase.getDatabase(context).chatsDao(),
            ChatsDatabase.getDatabase(context).linesDao(),
        )
    }
}