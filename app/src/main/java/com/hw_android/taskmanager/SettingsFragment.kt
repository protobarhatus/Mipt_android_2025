package com.hw_android.taskmanager

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.activityViewModels

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view :View =  inflater.inflate(R.layout.fragment_settings, container, false)
        view.findViewById<CheckBox>(R.id.noFiltersCheckBox).let {
            it.isChecked = viewModel.settings.value[TaskViewModel.Settings.NO_FILTERS] ?: false
            it.setOnClickListener { it1->viewModel.changeFiltersSetting(it.isChecked) }
        }
        view.findViewById<CheckBox>(R.id.noAdCheckBox).let {
            it.isChecked = viewModel.settings.value[TaskViewModel.Settings.NO_AD] ?: false
            it.setOnClickListener {it1->viewModel.changeAdSetting(it.isChecked) }}
        view.findViewById<CheckBox>(R.id.sortCheckBox).let {
            it.isChecked = viewModel.settings.value[TaskViewModel.Settings.SORTING] ?: false
            it.setOnClickListener {it1->viewModel.changeSortSetting(it.isChecked) }}
        return view
    }
}