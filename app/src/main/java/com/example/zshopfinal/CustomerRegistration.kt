package com.example.zshopfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import com.example.zshopfinal.databinding.ActivityCustomerRegistrationBinding
import com.example.zshopfinal.models.CustomerModel
import com.example.zshopfinal.models.validations.ValidationResult
import com.example.zshopfinal.models.validations.customerRegValidation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Locale

class CustomerRegistration : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerRegistrationBinding
    private var validityCount = 0
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storageReference: FirebaseStorage
    private val REQUEST_CODE_SPEECH_NAME = 1
    private val REQUEST_CODE_SPEECH_EMAIL = 2
    private val REQUEST_CODE_SPEECH_PHONE = 3
    private val REQUEST_CODE_SPEECH_GUPHONE = 4
    private val REQUEST_CODE_SPEECH_ADDRESS = 5
    private val REQUEST_CODE_SPEECH_STREET = 6
    private val REQUEST_CODE_SPEECH_HOUSE = 7
    private val REQUEST_CODE_SPEECH_PASSWORD = 8
    private val REQUEST_CODE_SPEECH_REPASSWORD = 9


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initializing auth and database variables
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.cancelbtn.setOnClickListener{
            intent = Intent(applicationContext, CustomerLogin::class.java)
            startActivity(intent)
        }

        binding.registerbtn.setOnClickListener{
            validateFormData()
            createCustomer()
        }

        //voice implementation
        binding.regvoice1.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_NAME)
        }
        binding.regvoice2.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_EMAIL)
        }
        binding.regvoice3.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_PHONE)
        }
        binding.regvoice4.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_GUPHONE)
        }
        binding.regvoice5.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_ADDRESS)
        }
        binding.regvoice6.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_STREET)
        }
        binding.regvoice7.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_HOUSE)
        }
        binding.regvoice8.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_PASSWORD)
        }
        binding.regvoice9.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_REPASSWORD)
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
                REQUEST_CODE_SPEECH_NAME -> {
                    binding.nametx.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_EMAIL -> {
                    binding.emailtx.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_PHONE -> {
                    binding.phonetx.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_GUPHONE -> {
                    binding.gphonetx.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_ADDRESS -> {
                    binding.addresstx.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_STREET -> {
                    binding.streettx.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_HOUSE -> {
                    binding.housetx.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_PASSWORD -> {
                    binding.passtx.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_REPASSWORD -> {
                    binding.repasstx.setText(spokenText)
                }
            }
        }
    }





    private fun createCustomer(){
        val name = binding.nametx.text.toString()
        val pass = binding.passtx.text.toString()
        val conpass = binding.repasstx.text.toString()
        val email = binding.emailtx.text.toString()
        val phone = binding.phonetx.text.toString()
        val guphone = binding.gphonetx.text.toString()
        val address = binding.addresstx.text.toString()
        val street = binding.streettx.text.toString()
        val houseNo = binding.housetx.text.toString()

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener{
            if(it.isSuccessful){
                val databaseRef = database.reference.child("Customer").child(auth.currentUser!!.uid)
                val customer = CustomerModel(name, pass, conpass, email, phone, guphone, address, street, houseNo, auth.currentUser!!.uid)
                databaseRef.setValue(customer).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent = Intent(applicationContext, CustomerLogin::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "Error: ${it.exception!!.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Error: ${it.exception!!.message.toString()}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateFormData(){
        validityCount = 0

        val name = binding.nametx.text.toString()
        val pass = binding.passtx.text.toString()
        val conpass = binding.repasstx.text.toString()
        val email = binding.emailtx.text.toString()
        val phone = binding.phonetx.text.toString()
        val guphone = binding.gphonetx.text.toString()

        var formval = customerRegValidation(name, pass, conpass, email, phone, guphone)

        when(formval.validateName()){
            is ValidationResult.Empty -> {
                binding.nametx.error = (formval.validateName() as ValidationResult.Empty).errorMessage
            }
            is ValidationResult.Valid -> {
                validityCount++
            }
            is ValidationResult.Invalid -> {
                binding.nametx.error = (formval.validateName() as ValidationResult.Invalid).errorMessage
            }
        }

//        when(formval.validateEmail()){
//            is ValidationResult.Empty -> {
//                binding.emailtx.error = (formval.validateEmail() as ValidationResult.Empty).errorMessage
//            }
//            is ValidationResult.Valid -> {
//                validityCount++
//            }
//            is ValidationResult.Invalid -> {
//                binding.emailtx.error = (formval.validateEmail() as ValidationResult.Invalid).errorMessage
//            }
//        }

//        when(formval.validatePhone()){
//            is ValidationResult.Empty -> {
//                binding.phonetx.error = (formval.validatePhone() as ValidationResult.Empty).errorMessage
//            }
//            is ValidationResult.Valid -> {
//                validityCount++
//            }
//            is ValidationResult.Invalid -> {
//                binding.phonetx.error = (formval.validatePhone() as ValidationResult.Invalid).errorMessage
//            }
//        }

        when(formval.validateGuphone()){
            is ValidationResult.Empty -> {
                binding.gphonetx.error = (formval.validateGuphone() as ValidationResult.Empty).errorMessage
            }
            is ValidationResult.Valid -> {
                validityCount++
            }
            is ValidationResult.Invalid -> {
                binding.gphonetx.error = (formval.validateGuphone() as ValidationResult.Invalid).errorMessage
            }
        }

        when(formval.validatePass()){
            is ValidationResult.Empty -> {
                binding.passtx.error = (formval.validatePass() as ValidationResult.Empty).errorMessage
            }
            is ValidationResult.Valid -> {
                validityCount++
            }
            is ValidationResult.Invalid -> {
                binding.passtx.error = (formval.validatePass() as ValidationResult.Invalid).errorMessage
            }
        }

        if (conpass == pass) {
            validityCount ++
        }else{
            binding.repasstx.error = "Password does not match , Please Try again"
        }
    }


}