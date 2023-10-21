package com.example.mobdevemco

import android.content.Intent
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

            finish()
        })
        viewBinding.cancelBtn.setOnClickListener(View.OnClickListener {

            finish()
        })
        viewBinding.editLocationBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@NewEntryActivity, EntryMapActivity::class.java)
            this.startActivity(intent)
        })
    }

}