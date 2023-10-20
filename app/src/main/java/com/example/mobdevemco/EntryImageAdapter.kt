package com.example.mobdevemco
import android.content.Intent
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.EntryImageItemBinding
import androidx.appcompat.app.AlertDialog
class EntryImageAdapter(private val data: ArrayList<EntryImages>) : RecyclerView.Adapter<EntryImageViewHolder>() {
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
}