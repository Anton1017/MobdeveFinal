package com.example.mobdevemco

import android.view.View
import android.content.DialogInterface.OnClickListener
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.EntryItemBinding

class EntryViewHolder(private val viewBinding: EntryItemBinding): RecyclerView.ViewHolder(viewBinding.root) {
    fun bindData(entry: Entry) {
        this.viewBinding.entryTitle.text = entry.title
        this.viewBinding.entryLocationName.text = entry.locationName
        // TODO: images for recyclerView
        this.viewBinding.entryDescription.text = entry.description
        this.viewBinding.entryCreatedAt.text = entry.createdAt.toStringFull()
        //this.viewBinding.entroImage.setImageResource(entry.imageId)
    }

    fun setDeleteOnClickListener(onClickListener: View.OnClickListener){
        this.viewBinding.deletebtn.setOnClickListener(onClickListener)
    }

}