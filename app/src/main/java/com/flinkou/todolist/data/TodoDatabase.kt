package com.flinkou.todolist.data

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.flinkou.todolist.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Todo::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun taskDao(): TodoDao

    class Callback @Inject constructor(
        private val database: Provider<TodoDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Todo(
                    Todo.getNextID(),
                    "todo1",
                    "low",
                    false,
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    System.currentTimeMillis()
                ),)
                dao.insert(Todo(
                    Todo.getNextID(),
                    "todo2",
                    "basic",
                    false,
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    System.currentTimeMillis()
                ),)
                dao.insert(Todo(
                    Todo.getNextID(),
                    "todo3",
                    "important",
                    true,
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    System.currentTimeMillis()
                ),)
                dao.insert(Todo(
                    Todo.getNextID(),
                    "todo4",
                    "basic",
                    false,
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    System.currentTimeMillis()
                ),)
            }
        }
    }
}