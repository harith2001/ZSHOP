package com.example.zshopfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.zshopfinal.databinding.ActivityAdminLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminLogin : AppCompatActivity() {
    private lateinit var binding: ActivityAdminLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initializing auth
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Admin")

        //register
        binding.btnRegister.setOnClickListener {
            intent = Intent(applicationContext, AdminRegistration::class.java)
            startActivity(intent)
        }

        //back
        binding.btnBackR.setOnClickListener{
            intent = Intent(applicationContext, FirstPage::class.java)
            startActivity(intent)
        }

        //login button function
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPass.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) {
                    binding.edtEmail.error = "Enter your email address"
                }
                if (password.isEmpty()) {
                    binding.edtPass.error = "Enter your password"
                }

                Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()
            } else if (!email.matches(emailPattern.toRegex())) {
                //validate email pattern
                binding.edtEmail.error = "Enter a valid email address"
            } else if (password.length <= 5) {
                //validate passwords
                binding.edtPass.error = "Password must be at least 6 characters."

            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            intent = Intent(applicationContext, AdminDashboard::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }


        }
    }
}