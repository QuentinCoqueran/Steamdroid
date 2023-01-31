package com.example.steamdroid.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.*
import com.example.steamdroid.databinding.HomeBinding
import com.example.steamdroid.game_details.GameDetailsRequest
import com.example.steamdroid.R
import com.example.steamdroid.favoris.FavoritesFragment
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import com.example.steamdroid.search.SearchGame
import com.example.steamdroid.search.SearchGameActivity
import com.google.firebase.auth.FirebaseAuth
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
                for (i in bestSellersResponse!!.response.ranks) {
                    GameDetailsRequest().getGame(i.appid, lang, currency) { game ->
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
                        if (count == bestSellersResponse.response.ranks.size) {
                            isFinished = true
                            productGameList = products.toMutableList()
                            val adapter = ProductAdapter(products)
                            recyclerView.adapter = adapter
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
            //val intent = Intent(this, SearchGameActivity::class.java)
            //startActivity(intent)
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
}
