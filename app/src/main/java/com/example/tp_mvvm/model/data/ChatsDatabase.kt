package com.example.tp_mvvm.model.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Chat::class, Line::class], version = 1, exportSchema = false)
abstract class ChatsDatabase : RoomDatabase() {

    abstract fun chatsDao(): ChatsDao
    abstract fun linesDao(): LinesDao

    companion object {
        @Volatile
        private var Instance: ChatsDatabase? = null

        fun getDatabase(context: Context): ChatsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    ChatsDatabase::class.java,
                    "chats_database"
                )//.allowMainThreadQueries()
                    .build().also { Instance = it }
            }
        }
    }
}