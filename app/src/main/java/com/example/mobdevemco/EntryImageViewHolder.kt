package com.example.mobdevemco
import android.view.View
import android.content.DialogInterface.OnClickListener
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.EntryImageItemBinding


class EntryImageViewHolder(private val viewBinding: EntryImageItemBinding): RecyclerView.ViewHolder(viewBinding.root) {
    fun bindData(entryImages: EntryImages){
        this.viewBinding.imageEntry.setImageResource(entryImages.imageId)
    }

}