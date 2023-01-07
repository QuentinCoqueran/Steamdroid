package com.example.steamdroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.databinding.HomeBinding
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import com.google.firebase.auth.FirebaseAuth


class HomeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isConnected()
        setContentView(R.layout.home)
        BestsellersApiSteam().getResponse()
        val binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //val recyclerView = binding.list
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val product = Product(
            "Counter-Strike: Global Offensive",
            14.99,
            "https://steamcdn-a.akamaihd.net/steam/apps/570/header.jpg?t=1603289340",
            "Ankama",
            "https://steamcdn-a.akamaihd.net/steam/apps/730/header.jpg?t=1603289340"
        )
        val products = listOf(
            product,
            product,
            product,
            product,
            product,
            product
        )
        recyclerView.adapter = ProductAdapter(products)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun isConnected() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}
