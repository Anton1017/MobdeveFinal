package com.example.mobdevemco.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.mobdevemco.adapter.EntryImageAdapter
import com.example.mobdevemco.databinding.ActivityViewEntryBinding
import com.example.mobdevemco.helper.EntryDbHelper
import com.example.mobdevemco.model.CustomDateTime
import com.example.mobdevemco.model.Entry
import com.example.mobdevemco.model.EntryImages
import java.util.concurrent.Executors

class EntryDetailsActivity: AppCompatActivity() {
    //private var entryDataImages = DataGenerator.loadEntryImageData()

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: EntryImageAdapter
    private lateinit var entry: Entry

    private var entryDbHelper: EntryDbHelper? = null
    private val executorService = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding : ActivityViewEntryBinding = ActivityViewEntryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val intent: Intent = intent
        executorService.execute {
            entryDbHelper = EntryDbHelper.getInstance(this@EntryDetailsActivity)
            val e = entryDbHelper?.getEntry(intent.getLongExtra(Entry.ID, -1))
            runOnUiThread {
                if (e != null) {
                    this.entry = e



                    viewBinding.entryTitle.text = entry.getTitle()
                    viewBinding.entryCreatedAt.text = entry.getCreatedAt().toStringFormatted()
                    viewBinding.entryLocationName.text = entry.getLocationName()
                    viewBinding.entryDescription.text = entry.getDescription()


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
                    this.myAdapter = EntryImageAdapter(entry.getImages())
                    this.myAdapter.setViewType(EntryImageAdapter.ENTRY_IMAGE_LAYOUT)
                    this.recyclerView.adapter = myAdapter
                    val linearLayoutManager = LinearLayoutManager(this)
                    linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    recyclerView.layoutManager = linearLayoutManager

                    //this.viewBinding.entryImage.setImageResource(entry.imageId)
                    val snapHelper: SnapHelper = PagerSnapHelper()
                    recyclerView.setOnFlingListener(null);
                    snapHelper.attachToRecyclerView(recyclerView)
                    recyclerView.addOnScrollListener(SnapOnScrollListener(snapHelper, SnapOnScrollListener.NOTIFY_ON_SCROLL) { position ->
                        val curr_pos = position + 1
                        viewBinding.currentImg.text = curr_pos.toString().replace(" ", "")
                    })
                    viewBinding.totalImg.text = entry.getImages().size.toString()

                    if(entry.getImages().size == 0){
                        recyclerView.visibility = View.GONE
                        viewBinding.imageCountView.visibility = View.GONE
                    }
                }
            }
        }



    }

}