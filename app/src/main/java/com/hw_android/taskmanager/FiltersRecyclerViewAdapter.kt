package com.hw_android.taskmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.RecyclerView

class FiltersRecyclerViewAdapter(private var filters: ArrayList<Filter>,
                                 private val onClick: (Int) -> Unit,
                                 private var ignore_last_filters : Int = 0) :
    RecyclerView.Adapter<FiltersRecyclerViewAdapter.ButtonViewHolder>() {

    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.filters_button)
    }
    var hide: Boolean = false
        set(value) {field = value
        notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.button, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {

        holder.button.text = filters[position].tag
        holder.button.setBackgroundColor(
            if (filters[position].enacted)
                ContextCompat.getColor(holder.itemView.context, R.color.filter_enacted)
            else
                ContextCompat.getColor(holder.itemView.context, R.color.filter_not_enacted)
        )
        holder.button.setOnClickListener {
            onClick(position)
        }
    }



    override fun getItemCount(): Int = if (hide) 0 else filters.size - ignore_last_filters

    fun updateFilters(newFilters: ArrayList<Filter>) {
        filters = newFilters
        notifyDataSetChanged()
    }
}