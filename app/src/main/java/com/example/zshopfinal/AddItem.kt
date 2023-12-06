package com.example.zshopfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.zshopfinal.databinding.ActivityAddItemBinding
import com.example.zshopfinal.models.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.speech.RecognizerIntent
import java.util.Locale

class AddItem : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private val REQUEST_CODE_SPEECH_ITEM = 1
    private val REQUEST_CODE_SPEECH_QUANTITY = 2
    private val REQUEST_CODE_SPEECH_NOTES = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize variables
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference

        binding.Btncancel.setOnClickListener {
            intent = Intent(applicationContext, ShoppingCart::class.java)
            startActivity(intent)
        }

        binding.Btnadd.setOnClickListener {
            addItem()
        }

        binding.voiceadd1.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_ITEM)
        }
        binding.voiceadd2.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_QUANTITY)
        }
        binding.voiceadd3.setOnClickListener{
            StartSpeech(REQUEST_CODE_SPEECH_NOTES)
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
                REQUEST_CODE_SPEECH_ITEM -> {
                    // Set the recognized text to edtitem
                    binding.edtitem.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_QUANTITY -> {
                    // Set the recognized text to edtquantity
                    binding.edtquantity.setText(spokenText)
                }
                REQUEST_CODE_SPEECH_NOTES -> {
                    // Set the recognized text to edtnotes
                    binding.edtnotes.setText(spokenText)
                }
            }
        }
    }




    private fun addItem(){

        val itemname = binding.edtitem.text.toString().trim()
        val quantity = binding.edtquantity.text.toString().trim()
        val note = binding.edtnotes.text.toString().trim()

        //validate form
        if(itemname.isEmpty() || quantity.isEmpty() || note.isEmpty()){
            if(itemname.isEmpty()){
                binding.edtitem.error = "Enter Item Name"
            }
            if(quantity.isEmpty()){
                binding.edtquantity.error = "Enter Quantity"
            }
            if(note.isEmpty()){
                binding.edtnotes.error = "Enter Notes"
            }
        } else {
            // TODO:
            // 1. Add item to active_carts node under user id
            var itemId = databaseRef.child(getString(R.string.active_carts_db_node_name)).child(uid).push().key
            val item = Item(itemname, quantity, note, itemId)
            databaseRef.child(getString(R.string.active_carts_db_node_name)).child(uid).child(itemId.toString()).setValue(item).addOnCompleteListener {
                if (it.isSuccessful){
                    intent = Intent(applicationContext, ShoppingCart::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()

                } else {
                    //display a failure message
                    Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

}