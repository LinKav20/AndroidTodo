package com.flinkou.todolist.ui.recycler

import com.flinkou.todolist.data.Todo

interface OnItemClickedListener {
    fun onItemClick(todo: Todo)
    fun onCheckBoxClick(todo: Todo, isChecked: Boolean)
}