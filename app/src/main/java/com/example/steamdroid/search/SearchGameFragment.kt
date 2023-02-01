package com.example.steamdroid.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.R
import com.example.steamdroid.home.HomeActivity
import com.example.steamdroid.home.HomeActivity.Companion.inProgress
import com.example.steamdroid.home.HomeActivity.Companion.isLoaded
import com.example.steamdroid.home.HomeActivity.Companion.searchGameList
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchGameFragment : Fragment() {

    private val handler = Handler()
    private var isFinished = true
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        println("SEARCH ACTIVITY")

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        showWaitingDots()

        var actualList = mutableListOf<SearchGame>()

        var multiplier = 1
        val cross = view.findViewById<ImageView>(R.id.white_cross)

        cross.setOnClickListener {
            navController.navigateUp();
        }

        println("start search")
        /*
        if (!isLoaded && !inProgress) {
            inProgress = true
            val searchGameApi = SteamApiHelperImpl(RetrofitBuilder.searchGameService)
            GlobalScope.launch {
                val result = searchGameApi.searchGame()
                searchGameList = result
                inProgress = false
                isLoaded = true
            }
        }*/

        val searchInput = view.findViewById<EditText>(R.id.search_input)

        if (searchInput.requestFocus()) {
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }

        searchInput.setOnKeyListener { _, keyCode, event ->
            if (isLoaded && !inProgress) {
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {

                    actualList.clear()

                    isFinished = false
                    showWaitingDots()

                    println("Enter pressed")
                    val search = searchInput.text.toString()

                    val list: MutableList<SearchGame> = searchGameList.filter {
                        it.appName!!.contains(
                            search,
                            true
                        )

                    } as MutableList<SearchGame>

                    actualList = list

                    println("list size : ${list.size}")

                    var max = 5

                    if (list.size in 1..4) {
                        max = list.size
                    } else if (list.size == 0) {
                        Toast.makeText(context, "No game found", Toast.LENGTH_SHORT).show()
                        isFinished = true
                        return@setOnKeyListener false
                    }

                    val sendList = list.subList(0, max).toMutableList()

                    list.subList(0, max).clear()

                    actualList = list

                    /*getProduct(sendList) { products ->
                        multiplier++

                        println("products GET")

                        isFinished = true

                        recyclerView.adapter = null

                        val adapter = ProductAdapter(products!!)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        recyclerView.setHasFixedSize(true)

                    }*/

                }

            } else {
                showWaitingDots()
            }
            false
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    println("end of list")

                    isFinished = false
                    showWaitingDots()

                    var max = 5

                    if (actualList.size in 1..4) {
                        max = actualList.size
                    } else if (actualList.size == 0) {
                        isFinished = true
                        Toast.makeText(context, "No more games", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }

                    val list = actualList.subList(0, max).toMutableList()
                    actualList.subList(0, max).clear()
                    /*getProduct(list) { products ->
                        multiplier++
                        println("products GET")
                        isFinished = true

                        val adapter = recyclerView.adapter as ProductAdapter

                        adapter.updateProducts(products!!)
                        adapter.notifyItemRangeInserted(adapter.itemCount, products.size)
                    }*/

                }
            }
        })

    }

    fun getProduct(list: MutableList<SearchGame>, callback: (MutableList<Product>?) -> Unit) {
        val productList = mutableListOf<Product>()
        var cpt = 0
        println("getProduct")

        for (i in list) {
                val gameDetailsService = SteamApiHelperImpl(RetrofitBuilder.gameDetailsService)
                GlobalScope.launch {
                    val result = gameDetailsService.gameDetails(i.appId!!)

                    println("gameDetails : ${result.gameName}")
                    //println("gameDetails : ${gameDetails.price}")
                    //println("gameDetails : ${gameDetails.backGroundImg}")
                    //println("gameDetails : ${gameDetails.editorName}")
                    //println("gameDetails : ${gameDetails.backGroundImgTitle}")

                    if (result.backGroundImg!!.isEmpty()){
                        result.backGroundImg = "https://play-lh.googleusercontent.com/YUBDky2apqeojcw6eexQEpitWuRPOK7kPe_UbqQNv-A4Pi_fXm-YQ8vTCwPKtxIPgius"
                    }

                    productList.add(
                        Product(
                            result.gameName.orEmpty(),
                            result.price.orEmpty(),
                            result.backGroundImg.orEmpty(),
                            result.editorName.orEmpty(),
                            result.backGroundImgTitle.orEmpty()
                        )
                    )
                    cpt++
                    if (cpt == list.size) {
                        callback(productList)
                    }

                    inProgress = false
                    isLoaded = true
                }

        }
    }

    fun showWaitingDots() {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBar)
        if (progressBar != null) {
            progressBar.visibility = View.VISIBLE
        }
        updateDots()
    }

    private fun updateDots() {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBar)
        if (isFinished && isLoaded && !inProgress) {
            if (progressBar != null) {
                progressBar.visibility = View.GONE
            }
            return
        }
        handler.postDelayed({ updateDots() }, 1000)
    }


}