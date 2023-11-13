package com.example.mobdevemco.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultCallback

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mobdevemco.databinding.ActivityCreateEntryBinding
class NewEntryActivity : AppCompatActivity(){
    companion object {
        const val POSITION_KEY = "POSITION_KEY"

        const val TITLE_KEY = "TITLE_KEY"
        const val LOCATION_NAME_KEY = "LOCATION_NAME_KEY"
        const val IMAGE_KEY = "IMAGE_KEY"
        const val DESCRIPTION_KEY = "DESCRIPTION_KEY"
        const val CREATED_AT_KEY = "CREATED_AT_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding : ActivityCreateEntryBinding = ActivityCreateEntryBinding.inflate(layoutInflater)
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
            val intent = Intent(this@NewEntryActivity, EntryMapActivity::class.java)
            this.startActivity(intent)
        })
    }

}