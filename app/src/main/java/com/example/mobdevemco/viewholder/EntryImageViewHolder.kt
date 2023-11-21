package com.example.mobdevemco.viewholder
import android.util.Log
import com.bumptech.glide.Glide
import com.example.mobdevemco.databinding.EntryImageItemBinding
import com.example.mobdevemco.model.EntryImages
import java.io.File


class EntryImageViewHolder(private val viewBinding: EntryImageItemBinding): CustomImageViewHolder(viewBinding) {
    override fun bindData(entryImages: EntryImages) {
        val context = viewBinding.imageEntry.context
        Glide.with(viewBinding.imageEntry.context).load(entryImages.getBitmapFromInputStream(context)).into(viewBinding.imageEntry)
    }

}