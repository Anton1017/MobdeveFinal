package com.example.mobdevemco

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mobdevemco.databinding.ActivityCreateEntryBinding
import com.example.mobdevemco.databinding.ActivityEditMapBinding

class EntryMapActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding : ActivityEditMapBinding = ActivityEditMapBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.confirmBtn.setOnClickListener(View.OnClickListener {

            finish()
        })
        viewBinding.backBtn.setOnClickListener(View.OnClickListener {

            finish()
        })
    }

}