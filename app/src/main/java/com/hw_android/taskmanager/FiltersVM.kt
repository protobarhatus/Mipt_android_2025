package com.hw_android.taskmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
typealias FilterAction = (Task)-> Boolean
data class Filter(val tag: String, var enacted: Boolean, var nonTagFilterAction : FilterAction? = null) {



    fun check(task: Task): Boolean {
        return nonTagFilterAction?.invoke(task) ?: task.tags.contains(tag)
    }

}
class FiltersVM {
    private val _filters = MutableLiveData<ArrayList<Filter>>()
    val filters: LiveData<ArrayList<Filter>> = _filters



    constructor(filts: List<String>)
    {
        _filters.value = ArrayList<Filter>()
        filts.forEach { _filters.value?.add(Filter(it, false));  }
    }

    fun toggleFilter(pos: Int)
    {
        if (pos < 0 || pos >= _filters.value!!.size)
            return
        val currentList = _filters.value ?: return
        val filter = currentList[pos]
        currentList[pos] = filter.copy(enacted = !filter.enacted)
        _filters.value = currentList
    }

    fun addFilter(filter: Filter) {
        _filters.value?.add(filter)
    }

}