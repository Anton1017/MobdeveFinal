package com.example.mobdevemco.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.activity.EntryDetailsActivity
import com.example.mobdevemco.databinding.EntryItemBinding
import com.example.mobdevemco.model.Entry
import com.example.mobdevemco.viewholder.EntryViewHolder


class EntryAdapter(private val data: ArrayList<Entry>, private val entryResultLauncher: ActivityResultLauncher<Intent>) : RecyclerView.Adapter<EntryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        // Initialize the ViewBinding of an item's layout
        val itemViewBinding: EntryItemBinding = EntryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val entryViewHolder = EntryViewHolder(itemViewBinding)

        if(entryViewHolder.adapterPosition == 1){
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 16, 0, 8)
            entryViewHolder.itemView.setLayoutParams(lp)
        }

        val builder = AlertDialog.Builder(entryViewHolder.itemView.context)

        entryViewHolder.itemView.setOnClickListener{
            val entry = data.get(entryViewHolder.adapterPosition)
            val intent : Intent = Intent(entryViewHolder.itemView.context, EntryDetailsActivity::class.java)
            intent.putExtra(Entry.ID, entry.getId())
            intent.putExtra(ADAPTER_POS, entryViewHolder.adapterPosition)
            entryResultLauncher.launch(intent)
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

    companion object {
        val ADAPTER_POS = "ADAPTER_POS"
    }

}