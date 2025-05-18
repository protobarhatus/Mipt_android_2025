package com.hw_android.taskmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID

interface CustomViewHolderCustomizer {
    fun createViewHolder(i: LayoutInflater, parent:  android.view.ViewGroup): TaskAdapter.TaskViewHolder
    fun customizeViewHolder(p:TaskAdapter.TaskViewHolder): Unit
}

class TaskAdapter(
    private var tasks: List<Task>,private var filters: ArrayList<Filter>,
    private val onMarkDoneClick: (Task) -> Unit,
    private val customizer: CustomViewHolderCustomizer,
    private val onTaskClick: (UUID)->Unit

) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    open class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
    private var uniqueFilters : ArrayList<Filter> = ArrayList<Filter>()

    var textForNoTasks: String? = null

    fun addUniqueFilter(filter: Filter) {
        uniqueFilters.add(filter)
        filters.add(filter)
        notifyDataSetChanged()
    }

    var sortByUrgency : Boolean = false
        set(value) {
            field = value
            updateFilters(filters)
        }

    class NormalViewHolder(view: View): TaskViewHolder(view) {
        val title: TextView = view.findViewById(R.id.taskTitle)
        val markDoneButton: ImageButton = view.findViewById(R.id.markDoneButton)
    }

    private var filteredTasks : ArrayList<Task> = arrayListOf<Task>()
    var special_view_index = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_SPECIAL = 1
    }
    override fun getItemViewType(position: Int): Int {
        return if (position == special_view_index) TYPE_SPECIAL else TYPE_NORMAL
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_NORMAL) {
            val view = inflater.inflate(R.layout.task_view_holder, parent, false)
            NormalViewHolder(view)
        } else {
            customizer.createViewHolder(inflater, parent)
        }
    }

    private fun hideButtonOnHolder(holder: NormalViewHolder) {
        holder.markDoneButton.visibility = View.GONE

        val constraintSet = ConstraintSet()
        var constraint = holder.itemView.findViewById<ConstraintLayout>(R.id.taskConstraint)
        constraintSet.clone(constraint)
        constraintSet.clear(R.id.taskTitle, ConstraintSet.END)
        constraintSet.connect(
            R.id.taskTitle,
            ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END,
            8
        )
        constraintSet.applyTo(constraint)
    }

    override fun onBindViewHolder(iholder: TaskViewHolder, iposition: Int) {

        var position = iposition
        if (special_view_index >= 0 && special_view_index <= filteredTasks.size)
        {
            if (position == special_view_index)
            {
                customizer.customizeViewHolder(iholder)
                return
            }
            if (position > special_view_index)
                position -= 1
        }
        if (filteredTasks.size == 0 && textForNoTasks != null) {
            val holder = iholder as NormalViewHolder
            holder.title.text = textForNoTasks
            holder.title.isClickable = false
            hideButtonOnHolder(holder)
            return
        }
        val task = filteredTasks[position]

        val holder = iholder as NormalViewHolder
        holder.title.text = task.title
        holder.title.isClickable = true
        holder.title.setOnClickListener { onTaskClick(filteredTasks[position].id) }
        if (task.isDone) {
            hideButtonOnHolder(holder)
        } else {
            holder.markDoneButton.visibility = View.VISIBLE

            holder.markDoneButton.setOnClickListener {
                onMarkDoneClick(task)
            }

            val constraintSet = ConstraintSet()
            var constraint = holder.itemView.findViewById<ConstraintLayout>(R.id.taskConstraint)
            constraintSet.clone(constraint)
            constraintSet.clear(R.id.taskTitle, ConstraintSet.END)
            constraintSet.connect(
                R.id.taskTitle,
                ConstraintSet.END,
                R.id.markDoneButton,
                ConstraintSet.START,
                8
            )
            constraintSet.applyTo(constraint)
        }
    }

    override fun getItemCount() = filteredTasks.size + (if (special_view_index < 0 || special_view_index > filteredTasks.size) 0 else 1) + (if (filteredTasks.size == 0 && textForNoTasks != null) 1 else 0)

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        updateFilters(filters)
        //notifyDataSetChanged()
    }

    fun updateFilters(newFilters: ArrayList<Filter>) {
        filters = newFilters.clone() as ArrayList<Filter>
        filters.addAll(uniqueFilters)
        filteredTasks.clear()
        tasks.forEach {
            var missAny = false
            filters.forEach { filter->
                if (filter.enacted && !filter.check(it))
                    missAny = true
            }
            if (!missAny)
                filteredTasks.add(it)
        }
        if (sortByUrgency)
            filteredTasks.sortWith { t1, t2-> t2.urgency.compareTo(t1.urgency) }
        notifyDataSetChanged()

    }
}