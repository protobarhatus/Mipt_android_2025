package com.hw_android.taskmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

data class Task(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val deadline: LocalDateTime,
    val urgency: Int,
    val tags: Set<String>,
    var isDone: Boolean = false
) : java.io.Serializable

class TaskViewModel : ViewModel() {

    private val _tasks = MutableLiveData<ArrayList<Task>>()
    val tasks: LiveData<ArrayList<Task>> = _tasks
    enum class Settings {
        NO_FILTERS,
        NO_AD,
        SORTING
    }
    private val _settings = MutableLiveData<MutableMap<Settings, Boolean>>()
    val settings: LiveData<MutableMap<Settings, Boolean>> = _settings

    init {


        //_tasks.value =

        _settings.value = mutableMapOf<Settings, Boolean>(Settings.NO_FILTERS to false, Settings.NO_AD to false,
            Settings.SORTING to false)
    }

    fun setTasks(task: ArrayList<Task>) {
        _tasks.value = task.clone() as ArrayList<Task>

    }

    fun changeFiltersSetting(new_val: Boolean) {
        val c = _settings.value ?: return
        c[Settings.NO_FILTERS] = new_val
        _settings.value = c
    }
    fun changeAdSetting(new_val: Boolean) {
        val c = _settings.value ?: return
        c[Settings.NO_AD] = new_val
        _settings.value = c
    }
    fun changeSortSetting(new_val: Boolean) {
        val c = _settings.value ?: return
        c[Settings.SORTING] = new_val
        _settings.value = c
    }

    fun markTaskDone(taskId: UUID) {

        val c : ArrayList<Task> = _tasks.value ?: return
        c.forEach {
            if (it.id == taskId)
                it.isDone = true
        }
        _tasks.value = c

    }

    fun getTaskById(id: UUID): Task? {
        return _tasks.value?.find { it.id == id }
    }
    fun updateTask(index: Int, new_task : Task) {
        val c : ArrayList<Task> = _tasks.value ?: return
        c[index] = new_task
        _tasks.value = c
    }
    fun addTask(new_task: Task) {
        val c : ArrayList<Task> = _tasks.value ?: return
        c.add(new_task)
        _tasks.value = c
    }
    fun deleteTask(index: Int) {
        val c : ArrayList<Task> = _tasks.value ?: return
        c.removeAt(index)
        _tasks.value = c
    }


}