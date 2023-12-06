package com.example.zshopfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zshopfinal.Adapters.ItemAdapter
import com.example.zshopfinal.Adapters.ShoppingCartAdapter
import com.example.zshopfinal.databinding.ActivityShoppingHistoryBinding
import com.example.zshopfinal.models.Item
import com.example.zshopfinal.models.ShoppingCart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class ShoppingHistory : AppCompatActivity() {

    lateinit var binding: ActivityShoppingHistoryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShoppingCartAdapter
    private var mList = ArrayList<ShoppingCart>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //back button
        binding.btnbackS.setOnClickListener {
            intent = Intent(this, CustomerDashboard::class.java)
            startActivity(intent)
        }

        var recyclerView = binding.rvCartsHistory


        initialiseDb()

        recyclerView.layoutManager = LinearLayoutManager(this);

        fetchShoppingCartsHistory()

        adapter = ShoppingCartAdapter(mList)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListner(object:ShoppingCartAdapter.onItemClickListner{
            override fun onItemClick(cartId: String) {
                intent = Intent(applicationContext, ShoppingCartDetails::class.java).also {
                    it.putExtra("cartId", cartId)
                    startActivity(it)
                }
            }
        })

    }

    private fun initialiseDb() {
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference;
    }

    private fun fetchShoppingCartsHistory() {
        databaseRef.child(getString(R.string.carts_history_db_node_name)).child(uid).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (itemSnapshot in snapshot.children){
                    val cart = itemSnapshot.getValue(ShoppingCart::class.java)
                    if (cart != null) {
                        mList.add(cart)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}