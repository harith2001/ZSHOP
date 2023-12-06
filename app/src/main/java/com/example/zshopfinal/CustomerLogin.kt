package com.example.zshopfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.widget.Toast
import com.example.zshopfinal.databinding.ActivityCustomerLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class CustomerLogin : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerLoginBinding
    private lateinit var auth: FirebaseAuth
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private lateinit var databaseReference: DatabaseReference
    private val REQUEST_CODE_SPEECH_EMAIL = 1
    private val REQUEST_CODE_SPEECH_PASSWORD = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initializing auth
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Customer")

        //back button function
        binding.backimg.setOnClickListener {
            intent = Intent(applicationContext, FirstPage::class.java)
            startActivity(intent)
        }

        binding.btnAcess.setOnClickListener{
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)

        }

        //login button function
        binding.loginbtn.setOnClickListener {
            val email = binding.edtemail.text.toString()
            val password = binding.edtpass.text.toString()

            if(email.isEmpty() || password.isEmpty()){

                if(email.isEmpty()){
                    binding.edtemail.error = "Enter your email address"
                }
                if(password.isEmpty()){
                    binding.edtpass.error = "Enter your password"
                }

                Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()

            } else if (!email.matches(emailPattern.toRegex())){
                //validate email pattern
                binding.edtemail.error = "Enter a valid email address"

            } else if (password.length <=5){
                //validate passwords
                binding.edtpass.error = "Password must be at least 6 characters."

            } else{
                //Log in
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        intent = Intent(applicationContext, CustomerDashboard::class.java)
                        startActivity(intent)
                    } else{
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //register button function
        binding.btnregister.setOnClickListener {
            intent = Intent(applicationContext, CustomerRegistration::class.java)
            startActivity(intent)
        }

        //voice
        binding.loginvoice1.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_EMAIL)
        }

        binding.loginvoice2.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_PASSWORD)
        }
    }


    //voice implementation
    private fun StartSpeech(requestCode: Int) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to input")

        try {
            startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.get(0)

            when (requestCode) {
                REQUEST_CODE_SPEECH_EMAIL -> {
                    binding.edtemail.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_PASSWORD -> {
                    binding.edtpass.setText(spokenText)
                }
            }
        }
    }


}