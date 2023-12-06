package com.example.zshopfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.zshopfinal.databinding.ActivityCustomerDashboardBinding
import com.example.zshopfinal.models.CustomerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CustomerDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var customer : CustomerModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize variables
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Customer")

        databaseRef.child(auth.currentUser!!.uid).addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                //retrieve values from the db and convert them to user data class
                customer = snapshot.getValue(CustomerModel::class.java)!!
                val helloMessage = getString(R.string.hello_message, customer.name)
                binding.hellotx.text = helloMessage
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CustomerDashboard, "Failed to fetch user", Toast.LENGTH_SHORT).show()

            }
        })



        binding.cartbtn.setOnClickListener {
            intent = Intent(applicationContext, ShoppingCart::class.java)
            startActivity(intent)
        }

        binding.historybtn.setOnClickListener {
            intent = Intent(applicationContext, ShoppingHistory::class.java)
            startActivity(intent)
        }

        binding.profile.setOnClickListener {
            intent = Intent(applicationContext, CustomerProfile::class.java)
            startActivity(intent)
        }

        binding.logout.setOnClickListener {
            intent = Intent(applicationContext, FirstPage::class.java)
            startActivity(intent)
        }

    }
}