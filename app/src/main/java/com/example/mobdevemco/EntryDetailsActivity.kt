package com.example.mobdevemco

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.mobdevemco.databinding.ActivityViewEntryBinding

class EntryDetailsActivity: AppCompatActivity() {
    private var entryDataImages = DataGenerator.loadEntryImageData()

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: EntryImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding : ActivityViewEntryBinding = ActivityViewEntryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val builder = AlertDialog.Builder(this)
        viewBinding.deletebtn.setOnClickListener(View.OnClickListener{
            builder.setTitle("Alert!")
                .setMessage("Are you sure you want to delete " + viewBinding.entryTitle.text)
                .setCancelable(true)
                .setPositiveButton("Yes"){dialogInterface,it ->
                    finish()
                }
                .setNegativeButton("No"){dialogInterface,it ->
                    dialogInterface.cancel()
                }
                .show()
        })
        viewBinding.backBtn.setOnClickListener(View.OnClickListener{
            finish()
        })

        viewBinding.editBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@EntryDetailsActivity, EntryEditActivity::class.java)
            this.startActivity(intent)
        })
        this.recyclerView = viewBinding.entryImageRecyclerView
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