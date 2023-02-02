package com.example.steamdroid.home

import RetrofitBuilder
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.*
import com.example.steamdroid.databinding.HomeBinding
import com.example.steamdroid.game_details.GameDetailsRequest
import com.example.steamdroid.R
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import com.example.steamdroid.search.SearchGame
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import java.util.*


class HomeFragment : Fragment() {
    private val handler = Handler()
    private var isFinished = true
    private lateinit var navController: NavController

    companion object {
        var searchGameList = mutableListOf<SearchGame>()
        var productGameList = mutableListOf<Product>()
        var isLoaded = false
        var inProgress = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        //isConnected()
        //LOADER
        isFinished = false

        if (!isLoaded){
            inProgress = true
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    searchGameList = withContext(Dispatchers.Default) {
                        RetrofitBuilder.searchGameService.searchGame().await()
                    }
                    println("RESPONSE SUCESS !")
                    println("searchGameList: $searchGameList")

                    isLoaded = true
                    inProgress = false
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        //RECYCLER VIEW
        val binding = HomeBinding.inflate(layoutInflater)

        //setOn(binding.root)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        //////////////////////////////////////////

        val btnWishList: ImageView = view.findViewById(R.id.image_view_star)
        btnWishList.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_wishListFragment)
        }
        val btnFavorite: ImageView = view.findViewById(R.id.image_view_like)
        btnFavorite.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_favoritesFragment)
        }

        val apiClient = BestsellersApiSteam()
        val currentLocale = Locale.getDefault().language
        val lang = if (currentLocale == "fr") "french" else "english"
        val currency = if (currentLocale == "fr") "fr" else "us"
        var count = 0
        var products: List<Product> = listOf()
        if (productGameList.isEmpty()) {
            apiClient.getResponse() { bestSellersResponse ->
                showWaitingDots()
                GlobalScope.launch(Dispatchers.Main){
                    for (i in bestSellersResponse!!.response.ranks) {


                        try {
                            val game = withContext(Dispatchers.Default){
                                delay(2000)
                                RetrofitBuilder.gameDetailsService.getGame(i.appid, lang, currency).await()
                            }

                            if (game.gameName != null && game.editorName != null) {

                                println("gameName: ${game.gameName}")

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
                            if (count == bestSellersResponse.response.ranks.size) {
                                isFinished = true
                                productGameList = products.toMutableList()
                                setRecycler(productGameList)
                            }

                        } catch (e: Exception) {
                            println("error : ${e.message}")

                            products = products.plus(
                                Product(
                                    "No name",
                                    "0",
                                    "https://play-lh.googleusercontent.com/YUBDky2apqeojcw6eexQEpitWuRPOK7kPe_UbqQNv-A4Pi_fXm-YQ8vTCwPKtxIPgius",
                                    listOf("Probably got kick of API"),
                                    "Unknown"
                                )
                            )

                            count++

                            if (count == bestSellersResponse.response.ranks.size) {
                                isFinished = true
                                productGameList = products.toMutableList()
                                setRecycler(productGameList)
                            }
                        }

                    }
                }
            }
        } else {
            isFinished = true
            val adapter = ProductAdapter(productGameList)
            recyclerView.adapter = adapter
        }


        val searchInput = view.findViewById<View>(R.id.search_input)
        searchInput.setOnClickListener {
            println("search input clicked")
            navController.navigate(R.id.action_homeFragment_to_searchGameFragment)
        }
    }

    private fun isConnected() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null) {
            navController.navigate(R.id.action_homeFragment_to_signInFragment)
        }
    }

    fun showWaitingDots() {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBarHome)
        if (progressBar != null) {
            progressBar.visibility = View.VISIBLE
        }
        updateDots()
    }


    private fun updateDots() {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBarHome)
        if (isFinished) {
            if (progressBar != null) {
                progressBar.visibility = View.GONE
            }
            return
        }
        handler.postDelayed({ updateDots() }, 1000)
    }

    @WorkerThread
    fun setRecycler(list: MutableList<Product>) {
        ContextCompat.getMainExecutor(this.requireContext()).execute {
            // This is where your UI code goes.
            println("FINISH !")
            val recyclerView = this.requireView().findViewById<RecyclerView>(R.id.recycler_view)

            println("products GET")

            isFinished = true

            println(" 1 products size : ${list.size}")

            recyclerView.adapter = ProductAdapter(list as List<Product>)
            println("adapter set")
            recyclerView.layoutManager = LinearLayoutManager(this@HomeFragment.context)
            println("layout set")
            recyclerView.setHasFixedSize(true)
            println("has fixed size")
        }
    }
}