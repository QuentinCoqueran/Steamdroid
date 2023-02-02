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
import com.example.steamdroid.R
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import com.example.steamdroid.search.SearchGame
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import retrofit2.await
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
        return inflater.inflate(R.layout.home, container, false)
    }
    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        isConnected()
        //LOADER
        isFinished = false
        if (!isLoaded) {
            inProgress = true
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    searchGameList = withContext(Dispatchers.Default) {
                        RetrofitBuilder.searchGameService.searchGame().await()
                    }
                    isLoaded = true
                    inProgress = false
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        //RECYCLER
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_home)
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


        val currentLocale = Locale.getDefault().language
        val lang = if (currentLocale == "fr") "french" else "english"
        val currency = if (currentLocale == "fr") "fr" else "us"
        var count = 0
        var products: List<Product> = listOf()

        if (productGameList.isEmpty()) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val bestSellersResponse = withContext(Dispatchers.Default) {
                        RetrofitBuilder.bestsellersService.getBestsellers().await()
                    }
                    showWaitingDots()
                    localList = bestSellersResponse.response.ranks as MutableList<Rank>
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
                    if (!doCancel) {
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
                } catch (e: Exception) {
                    e.printStackTrace()
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
                    if (!isFinished) {
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
                                if (cpt == list.size) {
                                    isFinished = true
                                    updateRecycler(newList)

                                    productGameList.addAll(newList)
                                }

                            } catch (e: Exception) {
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
            val recyclerView = this@HomeFragment.view?.findViewById<RecyclerView>(R.id.recycler_view_home)
            isFinished = true
            if (recyclerView != null) {
                recyclerView.adapter = ProductAdapter(list as List<Product>, this)
            }
            if (recyclerView != null) {
                recyclerView.layoutManager = LinearLayoutManager(this@HomeFragment.context)
            }
            recyclerView?.setHasFixedSize(true)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @WorkerThread
    fun updateRecycler(list: MutableList<Product>) {
        ContextCompat.getMainExecutor(this.requireContext()).execute {
            GlobalScope.launch(Dispatchers.Main) {
                val recyclerView = withContext(Dispatchers.Default) {
                    while (needSuspend)
                        delay(500)
                    this@HomeFragment.requireView().findViewById<RecyclerView>(R.id.recycler_view_home)

                }
                // This is where your UI code goes.
                isFinished = true
                val adapter = recyclerView.adapter as ProductAdapter
                adapter.notifyItemRangeInserted(adapter.itemCount, list.size)

            }
        }
    }
}
