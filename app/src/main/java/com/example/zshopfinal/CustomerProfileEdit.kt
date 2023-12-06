package com.example.zshopfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import com.example.zshopfinal.databinding.ActivityCustomerProfileEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class CustomerProfileEdit : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerProfileEditBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private val REQUEST_CODE_SPEECH_ENAME = 1
    private val REQUEST_CODE_SPEECH_EPHONE = 2
    private val REQUEST_CODE_SPEECH_EGUPHONE = 3
    private val REQUEST_CODE_SPEECH_EADDRESS = 4
    private val REQUEST_CODE_SPEECH_ESTREET = 5
    private val REQUEST_CODE_SPEECH_EHOUSE = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize variables
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Customer")

        val name = intent.getStringExtra("name")
        //val email = intent.getStringExtra("email")
        val phone = intent.getStringExtra("phone")
        val gphone = intent.getStringExtra("gphone")
        val house = intent.getStringExtra("house")
        val address = intent.getStringExtra("address")
        val street = intent.getStringExtra("street")

        //bind values to editTexts
        binding.edtname.setText(name)
        //binding.edtemail.setText(email)
        binding.edtphone.setText(phone)
        binding.edtgphone.setText(gphone)
        binding.edthouse.setText(house)
        binding.edtaddress.setText(address)
        binding.edtstreet.setText(street)

        binding.btnbackk.setOnClickListener {
            intent = Intent(applicationContext, CustomerProfile::class.java)
            startActivity(intent)
        }

        //voice implementation

        binding.edvoice1.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_ENAME)
        }
        binding.edvoice2.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_EPHONE)
        }
        binding.edvoice3.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_EGUPHONE)
        }
        binding.edvoice4.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_EHOUSE)
        }
        binding.edvoice5.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_ESTREET)
        }
        binding.edvoice6.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_EADDRESS)
        }


        binding.btnupdate.setOnClickListener {
            var name = binding.edtname.text.toString()
            //var email = binding.edtemail.text.toString()
            var phone = binding.edtphone.text.toString()
            var gphone = binding.edtgphone.text.toString()
            var house = binding.edthouse.text.toString()
            var address = binding.edtaddress.text.toString()
            var street = binding.edtstreet.text.toString()

            if (name.isEmpty() || phone.isEmpty() || gphone.isEmpty() || house.isEmpty() || address.isEmpty() || street.isEmpty()) {
                if (name.isEmpty()) {
                    binding.edtname.error = "Please enter name"
                }
                if (phone.isEmpty()) {
                    binding.edtphone.error = "Please enter phone number"
                }
                if (gphone.isEmpty()) {
                    binding.edtgphone.error = "Please enter guardian phone number"
                }
                if (house.isEmpty()) {
                    binding.edthouse.error = "Please enter house number"
                }
                if (address.isEmpty()) {
                    binding.edtaddress.error = "Please enter address"
                }
                if (street.isEmpty()) {
                    binding.edtstreet.error = "Please enter street"
                }
            } else {
                val map = HashMap<String, Any>()

                map["name"] = name
                //map["email"] = email
                map["phone"] = phone
                map["guphone"] = gphone
                map["houseNo"] = house
                map["address"] = address
                map["street"] = street

                //update database from hashMap
                databaseRef.child(uid).updateChildren(map).addOnCompleteListener {
                    if (it.isSuccessful) {
                        intent = Intent(applicationContext, CustomerProfile::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
                REQUEST_CODE_SPEECH_ENAME -> {
                    binding.edtname.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_EPHONE -> {
                    binding.edtphone.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_EGUPHONE -> {
                    binding.edtgphone.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_EADDRESS -> {
                    binding.edtaddress.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_ESTREET -> {
                    binding.edtstreet.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_EHOUSE -> {
                    binding.edthouse.setText(spokenText)
                }
            }
        }
    }



}