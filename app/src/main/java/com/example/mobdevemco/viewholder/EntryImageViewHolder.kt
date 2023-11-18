package com.example.mobdevemco.viewholder
import com.example.mobdevemco.databinding.EntryImageItemBinding
import com.example.mobdevemco.model.EntryImages


class EntryImageViewHolder(private val viewBinding: EntryImageItemBinding): CustomImageViewHolder(viewBinding) {
    override fun bindData(entryImages: EntryImages) {
        this.viewBinding.imageEntry.setImageBitmap(entryImages.getBitmap())
    }

}