package com.example.mobdevemco

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.databinding.EntryItemBinding
import androidx.appcompat.app.AlertDialog
class EntryAdapter(private val data: ArrayList<Entry>) : RecyclerView.Adapter<EntryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        // Initialize the ViewBinding of an item's layout
        val itemViewBinding: EntryItemBinding = EntryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        val entryViewHolder = EntryViewHolder(itemViewBinding)

        val builder = AlertDialog.Builder(entryViewHolder.itemView.context)

        entryViewHolder.itemView.setOnClickListener{
            val intent : Intent = Intent(entryViewHolder.itemView.context, EntryDetailsActivity::class.java)
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