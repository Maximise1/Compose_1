package com.example.tp_mvvm.model.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LinesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(line: Line): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(line: Line)

    @Delete
    suspend fun delete(line: Line)

    @Query("SELECT * from lines where id = :id")
    fun getLine(id: Long): Flow<Line>

    @Query("SELECT * from lines where chatId = :chatId ORDER BY lastModified ASC")
    fun getAllChatLines(chatId: Long): Flow<List<Line>>

    @Query("SELECT * from (SELECT * FROM lines ORDER BY lastModified DESC) AS lines GROUP BY chatId")
    fun getFirstLines(): Flow<List<Line>>

    @Query("DELETE from lines WHERE chatId in (:chatIds)")
    fun deleteLinesFromChats(chatIds: List<Long>)

    @Query("SELECT * from lines")
    fun getAllLines(): Flow<List<Line>>
}