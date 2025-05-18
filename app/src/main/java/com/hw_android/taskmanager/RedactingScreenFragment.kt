package com.hw_android.taskmanager

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.transition.Visibility
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar


class RedactingScreenFragment : DialogFragment() {

    lateinit var task : Task

    lateinit var nameInputView : TextInputEditText
    lateinit var descriptionInputView : TextInputEditText
    lateinit var tagsInputView : TextInputEditText
    lateinit var urgencyInputView : TextInputEditText
    lateinit var deadlineButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        task = arguments?.getSerializable(getString(R.string.TASK_BUNDLE)) as Task

        val view = inflater.inflate(R.layout.fragment_redacting_screen, container, false) ?: return null
        nameInputView = view.findViewById<TextInputEditText>(R.id.nameInput)
        nameInputView.setText(task.title)
        descriptionInputView = view.findViewById<TextInputEditText>(R.id.desriptionInput)
        descriptionInputView.setText(task.description)
        tagsInputView = view.findViewById<TextInputEditText>(R.id.tagsInput);
        tagsInputView.setText(task.tags.joinToString(", "))
        urgencyInputView = view.findViewById<TextInputEditText>(R.id.urgencyInput);
        urgencyInputView.setText(task.urgency.toString())
        deadlineButton = view.findViewById<Button>(R.id.deadlineButton);
        deadlineButton.setText(task.deadline.toString())
        deadlineButton.setOnClickListener { chooseDateTime() }

        view.findViewById<Button>(R.id.saveButton).setOnClickListener { setResultAndClose() }
        view.findViewById<Button>(R.id.backButton).setOnClickListener { dismiss() }

        if (task.title == "") {
            view.findViewById<Button>(R.id.deleteButton).isEnabled = false
            view.findViewById<Button>(R.id.deleteButton).visibility = View.GONE
        }
        view.findViewById<Button>(R.id.deleteButton).setOnClickListener { deleteTaskAndClose() }
        return view

    }

    fun chooseDateTime() {
        val calendar = Calendar.getInstance()
        val saveResult = {calendar: Calendar->
            task = task.copy(deadline = LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()))
            deadlineButton.setText(task.deadline.toString())
        }

        DatePickerDialog(requireContext(), { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)


            TimePickerDialog(requireContext(), { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)


                saveResult(calendar)
            },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()

        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun setResultAndClose() {
        if (!nameInputView.text!!.contains(Regex("\\S")))
            return;
        task = task.copy(
            title = nameInputView.text.toString(),
            description = descriptionInputView.text.toString(),
            tags = tagsInputView.text.toString().split(", ").toSet(),
            urgency = urgencyInputView.text.toString().toInt()

        )
        val result = Bundle().apply {
            putBoolean(getString(R.string.SAVE_RES_BUNDLE), true)
            putSerializable(getString(R.string.TASK_BUNDLE), task)
        }
        parentFragmentManager.setFragmentResult(getString(R.string.TASK_EDIT_RESULT), result)
        dismiss()
    }
    fun deleteTaskAndClose() {
        val result = Bundle().apply {
            putBoolean(getString(R.string.DELETE_RES_BUNDLE), true)
        }
        parentFragmentManager.setFragmentResult(getString(R.string.TASK_EDIT_RESULT), result)
        dismiss()
    }
}