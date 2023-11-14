package com.example.mobdevemco.viewholder

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.EntryItemBinding
import androidx.recyclerview.widget.SnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.mobdevemco.adapter.EntryImageAdapter
import com.example.mobdevemco.activity.SnapOnScrollListener
import com.example.mobdevemco.model.Entry

class EntryViewHolder(private val viewBinding: EntryItemBinding): RecyclerView.ViewHolder(viewBinding.root) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var entryImageAdapter: EntryImageAdapter
    fun bindData(entry: Entry) {
        this.viewBinding.entryTitle.text = entry.title
        this.viewBinding.entryLocationName.text = entry.locationName
        // TODO: images for recyclerView
        this.viewBinding.entryDescription.text = entry.description
        this.viewBinding.entryCreatedAt.text = entry.createdAt.toStringFormatted()
        recyclerView = viewBinding.entryImageRecyclerView
        entryImageAdapter = EntryImageAdapter(entry.images)
        recyclerView.adapter = entryImageAdapter
        val linearLayoutManager = LinearLayoutManager(itemView.context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = linearLayoutManager

        //this.viewBinding.entroImage.setImageResource(entry.imageId)
        val snapHelper: SnapHelper = PagerSnapHelper()
        recyclerView.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(recyclerView)

        recyclerView.addOnScrollListener(SnapOnScrollListener(snapHelper, SnapOnScrollListener.NOTIFY_ON_SCROLL) { position ->
            val curr_pos = position + 1
            this.viewBinding.currentImg.text = curr_pos.toString().replace(" ", "")
        })

        this.viewBinding.totalImg.text = entry.images.size.toString()

    }

//    fun setDeleteOnClickListener(onClickListener: View.OnClickListener){
//        this.viewBinding.deletebtn.setOnClickListener(onClickListener)
//    }

}