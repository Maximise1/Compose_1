package com.example.tp_mvvm.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lines")
data class Line (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val chatId: Long,
    val line: String,
    val lastModified: Long,
    val sender: Int,
    val image: String?,
)