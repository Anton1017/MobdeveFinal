package com.example.mobdevemco.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.mobdevemco.model.DataGenerator
import com.example.mobdevemco.adapter.EntryImageAdapter
import com.example.mobdevemco.databinding.ActivityEditEntryBinding

class EntryEditActivity : AppCompatActivity(){
    private var entryDataImages = DataGenerator.loadEntryImageData()

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: EntryImageAdapter
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val viewBinding : ActivityEditEntryBinding = ActivityEditEntryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.createBtn.setOnClickListener(View.OnClickListener {

            finish()
        })
        viewBinding.cancelBtn.setOnClickListener(View.OnClickListener {

            finish()
        })
        val galleryImage = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {

            })
        viewBinding.addImageBtn.setOnClickListener(View.OnClickListener {
            galleryImage.launch("image/*")
        })

        viewBinding.editLocationBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@EntryEditActivity, EntryMapActivity::class.java)
            this.startActivity(intent)
        })

        this.recyclerView = viewBinding.imageListRecyclerView
        this.myAdapter = EntryImageAdapter(entryDataImages)
        this.recyclerView.adapter = myAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = linearLayoutManager

        //this.viewBinding.entroImage.setImageResource(entry.imageId)
        val snapHelper: SnapHelper = PagerSnapHelper()
        recyclerView.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(recyclerView)
    }
}