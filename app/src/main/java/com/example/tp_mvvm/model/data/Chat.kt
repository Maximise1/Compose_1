package com.example.tp_mvvm.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "chats")
data class Chat (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var lastModified: Long,
)