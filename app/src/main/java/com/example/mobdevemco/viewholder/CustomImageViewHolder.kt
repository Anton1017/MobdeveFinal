package com.example.mobdevemco.viewholder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.mobdevemco.databinding.EntryImageItemBinding
import com.example.mobdevemco.model.EntryImages

abstract class CustomImageViewHolder(private val viewBinding: ViewBinding): RecyclerView.ViewHolder(viewBinding.root) {
    abstract fun bindData(entryImages: EntryImages)
}