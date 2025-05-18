package com.hw_android.taskmanager

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class ActualTasksFragment : Fragment() {
    private lateinit var filtersViewModel: FiltersVM
    private lateinit var filtersAdapter: FiltersRecyclerViewAdapter

    private lateinit var viewModel: TaskViewModel
    private lateinit var todaysTasksAdapter: TaskAdapter
    private lateinit var tommorowsTasksAdapter: TaskAdapter

    companion object {
        fun newInstance() = ActualTasksFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_actual_tasks, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.filters_list)
        val buttonTexts = resources.getStringArray(R.array.filters_array).toList()

        filtersViewModel = FiltersVM(buttonTexts)



        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        filtersAdapter = FiltersRecyclerViewAdapter(filtersViewModel.filters.value!!, filtersViewModel::toggleFilter, 1)
        recyclerView.adapter = filtersAdapter
        filtersViewModel.filters.observe ( viewLifecycleOwner) {
                filters -> filtersAdapter.updateFilters(filters)
        }


        viewModel = ViewModelProvider(requireActivity())[TaskViewModel::class.java]

        val todaysTasksRecyclerView = view.findViewById<RecyclerView>(R.id.todaysTasks)
        val tomorrowsTasksRecyclerView = view.findViewById<RecyclerView>(R.id.tomorrowsTasks)
        todaysTasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        tomorrowsTasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        todaysTasksAdapter = TaskAdapter(emptyList(), ArrayList<Filter>(), { task ->
            viewModel.markTaskDone(task.id)
        }, TextPlainsCustomizator(getString(R.string.todays_tasks))){
            (activity as MainActivity ).redactTask(it)
        }
        todaysTasksAdapter.special_view_index = 0
        tommorowsTasksAdapter = TaskAdapter(emptyList(), ArrayList<Filter>(), { task ->
            viewModel.markTaskDone(task.id)
        }, TextPlainsCustomizator(getString(R.string.tomorrows_tasks))){
            (activity as MainActivity ).redactTask(it)
        }
        tommorowsTasksAdapter.special_view_index = 0

        todaysTasksRecyclerView.adapter = todaysTasksAdapter
        tomorrowsTasksRecyclerView.adapter = tommorowsTasksAdapter

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            todaysTasksAdapter.updateTasks(tasks)
            tommorowsTasksAdapter.updateTasks(tasks)
        }
        filtersViewModel.filters.observe(viewLifecycleOwner) {
            todaysTasksAdapter.updateFilters(it)
            tommorowsTasksAdapter.updateFilters(it)
        }
        viewModel.settings.observe (viewLifecycleOwner) { settings ->
            toggleFiltersSettings(viewModel.settings.value[TaskViewModel.Settings.NO_FILTERS] ?: false, view)
            todaysTasksAdapter.sortByUrgency = viewModel.settings.value[TaskViewModel.Settings.SORTING] ?: false
            tommorowsTasksAdapter.sortByUrgency = viewModel.settings.value[TaskViewModel.Settings.SORTING] ?: false
        }

        todaysTasksAdapter.textForNoTasks = getString(R.string.no_tasks)
        tommorowsTasksAdapter.textForNoTasks = getString(R.string.no_tasks)
        addCustomFilters(view)
        return view
    }
    fun toggleFiltersSettings(enable: Boolean, view: View) {
        val filtersList = view.findViewById<RecyclerView>(R.id.filters_list)
        val midView = view.findViewById<View>(R.id.midView)
        val textView2 = view.findViewById<View>(R.id.textView2)

        val parent = midView.parent as ConstraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(parent)

        if (enable) {
            //filtersList.visibility = View.GONE
            filtersAdapter.hide = true
            // plannedFilterButton топ привязать к bottomOf textView2
            constraintSet.connect(
                midView.id,
                ConstraintSet.TOP,
                textView2.id,
                ConstraintSet.BOTTOM
            )
        } else {
            //filtersList.visibility = View.VISIBLE
            filtersAdapter.hide = false
            // plannedFilterButton топ обратно к bottomOf filters_list
            constraintSet.connect(
                midView.id,
                ConstraintSet.TOP,
                filtersList.id,
                ConstraintSet.BOTTOM
            )
        }

        constraintSet.applyTo(parent)
    }

    fun addCustomFilters(view: View) {
        val filter: Filter = Filter("", true)
        filter.nonTagFilterAction = { !it.isDone}
        filtersViewModel.addFilter(filter)

        val todayFilter: Filter = Filter("", true)
        todayFilter.nonTagFilterAction = { it.deadline.toLocalDate() == LocalDate.now()}
        todaysTasksAdapter.addUniqueFilter(todayFilter)

        val tomorrowFilter: Filter = Filter("", true)
        tomorrowFilter.nonTagFilterAction = {
            it.deadline.toLocalDate() == LocalDate.now().plusDays(1)
        }
        tommorowsTasksAdapter.addUniqueFilter(tomorrowFilter)
    }
}