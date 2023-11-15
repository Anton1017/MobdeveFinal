package com.example.mobdevemco.viewholder
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.EntryImageItemBinding
import com.example.mobdevemco.databinding.EntryNewImageItemBinding
import com.example.mobdevemco.model.EntryImages


class NewImageViewHolder(private val viewBinding: EntryNewImageItemBinding): CustomImageViewHolder(viewBinding) {
    override fun bindData(entryImages: EntryImages) {
        this.viewBinding.imageEntry.setImageURI(entryImages.getImageUri())
    }

}