package com.example.mobdevemco

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mobdevemco.databinding.ActivityCreateEntryBinding
class NewEntryActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding : ActivityCreateEntryBinding = ActivityCreateEntryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.createBtn.setOnClickListener(View.OnClickListener {
            /* TODO:
             *  1. Finish the activity
             * */
            finish()
        })
    }

}