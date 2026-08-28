package com.example.booksy

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LibrosDao {
    @Query("SELECT * FROM libros")
    fun getAllTasks(): Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(book: Book)

    @Update
    suspend fun updateTask(book: Book)

    @Delete
    suspend fun deleteTask(book: Book)
}