package com.example.mobdevemco.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.EntryItemBinding
import androidx.appcompat.app.AlertDialog
import com.example.mobdevemco.activity.EntryDetailsActivity
import com.example.mobdevemco.model.Entry
import com.example.mobdevemco.viewholder.EntryViewHolder

class EntryAdapter(private val data: ArrayList<Entry>, private val myActivityResultLauncher: ActivityResultLauncher<Intent>) : RecyclerView.Adapter<EntryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        // Initialize the ViewBinding of an item's layout
        val itemViewBinding: EntryItemBinding = EntryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val entryViewHolder = EntryViewHolder(itemViewBinding)

        val builder = AlertDialog.Builder(entryViewHolder.itemView.context)

        entryViewHolder.itemView.setOnClickListener{
            val entry = data.get(entryViewHolder.adapterPosition)
            val intent : Intent = Intent(entryViewHolder.itemView.context, EntryDetailsActivity::class.java)
            intent.putExtra(Entry.ID, entry.getId())
            parent.context.startActivity(intent)
        }

//        entryViewHolder.setDeleteOnClickListener(View.OnClickListener{
//            builder.setTitle("Alert!")
//                .setMessage("Are you sure you want to delete " + itemViewBinding.entryTitle.text)
//                .setCancelable(true)
//                .setPositiveButton("Yes"){dialogInterface,it ->
//                    dialogInterface.cancel()
//                }
//                .setNegativeButton("No"){dialogInterface,it ->
//                    dialogInterface.cancel()
//                }
//                .show()
//        })


        return entryViewHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bindData(data[position])
    }

}