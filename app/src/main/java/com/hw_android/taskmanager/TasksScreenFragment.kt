package com.hw_android.taskmanager

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID


class TasksScreenFragment() : Fragment() {

    private lateinit var filtersViewModel: FiltersVM
    private lateinit var filtersAdapter: FiltersRecyclerViewAdapter

    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter

    private lateinit var plannedFilterButton: Button
    private lateinit var doneFilterButton: Button

    companion object {
        fun newInstance() = TasksScreenFragment()
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    fun addCustomFilters(view: View) {
        var doneTasksFilter = Filter("", false)
        doneTasksFilter.nonTagFilterAction = { it.isDone }
        var plannedTasksFilter = Filter("", false)
        plannedTasksFilter.nonTagFilterAction = { !it.isDone}
        filtersViewModel.addFilter(doneTasksFilter)
        filtersViewModel.addFilter(plannedTasksFilter)

        plannedFilterButton = view.findViewById<Button>(R.id.plannedFilterButton)
        plannedFilterButton.setOnClickListener(::onPlannedFilterButtonTap)
        doneFilterButton =  view.findViewById<Button>(R.id.doneFilterButton)
        doneFilterButton.setOnClickListener (::onDoneFilterButtonTap)

        setColorForFilterButton(plannedFilterButton, filtersViewModel.filters.value!![filtersViewModel.filters.value!!.size - 1].enacted, view)
        setColorForFilterButton(doneFilterButton, filtersViewModel.filters.value!![filtersViewModel.filters.value!!.size - 2].enacted, view)

    }
    fun setColorForFilterButton(button: Button, enacted: Boolean, p: View) {
        button.setBackgroundColor(
            if (enacted)
                p.context.getColor(R.color.filter_enacted)
            else
                p.context.getColor(R.color.filter_not_enacted)

        )
    }

    fun onPlannedFilterButtonTap(p: View) {
        filtersViewModel.toggleFilter(filtersViewModel.filters.value!!.size - 1)

        setColorForFilterButton(plannedFilterButton, filtersViewModel.filters.value!![filtersViewModel.filters.value!!.size - 1].enacted, p)


    }
    fun onDoneFilterButtonTap(p: View) {
        filtersViewModel.toggleFilter(filtersViewModel.filters.value!!.size - 2)

        setColorForFilterButton(doneFilterButton, filtersViewModel.filters.value!![filtersViewModel.filters.value!!.size - 2].enacted, p)
    }
    private var adJob: Job? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_tasks_screen, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.filters_list)
        val buttonTexts = resources.getStringArray(R.array.filters_array).toList()

        filtersViewModel = FiltersVM(buttonTexts)
        addCustomFilters(view)


        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        filtersAdapter = FiltersRecyclerViewAdapter(filtersViewModel.filters.value!!, filtersViewModel::toggleFilter, 2)
        recyclerView.adapter = filtersAdapter
        filtersViewModel.filters.observe ( viewLifecycleOwner) {
            filters -> filtersAdapter.updateFilters(filters)
        }

        viewModel = ViewModelProvider(requireActivity())[TaskViewModel::class.java]

        val tasksRecyclerView = view.findViewById<RecyclerView>(R.id.taskRecyclerView)
        tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TaskAdapter(emptyList(), ArrayList<Filter>(), { task ->
            viewModel.markTaskDone(task.id)
        }, AdvertCustomizator({adapter.special_view_index = -1})){
            (activity as MainActivity ).redactTask(it)
        }

        tasksRecyclerView.adapter = adapter

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            adapter.updateTasks(tasks)
        }

        viewModel.settings.observe (viewLifecycleOwner) { settings ->
            toggleFiltersSettings(viewModel.settings.value[TaskViewModel.Settings.NO_FILTERS] ?: false, view)
            adapter.sortByUrgency = viewModel.settings.value[TaskViewModel.Settings.SORTING] ?: false
        }
        filtersViewModel.filters.observe(viewLifecycleOwner) { adapter.updateFilters(it) }

        view.findViewById<ImageButton>(R.id.addTaskButton).setOnClickListener { callNewTaskAddition() }

        adJob = lifecycleScope.launch {
            while (isActive) {
                adapter.special_view_index = (0..adapter.itemCount).random()
                delay(10_000)
                adapter.special_view_index = -1
                delay(50_000)
            }
        }

        return view
    }
    fun toggleFiltersSettings(enable: Boolean, view: View) {
        val filtersList = view.findViewById<RecyclerView>(R.id.filters_list)
        val plannedFilterButton = view.findViewById<View>(R.id.plannedFilterButton)
        val textView2 = view.findViewById<View>(R.id.textView2)

        val parent = plannedFilterButton.parent as ConstraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(parent)

        if (enable) {
            //filtersList.visibility = View.GONE
            filtersAdapter.hide = true
            // plannedFilterButton топ привязать к bottomOf textView2
            constraintSet.connect(
                plannedFilterButton.id,
                ConstraintSet.TOP,
                textView2.id,
                ConstraintSet.BOTTOM
            )
        } else {
            //filtersList.visibility = View.VISIBLE
            filtersAdapter.hide = false
            // plannedFilterButton топ обратно к bottomOf filters_list
            constraintSet.connect(
                plannedFilterButton.id,
                ConstraintSet.TOP,
                filtersList.id,
                ConstraintSet.BOTTOM
            )
        }

        constraintSet.applyTo(parent)
    }




    fun callNewTaskAddition() {
        (activity as MainActivity ).callTaskRedactor (Task(title = "", description = "", deadline = LocalDateTime.now(), urgency = 10, tags = setOf()), {
            viewModel.addTask(it)
        }){}
    }

}