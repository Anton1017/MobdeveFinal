package com.example.mobdevemco

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mobdevemco.databinding.ActivityCreateEntryBinding

class EntryDetailsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding : ActivityCreateEntryBinding = ActivityCreateEntryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

    }
}