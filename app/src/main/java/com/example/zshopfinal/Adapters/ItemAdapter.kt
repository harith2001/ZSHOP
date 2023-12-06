package com.example.zshopfinal.Adapters

import com.example.zshopfinal.models.Item
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.zshopfinal.R

class ItemAdapter (var mlist: List<Item>):

    RecyclerView.Adapter<ItemAdapter.viewHolder>() {

    private lateinit var mListner: onItemClickListner

    //Setting up onClick listner interface
    interface onItemClickListner {
        fun onItemClick(position: Int)

    }

    fun setOnItemClickListner(listner: onItemClickListner) {
        mListner = listner
    }

    inner class viewHolder(itemView: View, listner: onItemClickListner) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val note: TextView = itemView.findViewById(R.id.tvnote)
        val quantity: TextView = itemView.findViewById(R.id.tvQuantity)
        init {
            itemView.setOnClickListener {
                listner.onItemClick(adapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_add_item, parent, false)

        return viewHolder(view, mListner)
    }

    override fun onBindViewHolder(holder: ItemAdapter.viewHolder, position: Int) {
        holder.title.text = mlist[position].itemname
        holder.note.text = mlist[position].note
        holder.quantity.text = mlist[position].quantity
    }

    override fun getItemCount(): Int {
        return mlist.size
    }
}
