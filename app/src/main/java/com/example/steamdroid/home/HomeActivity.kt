package com.example.steamdroid.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.*
import com.example.steamdroid.databinding.HomeBinding
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : Activity() {

    companion object {
        var searchGameList = mutableListOf<SearchGame>()
        var isLoaded = false
        var inProgress = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isConnected()
        setContentView(R.layout.home)


        if (!isLoaded){
            inProgress = true
            SearchGameRequest().searchGame(){ list ->
                if (list != null) {
                    searchGameList = list
                    inProgress = false
                    isLoaded = true
                }else{
                    inProgress = false
                }
            }
        }

        println("HOME ACTIVITY");

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val apiClient = BestsellersApiSteam()
        apiClient.getResponse() { bestSellersResponse ->
            val products: List<Product> = listOf();
            for (i in bestSellersResponse!!.response.ranks) {
                GameDetailsRequest().getGame(i.appid) { game ->
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


    }

    private fun isConnected() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}
