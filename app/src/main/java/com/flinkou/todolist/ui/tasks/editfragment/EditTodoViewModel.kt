package com.flinkou.todolist.ui.tasks.editfragment

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flinkou.todolist.data.Todo
import com.flinkou.todolist.data.TodoDao
import com.flinkou.todolist.ui.tasks.events.EditTodoEvents
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditTodoViewModel @ViewModelInject constructor(
    private val todoDao: TodoDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    private val dateFormat = "dd.MM.yyyy"

    val todo = state.get<Todo>("todo")
    var todoText = state.get<String>("todo_text") ?: todo?.text ?: ""
        set(value) {
            field = value
            state.set("todo_text", value)
        }

    var todoIsDone = state.get<Boolean>("todo_done") ?: todo?.done ?: false
        set(value) {
            field = value
            state.set("todo_done", value)
        }

    var todoImportance = state.get<String>("todo_importance") ?: todo?.importance ?: "basic"
        set(value) {
            field = value
            state.set("todo_importance", value)
        }

    var todoDeadline = state.get<Long>("todo_deadline") ?: todo?.deadline ?: null
        set(value) {
            field = value
            Log.d("DEADLINE", value.toString())
            state.set("todo_deadline", value)
        }

    private val editTodoChannel = Channel<EditTodoEvents>()
    val editTodoEvent = editTodoChannel.receiveAsFlow()

    fun onSaveClick() {
        if (todoText.isBlank()) {
            showInvalidInputMessage("Text of the todo cannot be empty")
        } else {
            if (todo != null) {
                val updateTodo = todo.copy(
                    text = todoText,
                    importance = todoImportance,
                    deadline = todoDeadline,
                    updateTime = System.currentTimeMillis()
                )
                updateTodo(updateTodo)
            } else {
                val newTodo =
                    Todo(
                        Todo.getNextID(),
                        todoText,
                        todoImportance,
                        false,
                        todoDeadline,
                        System.currentTimeMillis(),
                        System.currentTimeMillis()
                    )
                createTodo(newTodo)
            }
        }
    }

    fun onCloseClick() {
        navigateBackAction()
    }

    fun onDeleteClick() {
        if (todo != null) {
            val todoDelete = todo
            deleteTodo(todoDelete)
        }
    }

    fun convertStringToLongDate(value: String): Long {
        val date = SimpleDateFormat(dateFormat).parse(value)
        return date?.time ?: System.currentTimeMillis()
    }

    fun convertLongToDateString(value: Long) =
        android.text.format.DateFormat.format(dateFormat, value)

    fun convertLongToDate(value: Long): GregorianCalendar {
        val calendar = GregorianCalendar()
        calendar.time = Date(value)
        return calendar
    }

    private fun deleteTodo(todo: Todo) = viewModelScope.launch {
        todoDao.delete(todo)
        navigateBackAction()
    }

    private fun navigateBackAction() = viewModelScope.launch {
        editTodoChannel.send(EditTodoEvents.NavigateBack)
    }

    private fun showInvalidInputMessage(message: String) = viewModelScope.launch {
        editTodoChannel.send(EditTodoEvents.ShowInvalidInputMessage(message))
    }

    private fun updateTodo(todo: Todo) = viewModelScope.launch {
        todoDao.update(todo)
        navigateBackAction()
    }

    private fun createTodo(todo: Todo) = viewModelScope.launch {
        todoDao.insert(todo)
        navigateBackAction()
    }

    fun getLongDateFromValues(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = day
        return calendar.timeInMillis
    }

}