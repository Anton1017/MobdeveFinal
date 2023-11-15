package com.example.mobdevemco.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.EntryImageItemBinding
import com.example.mobdevemco.model.EntryImages
import com.example.mobdevemco.viewholder.EntryImageViewHolder


class EntryImageAdapter(private var data: ArrayList<EntryImages>) : RecyclerView.Adapter<EntryImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryImageViewHolder {
        // Initialize the ViewBinding of an item's layout
        val itemViewBinding: EntryImageItemBinding = EntryImageItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        val entryImageViewHolder = EntryImageViewHolder(itemViewBinding)

        return entryImageViewHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }
    override fun onBindViewHolder(holder: EntryImageViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    fun updateData(data: ArrayList<EntryImages>) {
        this.data = data
        notifyDataSetChanged()
        Log.d("TAG", "Images: " + data.toString())
    }
}