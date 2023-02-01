package com.example.steamdroid.home

import RetrofitBuilder
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.*
import com.example.steamdroid.databinding.HomeBinding
import com.example.steamdroid.game_details.GameDetailsRequest
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import com.example.steamdroid.search.SearchGame
import com.example.steamdroid.search.SearchGameActivity
import com.example.steamdroid.search.SearchGameTypeAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory

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
            println("entered")

            GlobalScope.launch(Dispatchers.Default) {

                try {
                    val result = withContext(Dispatchers.Default) {
                        RetrofitBuilder.searchGameService.searchGame().await()
                    }
                    //searchGameList = result.response.apps as MutableList<SearchGame>
                    searchGameList = result as MutableList<SearchGame>
                    println("RECEIVE RESPONSE")
                    println(searchGameList)
                    //println(result.response.apps)
                } catch (e: Exception) {
                    println("ERROR")
                    println(e)
                }


                inProgress = false
                isLoaded = true
            }

        }

        println("HOME ACTIVITY")

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


}
