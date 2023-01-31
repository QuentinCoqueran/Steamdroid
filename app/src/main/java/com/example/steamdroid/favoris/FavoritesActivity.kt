package com.example.steamdroid.favoris

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.R
import com.example.steamdroid.databinding.FavorisBinding
import com.example.steamdroid.game_details.GameDetailsRequest
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FavoritesActivity : Activity() {
    private val handler = Handler()
    private var isFinished = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favoris)
        //LOADER
        isFinished = false
        //RECYCLER VIEW
        val binding = FavorisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_favoris)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        //////////////////////////////////////////
        var count = 0;
        var products: List<Product> = listOf();
        val currentLocale = Locale.getDefault().language
        val lang = if (currentLocale == "fr") "french" else "english"
        val currency = if (currentLocale == "fr") "fr" else "us"
        getFavoritesListId() { it ->
            showWaitingDots()
            if (it != null) {
                for (id in it) {
                    GameDetailsRequest().getGame(id, lang,currency) { game ->
                        if (game?.gameName != null && game.editorName != null) {
                            products = products.plus(
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
                        if (count == it.size) {
                            val linearMyLikes = findViewById<LinearLayout>(R.id.linear_layout_likes)
                            linearMyLikes.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            val closeBtn: ImageView = findViewById(R.id.close_favoris)
                            closeBtn.setOnClickListener {
                                finish()
                            }
                            val adapter = ProductAdapter(products)
                            recyclerView.adapter = adapter
                        }
                    }
                }
            }
            isFinished = true
        }
        val closeBtn: ImageView = findViewById(R.id.close_favoris)
        closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun getFavoritesListId(callback: (List<Number>?) -> Unit) {
        var favoritesListId = listOf<Number>();
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("favorites")
        docRef.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                for (document in documents) {
                    favoritesListId = favoritesListId.plus(document.get("id") as Number)
                }
                callback(favoritesListId)
            }
        }
    }

    fun showWaitingDots() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBarFavorites)
        progressBar.visibility = View.VISIBLE
        updateDots()
    }


    private fun updateDots() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBarFavorites)
        if (isFinished) {
            progressBar.visibility = View.GONE
            return
        }
        handler.postDelayed({ updateDots() }, 1000)
    }
}