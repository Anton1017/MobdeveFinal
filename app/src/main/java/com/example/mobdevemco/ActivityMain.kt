package com.example.mobdevemco


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.ActivityMainBinding

class ActivityMain : AppCompatActivity(){

    private var entryData = DataGenerator.loadEntryData()

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: EntryAdapter


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
                       
            this.startActivity(intent)
        })
        this.recyclerView = viewBinding.entryRecyclerView
        this.myAdapter = EntryAdapter(entryData)
        this.recyclerView.adapter = myAdapter
        this.recyclerView.layoutManager = LinearLayoutManager(this)
    }

}