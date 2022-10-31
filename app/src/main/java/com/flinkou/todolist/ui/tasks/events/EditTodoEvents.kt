package com.flinkou.todolist.ui.tasks.events

sealed class EditTodoEvents {
    data class ShowInvalidInputMessage(val message: String) : EditTodoEvents()
    object NavigateBack : EditTodoEvents()
}
