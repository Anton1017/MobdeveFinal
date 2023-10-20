package com.example.mobdevemco

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mobdevemco.databinding.ActivityViewEntryBinding

class EntryDetailsActivity: AppCompatActivity() {
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


    }
}