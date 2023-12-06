package com.example.zshopfinal.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zshopfinal.R
import com.example.zshopfinal.models.ShoppingCart

class ShoppingCartAdapter(var mlist: List<ShoppingCart>):
    RecyclerView.Adapter<ShoppingCartAdapter.viewHolder>()
    {
        private lateinit var mListner: onItemClickListner

        //Setting up onClick listner interface
        interface onItemClickListner {
            fun onItemClick(cartId: String)

        }

        fun setOnItemClickListner(listner: onItemClickListner) {
            mListner = listner
        }

        inner class viewHolder(itemView: View, listner: onItemClickListner) : RecyclerView.ViewHolder(itemView) {
            val cartId: TextView = itemView.findViewById(R.id.cart_id)
            val cartName: TextView = itemView.findViewById(R.id.cart_name)

            init {
                itemView.setOnClickListener {
                    listner.onItemClick(cartId.text.toString())
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.shopping_cart_item, parent, false)

            return viewHolder(view, mListner)
        }

        override fun getItemCount(): Int {
            return mlist.size
        }

        override fun onBindViewHolder(holder: viewHolder, position: Int) {
            holder.cartId.text = mlist[position].cartid
            holder.cartName.text = "Shopping Cart ${position + 1}"
        }


    }