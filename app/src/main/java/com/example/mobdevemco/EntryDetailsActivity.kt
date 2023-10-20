package com.example.mobdevemco

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mobdevemco.databinding.ActivityViewEntryBinding

class EntryDetailsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding : ActivityViewEntryBinding = ActivityViewEntryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

    }
}