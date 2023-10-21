package com.example.mobdevemco

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.ActivityEditEntryBinding

class EntryEditActivity : AppCompatActivity(){
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

    }
}