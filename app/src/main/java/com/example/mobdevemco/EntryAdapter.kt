package com.example.mobdevemco

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.EntryItemBinding

class EntryAdapter(private val data: ArrayList<Entry>) : RecyclerView.Adapter<EntryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        // Initialize the ViewBinding of an item's layout
        val itemViewBinding: EntryItemBinding = EntryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        val entryViewHolder = EntryViewHolder(itemViewBinding)



        return entryViewHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bindData(data[position])
    }

}