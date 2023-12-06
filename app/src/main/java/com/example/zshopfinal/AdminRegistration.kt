package com.example.zshopfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.zshopfinal.databinding.ActivityAdminRegistrationBinding
import com.example.zshopfinal.models.AdminModel
import com.example.zshopfinal.models.validations.ValidationResult
import com.example.zshopfinal.models.validations.adminRegValidation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminRegistration : AppCompatActivity() {

    private lateinit var binding: ActivityAdminRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var validityCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //intializing auth and database variables
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.btnCancelR.setOnClickListener{
            intent = Intent(applicationContext, AdminLogin::class.java)
            startActivity(intent)
        }

        binding.btnSubmitR.setOnClickListener{
            validateFormData()
            createAdmin()
        }
    }

    private fun createAdmin(){
        val name = binding.editNameR.text.toString()
        val email = binding.editEmailR.text.toString()
        val phone = binding.editPhoneR.text.toString()
        val shopname = binding.editShopR.text.toString()
        val location = binding.editLocationR.text.toString()
        val pass = binding.editPassR.text.toString()
        val conpass = binding.editRepassR.text.toString()

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener{

            if (it.isSuccessful){
                val databaseRef = database.reference.child("Admin").child(auth.currentUser!!.uid)
                val admin = AdminModel(name, pass, conpass, email, phone, shopname, location, auth.currentUser!!.uid)
                databaseRef.setValue(admin).addOnCompleteListener{
                    if (it.isSuccessful){
                        intent = Intent(applicationContext, AdminLogin::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(applicationContext, "Error: ${it.exception!!.message.toString()}", Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                Toast.makeText(applicationContext, "Error: ${it.exception!!.message.toString()}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateFormData(){
        validityCount = 0

        val name = binding.editNameR.text.toString()
        val email = binding.editEmailR.text.toString()
        val phone = binding.editPhoneR.text.toString()
        val shopname = binding.editShopR.text.toString()
        val location = binding.editLocationR.text.toString()
        val pass = binding.editPassR.text.toString()
        val conpass = binding.editRepassR.text.toString()

        var formval = adminRegValidation(name, shopname, location, pass, email, phone)

        when(formval.validateName()){
            is  ValidationResult.Empty -> {
                binding.editNameR.error = (formval.validateName() as ValidationResult.Empty).errorMessage
            }
            is ValidationResult.Valid -> {
                validityCount++
            }
            is ValidationResult.Invalid -> {
                binding.editNameR.error = (formval.validateName() as ValidationResult.Invalid).errorMessage
            }
        }

        when(formval.validateEmail()){
            is ValidationResult.Empty -> {
                binding.editEmailR.error = (formval.validateEmail() as ValidationResult.Empty).errorMessage
            }
            is ValidationResult.Valid -> {
                validityCount++
            }
            is ValidationResult.Invalid -> {
                binding.editEmailR.error = (formval.validateEmail() as ValidationResult.Invalid).errorMessage
            }
        }

        when(formval.validatePhone()){
            is ValidationResult.Empty -> {
                binding.editPhoneR.error = (formval.validatePhone() as ValidationResult.Empty).errorMessage
            }
            is ValidationResult.Valid -> {
                validityCount++
            }
            is ValidationResult.Invalid -> {
                binding.editPhoneR.error = (formval.validatePhone() as ValidationResult.Invalid).errorMessage
            }
        }

        when(formval.validatePass()){
            is ValidationResult.Empty -> {
                binding.editPassR.error = (formval.validatePass() as ValidationResult.Empty).errorMessage
            }
            is ValidationResult.Valid -> {
                validityCount++
            }
            is ValidationResult.Invalid -> {
                binding.editPassR.error = (formval.validatePass() as ValidationResult.Invalid).errorMessage
            }
        }

        when(formval.validateshopname()) {
            is ValidationResult.Empty -> {
                binding.editShopR.error =
                    (formval.validateshopname() as ValidationResult.Empty).errorMessage
            }

            is ValidationResult.Valid -> {
                validityCount++
            }

            is ValidationResult.Invalid -> {
                binding.editShopR.error =
                    (formval.validateshopname() as ValidationResult.Invalid).errorMessage
            }
        }

            when(formval.validatelocation()){
                is ValidationResult.Empty -> {
                    binding.editLocationR.error = (formval.validatelocation() as ValidationResult.Empty).errorMessage
                }
                is ValidationResult.Valid -> {
                    validityCount++
                }
                is ValidationResult.Invalid -> {
                    binding.editLocationR.error = (formval.validatelocation() as ValidationResult.Invalid).errorMessage
                }
            }

        if (pass == conpass) {
            validityCount++
        }else{
            binding.editRepassR.error = "Password does not match , Please Try again"
        }

    }


}