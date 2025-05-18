package com.hw_android.taskmanager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class TasksRepository(private val db: SQLiteDatabase) {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun loadTasks(): ArrayList<Task> {
        val tasks = ArrayList<Task>()
        val cursor: Cursor = db.query("tasks", null, null, null, null, null, null)

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val idString = it.getString(it.getColumnIndexOrThrow("id"))
                    val id = UUID.fromString(idString)

                    val title = it.getString(it.getColumnIndexOrThrow("title"))
                    val description = it.getString(it.getColumnIndexOrThrow("description"))
                    val deadlineString = it.getString(it.getColumnIndexOrThrow("deadline"))
                    val deadline = deadlineString.let { ds -> LocalDateTime.parse(ds, formatter) }
                    val urgency = it.getInt(it.getColumnIndexOrThrow("urgency"))
                    val tagsString = it.getString(it.getColumnIndexOrThrow("tags"))
                    val tags = tagsString?.split(",")?.map { tag -> tag.trim() }?.toSet() ?: emptySet()

                    val task = Task(id, title, description, deadline, urgency, tags)
                    tasks.add(task)
                } while (it.moveToNext())
            }
        }

        return tasks
    }

    fun saveTasks(tasks: List<Task>) {
        db.beginTransaction()
        try {
            db.delete("tasks", null, null) // очистить таблицу перед записью

            for (task in tasks) {
                val values = ContentValues().apply {
                    put("id", task.id.toString())
                    put("title", task.title)
                    put("description", task.description)
                    put("deadline", task.deadline.format(formatter))
                    put("urgency", task.urgency)
                    put("tags", task.tags.joinToString(","))
                }
                db.insert("tasks", null, values)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}

class TasksDbHelper(context: Context) : SQLiteOpenHelper(context, "tasks.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE tasks (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                description TEXT,
                deadline TEXT,
                urgency INTEGER,
                tags TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //
    }
}