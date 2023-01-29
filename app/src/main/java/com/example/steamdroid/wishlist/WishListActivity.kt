package com.example.steamdroid.wishlist

import android.app.Activity
import android.os.Bundle
import com.example.steamdroid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class WishListActivity : Activity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        println("TATA")
        getWishList()
        println("titi")
        setContentView(R.layout.wishlist)
    }

    private fun getWishList() {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("steamandroid").whereEqualTo("username", "papa")
        docRef.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                println("test")
            } else {
                println("test")
            }
        }
    }
}
