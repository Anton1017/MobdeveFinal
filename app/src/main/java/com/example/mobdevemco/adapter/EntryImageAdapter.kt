package com.example.mobdevemco.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.EntryImageItemBinding
import com.example.mobdevemco.databinding.EntryNewImageItemBinding
import com.example.mobdevemco.model.EntryImages
import com.example.mobdevemco.viewholder.CustomImageViewHolder
import com.example.mobdevemco.viewholder.EntryImageViewHolder
import com.example.mobdevemco.viewholder.NewImageViewHolder


class EntryImageAdapter(private var data: ArrayList<EntryImages>) : RecyclerView.Adapter<CustomImageViewHolder>() {
    private var viewType = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomImageViewHolder {
        // Initialize the ViewBinding of an item's layout


        val customImageViewHolder: CustomImageViewHolder
        return if(this.viewType == NEW_IMAGE_LAYOUT){
            val itemViewBinding: EntryNewImageItemBinding = EntryNewImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
            customImageViewHolder = NewImageViewHolder(itemViewBinding)
            customImageViewHolder
        }else if (this.viewType == ENTRY_IMAGE_LAYOUT){
            val itemViewBinding: EntryImageItemBinding = EntryImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
            customImageViewHolder = EntryImageViewHolder(itemViewBinding)
            customImageViewHolder
        }else{
            throw Throwable("ERROR: Invalid viewType>. Be sure to set the viewType in <EntryImageAdapter>.setViewType().")
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
    override fun onBindViewHolder(holder: CustomImageViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    fun updateData() {
        notifyDataSetChanged()
    }

    fun setViewType(viewType: Int){
        this.viewType = viewType
    }

    companion object {
        val NEW_IMAGE_LAYOUT = 1
        val ENTRY_IMAGE_LAYOUT = 2
    }
}