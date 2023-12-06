package com.example.zshopfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.zshopfinal.databinding.ActivityAdminDashboardBinding
import com.example.zshopfinal.models.AdminModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminDashboard : AppCompatActivity() {

    private lateinit var  binding: ActivityAdminDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var admin : AdminModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize variables
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Admin")

        databaseRef.child(auth.currentUser!!.uid).addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                //retrieve values from the db and convert them to user data class
                admin = snapshot.getValue(AdminModel::class.java)!!
                val helloMessage = "Hello,${admin.name} From ${admin.shopname}"
                binding.textView17.text = helloMessage
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminDashboard, "Failed to fetch user", Toast.LENGTH_SHORT).show()

            }
        })

        binding.btnLogoutR.setOnClickListener {
            intent = Intent(applicationContext, AdminLogin::class.java)
            startActivity(intent)
        }

        binding.btnProfileR.setOnClickListener {
            intent = Intent(applicationContext, AdminProfile::class.java)
            startActivity(intent)
        }

        binding.btnOrders.setOnClickListener{
            intent = Intent(applicationContext, AdminOrders::class.java)
            startActivity(intent)
        }





    }
}