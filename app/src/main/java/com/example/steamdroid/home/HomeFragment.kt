package com.example.steamdroid.home

import RetrofitBuilder
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
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
import com.example.steamdroid.favoris.FavoritesFragment
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
        var localList = mutableListOf<Rank>()
        var isLoaded = false
        var inProgress = false
        var needSuspend = false
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

        isConnected()
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

                localList = bestSellersResponse!!.response.ranks as MutableList<Rank>

                var max = 5

                var doCancel = false

                if (localList.size in 1..4) {
                    max = localList.size
                } else if (localList.size == 0) {
                    isFinished = true
                    Toast.makeText(context, "No more games", Toast.LENGTH_SHORT)
                        .show()
                    doCancel = true
                }

                val sendList = localList.subList(0, max).toMutableList()

                localList.subList(0, max).clear()




                if(!doCancel) {
                    GlobalScope.launch(Dispatchers.Main) {
                        for (i in sendList) {


                            try {
                                val game = withContext(Dispatchers.Default) {
                                    delay(500)
                                    RetrofitBuilder.gameDetailsService.getGame(
                                        i.appid,
                                        lang,
                                        currency
                                    ).await()
                                }

                                if (game.gameName != null && game.editorName != null) {

                                    println("gameName: ${game.gameName}")

                                    products = products.plus(
                                        Product(
                                            id,
                                            game.gameName.orEmpty(),
                                            game.price.orEmpty(),
                                            game.backGroundImg.orEmpty(),
                                            game.editorName.orEmpty(),
                                            game.backGroundImgTitle.orEmpty()
                                        )
                                    )
                                }
                                count++
                                if (count == sendList.size) {
                                    isFinished = true
                                    productGameList = products.toMutableList()
                                    setRecycler(productGameList)
                                }

                            } catch (e: Exception) {
                                println("error : ${e.message}")

                                products = products.plus(
                                    Product(
                                        id,
                                        "No name",
                                        "0",
                                        "https://play-lh.googleusercontent.com/YUBDky2apqeojcw6eexQEpitWuRPOK7kPe_UbqQNv-A4Pi_fXm-YQ8vTCwPKtxIPgius",
                                        listOf("Probably got kick of API"),
                                        "Unknown"
                                    )
                                )

                                count++

                                if (count == sendList.size) {
                                    isFinished = true
                                    productGameList = products.toMutableList()
                                    setRecycler(productGameList)
                                }
                            }

                        }
                    }
                }
            }
        } else {
            isFinished = true
            val adapter = ProductAdapter(productGameList, this)
            recyclerView.adapter = adapter
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    println("end of list")

                    if (!isFinished){
                        return
                    }

                    isFinished = false
                    showWaitingDots()

                    var max = 5

                    if (localList.size in 1..4) {
                        max = localList.size
                    } else if (localList.size == 0) {
                        isFinished = true
                        Toast.makeText(context, "No more games", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }

                    val list = localList.subList(0, max).toMutableList()
                    localList.subList(0, max).clear()
                    val newList = mutableListOf<Product>()

                    var cpt = 0

                    GlobalScope.launch(Dispatchers.Main) {
                        for (i in list) {

                            try {
                                val game = withContext(Dispatchers.Default) {
                                    delay(500)
                                    RetrofitBuilder.gameDetailsService.getGame(
                                        i.appid,
                                        lang,
                                        currency
                                    ).await()
                                }

                                if (game.gameName != null && game.editorName != null) {

                                    println("gameName: ${game.gameName}")

                                    newList.add(
                                        Product(
                                            i.appid,
                                            game.gameName.orEmpty(),
                                            game.price.orEmpty(),
                                            game.backGroundImg.orEmpty(),
                                            game.editorName.orEmpty(),
                                            game.backGroundImgTitle.orEmpty()
                                        )
                                    )
                                }
                                cpt++
                                println("count: $cpt / ${list.size}")
                                if (cpt == list.size) {
                                    isFinished = true
                                    updateRecycler(newList)

                                    productGameList.addAll(newList)
                                }

                            } catch (e: Exception) {
                                println("error : ${e.message}")

                                newList.plus(
                                    Product(
                                        i.appid,
                                        "No name",
                                        "0",
                                        "https://play-lh.googleusercontent.com/YUBDky2apqeojcw6eexQEpitWuRPOK7kPe_UbqQNv-A4Pi_fXm-YQ8vTCwPKtxIPgius",
                                        listOf("Probably got kick of API"),
                                        "Unknown"
                                    )
                                )

                                cpt++
                                println("count: $cpt / ${list.size}")
                                if (cpt == list.size) {
                                    isFinished = true
                                    updateRecycler(newList)
                                    productGameList.addAll(newList)
                                }
                            }

                        }

                    }

                }
            }
        })


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
            recyclerView.adapter = ProductAdapter(list as List<Product>, this)
            println("adapter set")
            recyclerView.layoutManager = LinearLayoutManager(this@HomeFragment.context)
            println("layout set")
            recyclerView.setHasFixedSize(true)
            println("has fixed size")
        }
    }

    @WorkerThread
    fun updateRecycler(list: MutableList<Product>) {
        ContextCompat.getMainExecutor(this.requireContext()).execute {



            GlobalScope.launch(Dispatchers.Main) {
                val recyclerView = withContext(Dispatchers.Default){
                    while (needSuspend)
                        delay(500)
                     this@HomeFragment.requireView().findViewById<RecyclerView>(R.id.recycler_view)

                }

                // This is where your UI code goes.
                println("FINISH !")

                println("products GET")

                isFinished = true

                val adapter = recyclerView.adapter as ProductAdapter

                list.forEach() {
                    println("GET name: ${it.gameName}")
                }

                //adapter.updateProducts(list)
                println("OMG !!!!!")
                adapter.notifyItemRangeInserted(adapter.itemCount, list.size)

            }


        }
    }
}
