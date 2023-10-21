package com.example.mobdevemco.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mobdevemco.databinding.ActivityEditMapBinding
import com.example.mobdevemco.databinding.ActivitySearchResultBinding

class EntrySearchActivity  : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding : ActivitySearchResultBinding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.backBtn.setOnClickListener(View.OnClickListener {

            finish()
        })
    }

}