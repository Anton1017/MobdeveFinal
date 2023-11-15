package com.example.mobdevemco.activity


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.adapter.EntryAdapter
import com.example.mobdevemco.databinding.ActivityMainBinding
import com.example.mobdevemco.helper.EntryDbHelper
import com.example.mobdevemco.model.DataGenerator
import com.example.mobdevemco.model.Entry
import com.example.mobdevemco.model.EntryImages
import java.util.concurrent.Executors

class ActivityMain : AppCompatActivity(){

    private var entryData = DataGenerator.loadEntryData()

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: EntryAdapter


    private val newEntryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        // Check to see if the result returned is appropriate (i.e. OK)
        if (result.resultCode == RESULT_OK) {
            val title : String = result.data?.getStringExtra(NewEntryActivity.TITLE_KEY)!!
            val locationName : String = result.data?.getStringExtra(NewEntryActivity.LOCATION_NAME_KEY)!!
            val images : ArrayList<EntryImages> = result.data?.getSerializableExtra(NewEntryActivity.IMAGE_KEY)!! as ArrayList<EntryImages>
            val description : String = result.data?.getStringExtra(NewEntryActivity.DESCRIPTION_KEY)!!

            val entry = Entry(title, locationName, images, description)

//            if(position != -1){
//                ActivityMain.data.set(position, email)
//                this.myAdapter.notifyItemChanged(position)
//            }
//            else{
//
//                ActivityMain.data.add(email)
//                this.myAdapter.notifyDataSetChanged()
//            }

        }
    }

    private var entryDbHelper: EntryDbHelper? = null
    private val executorService = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding : ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //Logic for adding a new entry
        viewBinding.entryAddBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ActivityMain, NewEntryActivity::class.java)
            this.startActivity(intent)
        })

        viewBinding.searchLogo.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ActivityMain, EntrySearchActivity::class.java)
            this.startActivity(intent)
        })
        this.recyclerView = viewBinding.entryRecyclerView
        this.myAdapter = EntryAdapter(entryData,newEntryResultLauncher)
        this.recyclerView.adapter = myAdapter

//        executorService.execute {
//            // Get all contacts from the database
//            entryDbHelper = EntryDbHelper.getInstance(this@ActivityMain)
//            val entries = entryDbHelper?.allEntriesDefault
//
//            runOnUiThread { // Pass in the contacts to the needed components and set the adapter
//                myAdapter = entries?.let { EntryAdapter(it, newEntryResultLauncher) }!!
//                this.recyclerView.adapter = myAdapter
//            }
//        }

        this.recyclerView.layoutManager = LinearLayoutManager(this)
    }

}