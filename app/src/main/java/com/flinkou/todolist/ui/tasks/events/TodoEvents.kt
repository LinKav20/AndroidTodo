package com.flinkou.todolist.ui.tasks.events

import com.flinkou.todolist.data.Todo

sealed class TodoEvents {
    object NavigateToAddTaskScreen : TodoEvents()
    data class NavigateToEditTaskScreen(val todo: Todo) : TodoEvents()
    data class ShowUndoTodoDeleteMessage(val todo: Todo) : TodoEvents()
}
