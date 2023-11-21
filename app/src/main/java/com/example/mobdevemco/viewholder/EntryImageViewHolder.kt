package com.example.mobdevemco.viewholder
import android.util.Log
import com.bumptech.glide.Glide
import com.example.mobdevemco.databinding.EntryImageItemBinding
import com.example.mobdevemco.model.EntryImages
import java.io.File


class EntryImageViewHolder(private val viewBinding: EntryImageItemBinding): CustomImageViewHolder(viewBinding) {
    override fun bindData(entryImages: EntryImages) {
        Glide.with(viewBinding.imageEntry.context).load(File(entryImages.getUri().toString()).path).into(viewBinding.imageEntry)
        Log.d("TAG",File(entryImages.getUri().toString()).path)
        //this.viewBinding.imageEntry.setImageBitmap(entryImages.getBitmap())
    }

}