package com.example.mobdevemco.viewholder

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.mobdevemco.activity.SnapOnScrollListener
import com.example.mobdevemco.adapter.EntryImageAdapter
import com.example.mobdevemco.databinding.EntryItemBinding
import com.example.mobdevemco.model.Entry

class EntryViewHolder(private val viewBinding: EntryItemBinding): RecyclerView.ViewHolder(viewBinding.root) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var entryImageAdapter: EntryImageAdapter
    fun bindData(entry: Entry) {
        this.viewBinding.entryTitle.text = entry.getTitle()
        this.viewBinding.entryLocationName.text = entry.getLocationName()
        // TODO: images for recyclerView
        this.viewBinding.entryDescription.text = entry.getDescription()
        this.viewBinding.entryCreatedAt.text = entry.getCreatedAt().toStringFormatted()
        recyclerView = viewBinding.entryImageRecyclerView

        Log.d("TAG", entry.getImages().toString())

        entryImageAdapter = EntryImageAdapter(entry.getImages())
        //REQUIRED: SET VIEW TYPE BEFORE ASSIGNING TO ADAPTER
        entryImageAdapter.setViewType(EntryImageAdapter.ENTRY_IMAGE_LAYOUT)

        recyclerView.adapter = entryImageAdapter

        // For removing image recycler view if no images are present
        // Might keep recyclerview disabled after editing entry with images
        if(entry.getImages().size == 0){
            recyclerView.visibility = View.GONE
            viewBinding.imageCountView.visibility = View.GONE
        }


        val linearLayoutManager = LinearLayoutManager(itemView.context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = linearLayoutManager

        //this.viewBinding.entryImage.setImageResource(entry.imageId)
        val snapHelper: SnapHelper = PagerSnapHelper()
        recyclerView.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(recyclerView)

        recyclerView.addOnScrollListener(SnapOnScrollListener(snapHelper, SnapOnScrollListener.NOTIFY_ON_SCROLL) { position ->
            val curr_pos = position + 1
            this.viewBinding.currentImg.text = curr_pos.toString().replace(" ", "")
        })

        this.viewBinding.totalImg.text = entry.getImages().size.toString()

    }

//    fun setDeleteOnClickListener(onClickListener: View.OnClickListener){
//        this.viewBinding.deletebtn.setOnClickListener(onClickListener)
//    }

}