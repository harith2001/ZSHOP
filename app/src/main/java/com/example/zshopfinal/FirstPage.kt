package com.example.zshopfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.zshopfinal.databinding.ActivityFirstPageBinding

class FirstPage : AppCompatActivity() {

    private lateinit var binding: ActivityFirstPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cusbtn.setOnClickListener {
            val intent = Intent(this, CustomerLogin::class.java)
            startActivity(intent)
        }

        binding.admbtn.setOnClickListener {
            val intent = Intent(this, AdminLogin::class.java)
            startActivity(intent)

        }
    }
}