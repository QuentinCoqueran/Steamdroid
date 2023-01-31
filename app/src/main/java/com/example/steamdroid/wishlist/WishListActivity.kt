package com.example.steamdroid.wishlist

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.R
import com.example.steamdroid.databinding.HomeBinding
import com.example.steamdroid.databinding.WishlistBinding
import com.example.steamdroid.game_details.GameDetailsRequest
import com.example.steamdroid.home.BestSellersResponse
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.*


class WishListActivity : Activity() {
    private val handler = Handler()
    private var isFinished = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wishlist)
        //LOADER
        isFinished = false
        //RECYCLER VIEW
        val binding = WishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_wishlist)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        //////////////////////////////////////////
        var count = 0;
        var products: List<Product> = listOf();
        val currentLocale = Locale.getDefault().language
        val lang = if (currentLocale == "fr") "french" else "english"
        getWishList() { it ->
            showWaitingDots()
            if (it != null) {
                for (id in it) {
                    GameDetailsRequest().getGame(id, lang) { game ->
                        if (game?.gameName != null && game.editorName != null) {
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
                        if (count == it.size) {
                            val txtMyLikes = findViewById<TextView>(R.id.text_view_likes)
                            txtMyLikes.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            val adapter = ProductAdapter(products)
                            recyclerView.adapter = adapter
                            var closeBtn: ImageView = findViewById(R.id.close_wishlist_btn)
                            closeBtn.setOnClickListener {
                                finish()
                            }
                        }
                    }
                }
            }
            isFinished = true
        }
        val closeBtn: ImageView = findViewById(R.id.close_wishlist_btn)
        closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun getWishList(callback: (List<Number>?) -> Unit) {
        var wishListId = listOf<Number>();
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("wishlist")
        docRef.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                for (document in documents) {
                    wishListId = wishListId.plus(document.get("id") as Number)
                }
                callback(wishListId)
            }
        }
    }

    fun showWaitingDots() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBarWishlist)
        progressBar.visibility = View.VISIBLE
        updateDots()
    }


    private fun updateDots() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBarWishlist)
        if (isFinished) {
            progressBar.visibility = View.GONE
            return
        }
        handler.postDelayed({ updateDots() }, 1000)
    }
}
