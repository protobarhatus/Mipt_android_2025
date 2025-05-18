package com.hw_android.taskmanager

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView

class TextPlainsViewHolder(val view: View) : TaskAdapter.TaskViewHolder(view) {
    val text = view.findViewById<TextView>(R.id.textPlainTextView)
}

class TextPlainsCustomizator(val text: String) : CustomViewHolderCustomizer {
    override fun createViewHolder(inflater: LayoutInflater, parent:  android. view. ViewGroup): TaskAdapter.TaskViewHolder {
        val view = inflater.inflate(R.layout.recycler_view_name_holder, parent, false)
        return TextPlainsViewHolder(view)

    }

    override fun customizeViewHolder(p: TaskAdapter.TaskViewHolder) {
        val holder = p as TextPlainsViewHolder
        holder.text.text = text
    }
}
