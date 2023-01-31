package com.example.steamdroid.search

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.steamdroid.R
import com.example.steamdroid.game_details.GameDetailsRequest
import com.example.steamdroid.home.HomeFragment
import com.example.steamdroid.home.HomeFragment.Companion.inProgress
import com.example.steamdroid.home.HomeFragment.Companion.isLoaded
import com.example.steamdroid.home.HomeFragment.Companion.searchGameList
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import java.util.*

class SearchGameActivity : Activity() {

    private val handler = Handler()
    private var isFinished = true

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_game)

        println("SEARCH ACTIVITY")

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        showWaitingDots()

        var actualList = mutableListOf<SearchGame>()

        var multiplier = 1
        val cross = findViewById<ImageView>(R.id.white_cross)

        cross.setOnClickListener {
            finish()
            val intent = Intent(this, HomeFragment::class.java)
            startActivity(intent)
        }

        println("start search")

        if (!isLoaded && !inProgress) {
            SearchGameRequest().searchGame { list ->
                if (list != null) {
                    println("list GET")
                    //products = list
                    searchGameList = list

                }
            }
        }

        val searchInput = findViewById<EditText>(R.id.search_input)

        if (searchInput.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
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

                    if (list.size in 1..4)
                    {
                        max = list.size
                    }else if (list.size == 0)
                    {
                        Toast.makeText(this, "No game found", Toast.LENGTH_SHORT).show()
                        isFinished = true
                        return@setOnKeyListener false
                    }

                    val sendList = list.subList(0, max).toMutableList()

                    list.subList(0, max).clear()

                    actualList = list

                    getProduct(sendList) { products ->
                        multiplier++

                        println("products GET")

                        isFinished = true

                        recyclerView.adapter = null

                        val adapter = ProductAdapter(products!!)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        recyclerView.setHasFixedSize(true)

                    }

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

                    if (actualList.size in 1..4 ) {
                        max = actualList.size
                    }else if (actualList.size == 0) {
                        isFinished = true
                        Toast.makeText(this@SearchGameActivity, "No more games", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val list = actualList.subList(0, max).toMutableList()
                    actualList.subList(0, max).clear()
                    getProduct(list) { products ->
                        multiplier++
                        println("products GET")
                        isFinished = true

                        val adapter = recyclerView.adapter as ProductAdapter

                        adapter.updateProducts(products!!)
                        adapter.notifyItemRangeInserted(adapter.itemCount, products.size)
                    }

                }
            }
        })

    }

    fun getProduct(list: MutableList<SearchGame>, callback: (MutableList<Product>?) -> Unit) {
        val productList = mutableListOf<Product>()
        var cpt = 0
        println("getProduct")
        val currentLocale = Locale.getDefault().language
        val lang = if (currentLocale == "fr") "french" else "english"
        val currency = if (currentLocale == "fr") "fr" else "us"

        for (i in list) {
            handler.postDelayed({
                GameDetailsRequest().getGame(i.appId!!,lang ,currency) { gameDetails ->
                    if (gameDetails != null) {

                        println("gameDetails : ${gameDetails.gameName}")
                        //println("gameDetails : ${gameDetails.price}")
                        //println("gameDetails : ${gameDetails.backGroundImg}")
                        //println("gameDetails : ${gameDetails.editorName}")
                        //println("gameDetails : ${gameDetails.backGroundImgTitle}")

                        if (gameDetails.backGroundImg!!.isEmpty()){
                            gameDetails.backGroundImg = "https://play-lh.googleusercontent.com/YUBDky2apqeojcw6eexQEpitWuRPOK7kPe_UbqQNv-A4Pi_fXm-YQ8vTCwPKtxIPgius"
                        }

                        productList.add(
                            Product(
                                gameDetails.gameName.orEmpty(),
                                gameDetails.price.orEmpty(),
                                gameDetails.backGroundImg.orEmpty(),
                                gameDetails.editorName.orEmpty(),
                                gameDetails.backGroundImgTitle.orEmpty()
                            )
                        )
                    } else {
                        println("NULL")
                        productList.add(
                            Product(
                                i.appName.orEmpty(),
                                "0",
                                "https://static.vecteezy.com/system/resources/previews/002/326/623/original/black-golden-royal-luxury-background-landing-page-free-vector.jpg",
                                listOf("Probably got kicked of API ..."),
                                "https://media.istockphoto.com/id/1352152504/fr/vectoriel/fond-g%C3%A9om%C3%A9trique-abstrait-noir-fonc%C3%A9-d%C3%A9coup%C3%A9-arri%C3%A8re-plan-futuriste-moderne-peut-%C3%AAtre.jpg?s=612x612&w=0&k=20&c=41U5gfwOM5zcOLRNjfWGMoKzDAGGto-8dD54CFBQC4s="
                            )
                        )
                    }
                    cpt++
                    if (cpt == list.size) {
                        callback(productList)
                    }
                }
            }, 1000)

        }
    }

    fun showWaitingDots() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        updateDots()
    }

    private fun updateDots() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        if (isFinished && isLoaded && !inProgress) {
            progressBar.visibility = View.GONE
            return
        }
        handler.postDelayed({ updateDots() }, 1000)
    }



}