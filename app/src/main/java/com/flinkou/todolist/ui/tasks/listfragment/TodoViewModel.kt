package com.flinkou.todolist.ui.tasks.listfragment

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.flinkou.todolist.data.Todo
import com.flinkou.todolist.data.TodoDao
import com.flinkou.todolist.data.preferences.PreferencesManager
import com.flinkou.todolist.data.preferences.UserPreferences
import com.flinkou.todolist.ui.tasks.events.TodoEvents
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TodoViewModel @ViewModelInject constructor(
    private val todoDao: TodoDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val todoEventsChannel = Channel<TodoEvents>()
    val todoEvents = todoEventsChannel.receiveAsFlow()

    val preferencesFLow = preferencesManager.preferencesFlow

    private val todoFlow = preferencesFLow.flatMapLatest { userPreferences ->
        todoDao.getTasks(userPreferences.hideCompleted)
    }

    fun onUpdateHideTodos(state: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(state)
    }

    val todos = todoFlow.asLiveData()

    private val doneCount = MutableStateFlow<Int>(0)
    private val doneFlow = doneCount.flatMapLatest {
        todoDao.getCompletedTasks()
    }

    val doneTodosCount = doneFlow.asLiveData()

    fun onTodoSelected(todo: Todo) =viewModelScope.launch{
        todoEventsChannel.send(TodoEvents.NavigateToEditTaskScreen(todo))
    }

    fun onTodoStateChanged(todo: Todo, state: Boolean) = viewModelScope.launch {
        todoDao.update(todo.copy(done = state))
    }

    fun onTodoSwipedRight(todo: Todo) = viewModelScope.launch {
        todoDao.delete(todo)
        todoEventsChannel.send(TodoEvents.ShowUndoTodoDeleteMessage(todo))
    }

    fun onUndoDeleteClick(todo: Todo) = viewModelScope.launch {
        todoDao.insert(todo)
    }

    fun onAddNewTodoClick() = viewModelScope.launch {
        todoEventsChannel.send(TodoEvents.NavigateToAddTaskScreen)
    }

}