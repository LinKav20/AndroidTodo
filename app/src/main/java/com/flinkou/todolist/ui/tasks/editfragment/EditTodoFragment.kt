package com.flinkou.todolist.ui.tasks.editfragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flinkou.todolist.R
import com.flinkou.todolist.databinding.EditTodoFragmentBinding
import com.flinkou.todolist.ui.tasks.events.EditTodoEvents
import com.flinkou.todolist.utils.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.edit_todo_fragment.*
import kotlinx.coroutines.flow.collect
import java.text.DateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.milliseconds

@AndroidEntryPoint
class EditTodoFragment : Fragment(R.layout.edit_todo_fragment) {

    private val viewModel: EditTodoViewModel by viewModels()
    private lateinit var binding: EditTodoFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EditTodoFragmentBinding.bind(view)

        initFields()
        initNavigation()
        initEvents()
    }

    private fun initEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.editTodoEvent.collect { event ->
                when (event) {
                    is EditTodoEvents.NavigateBack -> navigateBack()
                    is EditTodoEvents.ShowInvalidInputMessage -> showSnackbar(event.message)
                }.exhaustive
            }
        }
    }

    private fun navigateBack() {
        binding.edittextTodo.clearFocus()
        findNavController().popBackStack()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun initNavigation() {
        binding.saveButton.setOnClickListener {
            viewModel.todoImportance = binding.spinner.selectedItem.toString().toLowerCase()
            viewModel.onSaveClick()
        }

        binding.closeButton.setOnClickListener {
            viewModel.onCloseClick()
        }

        binding.deleteButton.setOnClickListener {
            viewModel.onDeleteClick()
        }
    }

    private fun initFields() {
        setCalendar()
        setSpinner()
        setSwitch()

        with(binding) {
            edittextTodo.setText(viewModel.todoText)

            edittextTodo.addTextChangedListener {
                viewModel.todoText = it.toString()
            }


            if (viewModel.todo == null) {
                deleteButton.isVisible = false
            }

        }
    }

    private fun setSpinner() {
        with(binding) {
            context?.let {
                ArrayAdapter.createFromResource(
                    it,
                    R.array.importance_array,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                    spinner.setSelection(1)
                }
            }

            when (viewModel.todoImportance) {
                "important" -> spinner.setSelection(2)
                "low" -> spinner.setSelection(0)
                else -> spinner.setSelection(1)
            }

        }
    }

    private fun setSwitch() {
        with(binding) {
            switcher.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    calendar.visibility = View.VISIBLE
                    viewModel.todoDeadline =
                        viewModel.convertStringToLongDate(calendar.text.toString())
                } else {
                    calendar.visibility = View.INVISIBLE
                    viewModel.todoDeadline = null
                }
            }
        }
    }

    private fun setCalendar() {
        with(binding) {
            if (viewModel.todoDeadline != null) {
                calendar.text = viewModel.convertLongToDateString(viewModel.todoDeadline!!)
                calendar.visibility = View.VISIBLE
                switcher.isChecked = true
            } else {
                calendar.text = viewModel.convertLongToDateString(System.currentTimeMillis())
            }

            calendar.setOnClickListener {
                val date = viewModel.convertLongToDate(
                    viewModel.todoDeadline ?: System.currentTimeMillis()
                )

                val datePickerDialog = context?.let { ctx ->
                    DatePickerDialog(
                        ctx,
                        { _, myYear, myMonth, myDayOfMonth ->
                            choseDayInCalendar(myYear, myMonth, myDayOfMonth)
                        },
                        date.get(Calendar.YEAR),
                        date.get(Calendar.MONTH),
                        date.get(Calendar.DAY_OF_MONTH)
                    )
                }
                datePickerDialog?.show()
            }
        }
    }

    private fun choseDayInCalendar(yearD: Int, monthD: Int, dayD: Int) {
        val newDate = viewModel.getLongDateFromValues(yearD, monthD, dayD)

        if (newDate >= System.currentTimeMillis()) {
            viewModel.todoDeadline = newDate
            binding.calendar.text = viewModel.convertLongToDateString(newDate)
        } else {
            Toast.makeText(
                context,
                "Deadline cannot be earlier than now",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}