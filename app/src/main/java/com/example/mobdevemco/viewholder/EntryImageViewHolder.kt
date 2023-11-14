package com.example.mobdevemco.viewholder
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.EntryImageItemBinding
import com.example.mobdevemco.model.EntryImages


class EntryImageViewHolder(private val viewBinding: EntryImageItemBinding): RecyclerView.ViewHolder(viewBinding.root) {
    fun bindData(entryImages: EntryImages){
        this.viewBinding.imageEntry.setImageURI(entryImages.imageUri)
    }

}