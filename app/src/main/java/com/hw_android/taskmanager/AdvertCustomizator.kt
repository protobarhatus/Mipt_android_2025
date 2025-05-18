package com.hw_android.taskmanager

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.hw_android.taskmanager.TaskAdapter.NormalViewHolder

class AdvertViewHolder(val view: View) : TaskAdapter.TaskViewHolder(view) {
    val closeButton = view.findViewById<ImageButton>(R.id.closeAdButton)
}

class AdvertCustomizator(val closeAdFunc: ()-> Unit): CustomViewHolderCustomizer {
    override fun createViewHolder(inflater: LayoutInflater, parent:  android. view. ViewGroup): TaskAdapter.TaskViewHolder {
        val view = inflater.inflate(R.layout.advert_view_holder, parent, false)
        return AdvertViewHolder(view)
    }

    override fun customizeViewHolder(p: TaskAdapter.TaskViewHolder) {
        (p as AdvertViewHolder).closeButton.setOnClickListener {
            closeAdFunc.invoke()
        }
    }
}