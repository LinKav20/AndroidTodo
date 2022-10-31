package com.flinkou.todolist.ui.recycler

import androidx.recyclerview.widget.DiffUtil
import com.flinkou.todolist.data.Todo

class DiffCallback : DiffUtil.ItemCallback<Todo>() {
    override fun areItemsTheSame(oldItem: Todo, newItem: Todo) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Todo, newItem: Todo) =
        oldItem == newItem

}