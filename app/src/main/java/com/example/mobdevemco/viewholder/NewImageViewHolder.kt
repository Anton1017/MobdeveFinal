package com.example.mobdevemco.viewholder
import android.util.Log
import com.bumptech.glide.Glide
import com.example.mobdevemco.databinding.EntryNewImageItemBinding
import com.example.mobdevemco.model.EntryImages
import java.io.File


class NewImageViewHolder(private val viewBinding: EntryNewImageItemBinding): CustomImageViewHolder(viewBinding) {
    override fun bindData(entryImages: EntryImages) {
        val context = viewBinding.imageEntry.context
//        Glide.with(context).load(entryImages.getBitmapFromInputStream(context)).into(viewBinding.imageEntry)
//        Log.d("TAG",File(entryImages.getUri().toString()).absolutePath)
        this.viewBinding.imageEntry.setImageBitmap(entryImages.getBitmapFromInputStream(context))
    }

}