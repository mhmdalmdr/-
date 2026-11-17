package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizResultDao {
    @Query("SELECT * FROM quiz_results ORDER BY completedAt DESC")
    fun getAllResults(): Flow<List<QuizResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: QuizResultEntity): Long

    @Query("DELETE FROM quiz_results WHERE id = :id")
    suspend fun deleteResultById(id: Long)

    @Query("DELETE FROM quiz_results")
    suspend fun clearAllResults()

    @Query("SELECT COUNT(*) FROM quiz_results")
    suspend fun getResultsCount(): Int
}
