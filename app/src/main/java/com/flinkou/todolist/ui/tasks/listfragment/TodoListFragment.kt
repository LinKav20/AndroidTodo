package com.flinkou.todolist.ui.tasks.listfragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flinkou.todolist.R
import com.flinkou.todolist.data.Todo
import com.flinkou.todolist.databinding.ListTodoFragmentBinding
import com.flinkou.todolist.ui.recycler.OnItemClickedListener
import com.flinkou.todolist.ui.recycler.TodoAdapter
import com.flinkou.todolist.ui.tasks.events.TodoEvents
import com.flinkou.todolist.utils.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.list_todo_fragment.*
import kotlinx.android.synthetic.main.todo_item.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TodoListFragment : Fragment(R.layout.list_todo_fragment), OnItemClickedListener {

    private val viewModel: TodoViewModel by viewModels()
    private lateinit var binding: ListTodoFragmentBinding
    private lateinit var todoAdapter: TodoAdapter
    private var clickedFilterButton = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ListTodoFragmentBinding.bind(view)

        initRecyclerView()
        initButtonShowItems()
        initSwipeCallback()
        initSnackBar()
        initEvents()
    }

    private fun initRecyclerView() {
        todoAdapter = TodoAdapter(this)

        with(binding) {
            recyclerView.apply {
                adapter = todoAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        viewModel.todos.observe(viewLifecycleOwner) {
            todoAdapter.submitList(it)
        }

        viewModel.doneTodosCount.observe(viewLifecycleOwner){
            done_count_text_view.text = "Done - ${it.size.toString()}"
        }
    }

    private fun initButtonShowItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            clickedFilterButton = viewModel.preferencesFLow.first().hideCompleted
            updateFilterSrc()
        }

        binding.itemsFilterBtn.setOnClickListener {
            clickedFilterButton = !clickedFilterButton
            viewModel.onUpdateHideTodos(clickedFilterButton)
            updateFilterSrc()
        }
    }

    private fun updateFilterSrc() {
        with(binding) {
            if (clickedFilterButton) itemsFilterBtn.setImageResource(R.drawable.ic_baseline_remove_red_eye_gray)
            else itemsFilterBtn.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
        }
    }

    private fun initSwipeCallback() {
        binding.apply {
            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    TODO("Not yet implemented")
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val todo = todoAdapter.currentList[viewHolder.adapterPosition]
                    when (direction) {
                        ItemTouchHelper.RIGHT -> viewModel.onTodoSwipedRight(todo)
                        ItemTouchHelper.LEFT -> viewModel.onTodoSwipedRight(todo)

                    }
                }
            }).attachToRecyclerView(recyclerView)
        }
    }

    private fun initSnackBar() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.todoEvents.collect { event ->
                when (event) {
                    is TodoEvents.ShowUndoTodoDeleteMessage -> snackbarShowUndoTodoDeleteMessage(
                        event.todo
                    )
                    is TodoEvents.NavigateToEditTaskScreen -> navigateToEditScreen(event.todo)
                    is TodoEvents.NavigateToAddTaskScreen -> navigateToAddScreen()
                }.exhaustive
            }
        }
    }

    private fun navigateToEditScreen(todo: Todo) {
        val action = TodoListFragmentDirections.actionTasksFragmentToEditTodoFragment(todo)
        findNavController().navigate(action)
    }

    private fun navigateToAddScreen() {
        val action = TodoListFragmentDirections.actionTasksFragmentToEditTodoFragment()
        findNavController().navigate(action)
    }

    private fun snackbarShowUndoTodoDeleteMessage(todo: Todo) {
        Snackbar.make(requireView(), "todo deleted!", Snackbar.LENGTH_LONG)
            .setAction("undo") {
                viewModel.onUndoDeleteClick(todo)
            }.show()
    }

    private fun initEvents() {
        with(binding) {
            editTaskButton.setOnClickListener {
                viewModel.onAddNewTodoClick()
            }
        }
    }

    override fun onItemClick(todo: Todo) {
        viewModel.onTodoSelected(todo)
    }

    override fun onCheckBoxClick(todo: Todo, isChecked: Boolean) {
        viewModel.onTodoStateChanged(todo, isChecked)
    }
}