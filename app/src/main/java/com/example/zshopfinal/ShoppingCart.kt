package com.example.zshopfinal

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zshopfinal.Adapters.ItemAdapter
import com.example.zshopfinal.databinding.ActivityShoppingCartBinding
import com.example.zshopfinal.models.Item
import com.example.zshopfinal.models.ShoppingCart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ShoppingCart : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private var mList = ArrayList<Item>()
    private lateinit var adapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemid : String
    private val SPEECH_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.additem.setOnClickListener {
            intent = Intent(applicationContext, AddItem::class.java)
            startActivity(intent)
        }

        binding.btnCancel.setOnClickListener {
            intent = Intent(applicationContext, CustomerDashboard::class.java)
            startActivity(intent)
        }

        binding.btnVoiceCommand2.setOnClickListener{
            startVoiceRecognition()
        }

        binding.btnSubmit.setOnClickListener {
            submitCart()
        }

        //initialize variables
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference;

        var recyclerView = binding.recyclerview

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this);

        //add data to array list
        retrieveData()

        adapter = ItemAdapter(mList)
        recyclerView.adapter = adapter

        //setting up on click listner each item
        adapter.setOnItemClickListner(object:ItemAdapter.onItemClickListner{
            override fun onItemClick(position: Int) {
                intent = Intent(applicationContext, ItemEdit::class.java).also {
                    it.putExtra("itemname", mList[position].itemname)
                    it.putExtra("quantity", mList[position].quantity)
                    it.putExtra("note", mList[position].note)
                    it.putExtra("itemId", mList[position].itemid)
                    startActivity(it)
                }
            }
        })
    }

    private fun retrieveData(){
//        todo
//        1. go to ActiveCarts node
//        2. go to userId
//        3. get active cart items
        databaseRef.child(getString(R.string.active_carts_db_node_name)).child(uid).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (itemSnapshot in snapshot.children){
                    val item = itemSnapshot.getValue(Item::class.java)
                    if (item != null) {
                        mList.add(item)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ShoppingCart, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun submitCart() {
//        todo
//        1. save cart to cart history under the logged in user

        var cartId = databaseRef.child(getString(R.string.carts_history_db_node_name)).child(uid).push().key
        val cart = ShoppingCart(cartId, mList)
        databaseRef.child(getString(R.string.carts_history_db_node_name)).child(uid).child(cartId.toString()).setValue(cart).addOnCompleteListener {
                if (it.isSuccessful){
//                      2. delete data from the logged in user's active cart since it's published
                    databaseRef.child(getString(R.string.active_carts_db_node_name)).child(uid).removeValue().addOnSuccessListener {

//                      3. navigate carts history
                        intent = Intent(applicationContext, CustomerDashboard::class.java)
                        startActivity(intent)
                    }

                } else {
                    Toast.makeText(this, "Error: ${it.exception!!.message.toString()}", Toast.LENGTH_SHORT).show()
                }
            }




    }

    //voice command implementation

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            // Handle the exception (e.g., show a message to the user)
            Toast.makeText(this, "Voice recognition not available on this device", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (results != null && results.isNotEmpty()) {
                val spokenText = results[0].toLowerCase(Locale.getDefault())
                processVoiceCommand(spokenText)
            }
        }
    }

    private fun processVoiceCommand(command:String){
        when(command){
            "add item" -> {
                intent = Intent(applicationContext, AddItem::class.java)
                startActivity(intent)
            }
            "cancel" -> {
                intent = Intent(applicationContext, CustomerDashboard::class.java)
                startActivity(intent)
            }
            "submit" ->{
                submitCart()


            }
        }
    }


}