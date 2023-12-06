package com.example.zshopfinal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zshopfinal.Adapters.ItemAdapter
import com.example.zshopfinal.databinding.ActivityShoppingCartDetailsBinding
import com.example.zshopfinal.models.Item
import com.example.zshopfinal.models.ShoppingCart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShoppingCartDetails : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingCartDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private var mList = ArrayList<Item>()
    private lateinit var adapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingCartDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root);

        binding.btnbackD.setOnClickListener {
            startActivity(Intent(this, ShoppingHistory::class.java))
        }


        val cartId = intent.getStringExtra("cartId")

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference;

        val recyclerView = binding.rvCartDetails

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this);

        adapter = ItemAdapter(mList)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListner(object:ItemAdapter.onItemClickListner{
            override fun onItemClick(position: Int) {
//                intent = Intent(applicationContext, ItemEdit::class.java).also {
//                    it.putExtra("itemname", mList[position].itemname)
//                    it.putExtra("quantity", mList[position].quantity)
//                    it.putExtra("note", mList[position].note)
//                    it.putExtra("itemId", mList[position].itemid)
//                    startActivity(it)
//                }
            }
        })

        fetchCartDetails(cartId!!)

    }

    private fun fetchCartDetails(cartId: String) {
        databaseRef.child(getString(R.string.carts_history_db_node_name)).child(uid).child(cartId).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                val cart = snapshot.getValue(ShoppingCart::class.java)
                if (cart?.items != null) {
                    for (aItem in cart.items) {
                        mList.add(aItem)
                    }
                    adapter.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ShoppingCartDetails, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}