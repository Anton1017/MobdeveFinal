package com.example.mobdevemco

import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.EntryItemBinding

class EntryViewHolder(private val viewBinding: EntryItemBinding): RecyclerView.ViewHolder(viewBinding.root) {
    fun bindData(entry: Entry) {
        this.viewBinding.entryTitle.text = entry.title
        this.viewBinding.entryLocationName.text = entry.locationName
        // TODO: images for recyclerView
        this.viewBinding.entryDescription.text = entry.description
        this.viewBinding.entryCreatedAt.text = entry.createdAt.toStringFull()
    }
}