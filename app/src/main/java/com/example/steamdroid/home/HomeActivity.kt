package com.example.steamdroid.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.BestsellersApiSteam
import com.example.steamdroid.game_details.GameDetailsRequest
import com.example.steamdroid.R
import com.example.steamdroid.SignInActivity
import com.example.steamdroid.databinding.HomeBinding
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class HomeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //isConnected()
        val apiClient = BestsellersApiSteam()
        var count = 0;
        var products: List<Product> = listOf();
        val currentLocale = Locale.getDefault().language
        val lang = if (currentLocale == "fr") "french" else "english"
        val currency = if (currentLocale == "fr") "fr" else "us"
        apiClient.getResponse() { bestSellersResponse ->
            for (i in bestSellersResponse!!.response.ranks) {
                GameDetailsRequest().getGame(i.appid, lang, currency) { game ->
                    if (game != null && game.gameName != null && game.editorName != null) {
                        products = products.plus(
                            Product(
                                game.gameName.orEmpty(),
                                game.price.orEmpty(),
                                game.backGroundImg.orEmpty(),
                                game.editorName.orEmpty()[0],
                                game.backGroundImgTitle.orEmpty()
                            )
                        )
                    }
                    count++
                    if (count == bestSellersResponse.response.ranks.size) {
                        setContentView(R.layout.home)
                        val binding = HomeBinding.inflate(layoutInflater)
                        setContentView(binding.root)
                        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
                        val adapter = ProductAdapter(products)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        recyclerView.setHasFixedSize(true)
                    }
                }
            }
        }
    }

    private fun isConnected() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}
