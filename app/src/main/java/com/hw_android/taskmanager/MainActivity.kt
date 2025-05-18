package com.hw_android.taskmanager

import android.content.pm.ActivityInfo
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime
import java.util.UUID

class MainActivity : AppCompatActivity() {

    val fragment_view : FragmentContainerView by lazy { findViewById<FragmentContainerView>(R.id.fragmentContainerView)}

    val tasks_screen_fragment : TasksScreenFragment by lazy { TasksScreenFragment() }
    val actual_screen_fragment : ActualTasksFragment by lazy { ActualTasksFragment() }
    val settings_screen_fragment : SettingsFragment by lazy { SettingsFragment() }

    val viewModel : TaskViewModel by viewModels()


    private lateinit var dbHelper: TasksDbHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var tasksRepository: TasksRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = TasksDbHelper(this)
        db = dbHelper.writableDatabase


        tasksRepository = TasksRepository(db)


        var tasks = tasksRepository.loadTasks()
        if (tasks.isEmpty) {
            tasks = arrayListOf<Task>(
                Task(
                    title = "Сделать дизайн",
                    description = "Нужно сделать дизайн главной страницы",
                    deadline = LocalDateTime.now().plusDays(1),
                    urgency = 5,
                    tags = setOf("FILTER1", "FILTER3")
                ),
                Task(
                    title = "Созвон с командой",
                    description = "Обсудить прогресс по задаче",
                    deadline = LocalDateTime.now().plusHours(3),
                    urgency = 3,
                    tags = setOf("FILTER3", "FILTER2")
                )
            )
        }
        viewModel.setTasks(tasks)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    }

    override fun onStop() {
        super.onStop()

        tasksRepository.saveTasks(viewModel.tasks.value)
    }

    fun onListButtonTap( p: View) {
        if (fragment_view.getFragment<Fragment>() is TasksScreenFragment)
            return;
        supportFragmentManager.commit {
            replace(R.id.fragmentContainerView, tasks_screen_fragment)
            addToBackStack(null)
        }
    }
    fun onActualTasksTap( p: View) {
        if (fragment_view.getFragment<Fragment>() is ActualTasksFragment)
            return;
        supportFragmentManager.commit {
            replace(R.id.fragmentContainerView, actual_screen_fragment)
            addToBackStack(null)
        }
    }
    fun onSettingsTap( p: View) {
        if (fragment_view.getFragment<Fragment>() is SettingsFragment)
            return;
        supportFragmentManager.commit {
            replace(R.id.fragmentContainerView, settings_screen_fragment)
            addToBackStack(null)
        }
    }

    fun redactTask(id: UUID) {
        val index = viewModel.tasks.value?.indexOfFirst { it.id == id } ?: return

        callTaskRedactor (viewModel.tasks.value[index], {
            viewModel.updateTask(index, it)
        }, {
            viewModel.deleteTask(index)
        })
    }

    fun callTaskRedactor(task: Task, successCall: (Task)-> Unit, deleteCall: (Task)-> Unit) {
        val taskEditorFragment = RedactingScreenFragment()

        val bundle = Bundle().apply {
            putSerializable(getString(R.string.TASK_BUNDLE), task)
        }
        taskEditorFragment.arguments = bundle

        supportFragmentManager.setFragmentResultListener(getString(R.string.TASK_EDIT_RESULT), this) { _, result ->
            val wasSaved = result.getBoolean(getString(R.string.SAVE_RES_BUNDLE), false)
            val wasDeleted = result.getBoolean(getString(R.string.DELETE_RES_BUNDLE), false)
            if (wasDeleted)
                deleteCall.invoke(task)
            val updatedTask = result.getSerializable(getString(R.string.TASK_BUNDLE)) as? Task
            if (wasSaved && updatedTask != null) {
                successCall.invoke(updatedTask)
            }
        }


        taskEditorFragment.show(supportFragmentManager, "")
    }

    fun onAboutTap(p: View) {
        val aboutFragment = AboutPageFragment()
        aboutFragment.show(supportFragmentManager, "")
    }

}