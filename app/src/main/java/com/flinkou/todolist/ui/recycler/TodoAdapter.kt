package com.flinkou.todolist.ui.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flinkou.todolist.R
import com.flinkou.todolist.data.Todo
import com.flinkou.todolist.databinding.TodoItemBinding

class TodoAdapter(
    private val listener: OnItemClickedListener
) : ListAdapter<Todo, TodoAdapter.TodoViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TodoItemBinding.inflate(inflater, parent, false)
        return TodoViewHolder(binding)
    }


    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TodoViewHolder(
        private val binding: TodoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                infoTodo.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position))
                    }
                }

                todoCheckbox.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCheckBoxClick(getItem(position), todoCheckbox.isChecked)
                    }
                }
            }
        }

        fun bind(todo: Todo) {
            with(binding) {
                todoTextview.text = todo.text
                todoTextview.paint.isStrikeThruText = todo.done
                todoCheckbox.isChecked = todo.done

                if (todo.importance == "important") {
                    importanceView.isVisible = true
                    importanceView.setImageResource(R.drawable.ic_baseline_priority_high_24)
                }

                if (todo.importance == "low") {
                    importanceView.isVisible = true
                    importanceView.setImageResource(R.drawable.ic_baseline_arrow_downward_24)
                }
            }
        }
    }
}