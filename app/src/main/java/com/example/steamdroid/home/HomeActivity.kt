package com.example.steamdroid.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.*
import com.example.steamdroid.databinding.HomeBinding
import com.example.steamdroid.game_details.GameDetailsRequest
import com.example.steamdroid.R
import com.example.steamdroid.SignInActivity
import com.example.steamdroid.databinding.HomeBinding
import com.example.steamdroid.favoris.FavoritesActivity
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import com.example.steamdroid.wishlist.WishListActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class HomeActivity : Activity() {
    private val handler = Handler()
    private var isFinished = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //isConnected()
        setContentView(R.layout.home)
        //LOADER
        isFinished = false
        //RECYCLER VIEW
        val binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        //////////////////////////////////////////

        val btnWishList: ImageView = findViewById(R.id.image_view_star)
        btnWishList.setOnClickListener {
            val intent = Intent(this, WishListActivity::class.java)
            startActivity(intent)
        }
        val btnFavorite: ImageView = findViewById(R.id.image_view_like)
        btnFavorite.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

        val apiClient = BestsellersApiSteam()
        val currentLocale = Locale.getDefault().language
        val lang = if (currentLocale == "fr") "french" else "english"
        val currency = if (currentLocale == "fr") "fr" else "us"
        apiClient.getResponse() { bestSellersResponse ->
            showWaitingDots()
            val products : List<Product> = listOf()
            for (i in bestSellersResponse!!.response.ranks) {
                GameDetailsRequest().getGame(i.appid, lang, currency) { game ->
                    if (game != null) {
                        products.plus(
                            Product(
                                game.gameName.orEmpty(),
                                game.price.orEmpty(),
                                game.backGroundImg.orEmpty(),
                                game.editorName.orEmpty(),
                                game.backGroundImgTitle.orEmpty()
                            )
                        )
                    }
                    count++
                    if (count == bestSellersResponse.response.ranks.size) {
                        isFinished = true
                        val adapter = ProductAdapter(products)
                        recyclerView.adapter = adapter
                    }
                }
            }
            val adapter = ProductAdapter(products)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = ProductAdapter(products)
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
        val binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchInput = findViewById<View>(R.id.search_input)
        searchInput.setOnClickListener {
            println("search input clicked")
            val intent = Intent(this, SearchGameActivity::class.java)
            startActivity(intent)
        }


    }

    private fun isConnected() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    fun showWaitingDots() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBarHome)
        progressBar.visibility = View.VISIBLE
        updateDots()
    }


    private fun updateDots() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBarHome)
        if (isFinished) {
            progressBar.visibility = View.GONE
            return
        }
        handler.postDelayed({ updateDots() }, 1000)
    }
}
