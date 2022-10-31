package com.flinkou.todolist.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM task_table WHERE done != :todoState OR done = 0")
    fun getTasks(todoState: Boolean): Flow<List<Todo>>

    @Query("SELECT * FROM task_table WHERE done = 1")
    fun getCompletedTasks(): Flow<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Todo)

    @Update
    suspend fun update(task: Todo)

    @Delete
    suspend fun delete(task: Todo)
}