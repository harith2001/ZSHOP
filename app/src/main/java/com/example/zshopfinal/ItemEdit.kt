package com.example.zshopfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import com.example.zshopfinal.databinding.ActivityItemEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class ItemEdit : AppCompatActivity() {

    private lateinit var binding: ActivityItemEditBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var itemId: String
    private val REQUEST_CODE_SPEECH_EITEM = 1
    private val REQUEST_CODE_SPEECH_EQUANTITY = 2
    private val REQUEST_CODE_SPEECH_ENOTES = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //fetch data from the intent
        val itemname = intent.getStringExtra("itemname")
        val quantity = intent.getStringExtra("quantity")
        val note = intent.getStringExtra("note")

        //bind values to editTexts
        binding.itemtx.setText(itemname)
        binding.quantitytx.setText(quantity)
        binding.notetx.setText(note)

        //initialize variables
        auth = FirebaseAuth.getInstance()
        itemId = intent.getStringExtra("itemId").toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Item")

        binding.BtnUpdate.setOnClickListener{
            var itemname = binding.itemtx.text.toString()
            var quantity = binding.quantitytx.text.toString()
            var note = binding.notetx.text.toString()

            //validate form
            if (itemname.isEmpty() || quantity.isEmpty() || note.isEmpty()){

                    if (itemname.isEmpty()){
                        binding.itemtx.error = "Enter Item Name"
                    }
                    if (quantity.isEmpty()){
                        binding.quantitytx.error = "Enter Quantity"
                    }
                    if (note.isEmpty()){
                        binding.notetx.error = "Enter Note"
                    }
                } else {
                    val map = HashMap<String,Any>()

                    //add data to hashMap
                    map["itemname"] = itemname
                    map["quantity"] = quantity
                    map["note"] = note

                    //update data in database
                    databaseRef.child(itemId).updateChildren(map).addOnSuccessListener {
                        binding.itemtx.setText("")
                        binding.quantitytx.setText("")
                        binding.notetx.setText("")

                        //display success message
                        Toast.makeText(applicationContext,"Item Updated Successfully", Toast.LENGTH_SHORT).show()
                        intent = Intent(applicationContext, ShoppingCart::class.java)
                        startActivity(intent)

                    }.addOnFailureListener {
                        //display failure message
                        Toast.makeText(applicationContext,"Item Update Failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }

                    binding.BtnDelete.setOnClickListener{
                        databaseRef.child(itemId).removeValue().addOnCompleteListener{
                            if (it.isSuccessful){
                                Toast.makeText(applicationContext,"Item Deleted Successfully", Toast.LENGTH_SHORT).show()
                                intent = Intent(applicationContext, ShoppingCart::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(applicationContext,"Item Deletion Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }


                    binding.btnCancel2.setOnClickListener{
                        intent = Intent(applicationContext, ShoppingCart::class.java)
                        startActivity(intent)
                    }


                    //voice
                    binding.editvoice1.setOnClickListener{
                        StartSpeech(REQUEST_CODE_SPEECH_EITEM)
                    }

                    binding.editvoice2.setOnClickListener{
                        StartSpeech(REQUEST_CODE_SPEECH_EQUANTITY)
                    }

                    binding.editvoice3.setOnClickListener{
                        StartSpeech(REQUEST_CODE_SPEECH_ENOTES)
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
                REQUEST_CODE_SPEECH_EITEM -> {
                    // Set the recognized text to edtitem
                    binding.itemtx.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_EQUANTITY -> {
                    // Set the recognized text to edtquantity
                    binding.quantitytx.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_ENOTES -> {
                    // Set the recognized text to edtnotes
                    binding.notetx.setText(spokenText)
                }
            }
        }
    }


}