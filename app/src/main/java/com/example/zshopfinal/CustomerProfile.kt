package com.example.zshopfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.zshopfinal.databinding.ActivityCustomerProfileBinding
import com.example.zshopfinal.models.CustomerModel
import android.speech.RecognizerIntent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.content.ActivityNotFoundException
import java.util.*


class CustomerProfile : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var customer: CustomerModel
    private val SPEECH_REQUEST_CODE = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize variables
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Customer")

        databaseRef.child(auth.currentUser!!.uid).addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                //retrieve values from the db and convert them to customer data class
                customer = snapshot.getValue(CustomerModel::class.java)!!

                binding.txname.text = customer.name
                binding.txemail.text = customer.email
                binding.txphone.text = customer.phone
                binding.txgphone.text = customer.guphone
                binding.txhouse.text = customer.houseNo
                binding.txaddress.text = customer.address
                binding.txstreet.text=customer.street
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CustomerProfile, "Failed to fetch user", Toast.LENGTH_SHORT).show()

            }
        })

        binding.btnVoiceCommand.setOnClickListener {
            startVoiceRecognition()
        }


        //Back
        binding.btnback.setOnClickListener{
            intent = Intent(applicationContext, CustomerDashboard::class.java)
                startActivity(intent)
        }

        //Delete
        binding.btndelete.setOnClickListener{
            var currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@CustomerProfile, "Account Deleted", Toast.LENGTH_SHORT).show()
                        intent = Intent(applicationContext, CustomerLogin::class.java)
                            startActivity(intent)
                    }else{
                        Toast.makeText(this@CustomerProfile, "Account Not Deleted", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        //Edit
        binding.btnedit.setOnClickListener {
            intent = Intent(applicationContext, CustomerProfileEdit::class.java).also {
                it.putExtra("name", customer.name)
                //it.putExtra("email", customer.email)
                it.putExtra("phone", customer.phone)
                it.putExtra("gphone", customer.guphone)
                it.putExtra("house", customer.houseNo)
                it.putExtra("address", customer.address)
                it.putExtra("street", customer.street)
                startActivity(it)
            }
        }

    }

    //voice commands implementation

    private fun startVoiceRecognition(){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        try{
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(this, "Voice recognition not supported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==SPEECH_REQUEST_CODE && resultCode== RESULT_OK){
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (results !=null && results.isNotEmpty()){
                val spokenText = results[0].toLowerCase(Locale.getDefault())
                pressVoiceButton(spokenText)
            }
        }
    }

    private fun pressVoiceButton(command:String){
        when(command) {
            "delete" -> {
                var currentUser = FirebaseAuth.getInstance().currentUser
                currentUser?.delete()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@CustomerProfile,
                                "Account Deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                            intent = Intent(applicationContext, CustomerLogin::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@CustomerProfile,
                                "Account Not Deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
            "edit"->{
                intent = Intent(applicationContext, CustomerProfileEdit::class.java).also {
                    it.putExtra("name", customer.name)
                    //it.putExtra("email", customer.email)
                    it.putExtra("phone", customer.phone)
                    it.putExtra("gphone", customer.guphone)
                    it.putExtra("house", customer.houseNo)
                    it.putExtra("address", customer.address)
                    it.putExtra("street", customer.street)
                    startActivity(it)
                }
            }
            "back"->{
                intent = Intent(applicationContext, CustomerDashboard::class.java)
                startActivity(intent)
            }

            else -> {
                Toast.makeText(this, "Voice command not recognized", Toast.LENGTH_SHORT).show()
            }
        }
    }

}