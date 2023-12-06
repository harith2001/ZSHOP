package com.example.zshopfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zshopfinal.Adapters.ShoppingCartAdapter
import com.example.zshopfinal.databinding.ActivityAdminOrdersBinding
import com.example.zshopfinal.models.ShoppingCart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminOrders : AppCompatActivity() {

    lateinit var binding: ActivityAdminOrdersBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShoppingCartAdapter
    private var mList = ArrayList<ShoppingCart>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnbackO.setOnClickListener{
            intent = Intent(this, AdminDashboard::class.java)
            startActivity(intent)
        }

        var recyclerView = binding.rvorders

        initialiseDb()

        recyclerView.layoutManager = LinearLayoutManager(this);

        fetchOrders()

        adapter = ShoppingCartAdapter(mList)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListner(object:ShoppingCartAdapter.onItemClickListner{
            override fun onItemClick(cartId: String) {
                intent = Intent(applicationContext, AdminOrderConfirm::class.java).also {
                    it.putExtra("cartId", cartId)
                }
                startActivity(intent)
            }
        })


    }

    private fun fetchOrders() {
        databaseRef.child("CartsHistory").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (snap in snapshot.children) {
                    val cart = snap.getValue(ShoppingCart::class.java)
                    if (cart != null) {
                        mList.add(cart)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminOrders, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initialiseDb() {
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference;
        uid = auth.currentUser?.uid.toString()
    }
}