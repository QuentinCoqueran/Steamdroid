package com.example.steamdroid.search

import RetrofitBuilder
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.EditText
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
import com.example.steamdroid.home.HomeFragment.Companion.inProgress
import com.example.steamdroid.home.HomeFragment.Companion.isLoaded
import com.example.steamdroid.home.HomeFragment.Companion.searchGameList
import com.example.steamdroid.main.MainActivity
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import kotlinx.coroutines.*
import java.util.*


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

        val currentLocale = Locale.getDefault().language
        val lang = if (currentLocale == "fr") "french" else "english"
        val currency = if (currentLocale == "fr") "fr" else "us"

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

                    // ===========================================

                    val products = mutableListOf<Product>()
                    var cpt = 0
                    println("getProduct")
                    GlobalScope.launch(Dispatchers.Main) {
                        for (i in sendList) {

                            try {
                                val result = withContext(Dispatchers.Default) {
                                    println("wait for await !")
                                    delay(500)
                                    RetrofitBuilder.gameDetailsService.getGame(i.appId!!, lang, currency).await()
                                }
                                println("wait for await  ?")
                                println("gameDetails : ${result.gameName}")
                                //println("gameDetails : ${gameDetails.price}")
                                //println("gameDetails : ${gameDetails.backGroundImg}")
                                //println("gameDetails : ${gameDetails.editorName}")
                                //println("gameDetails : ${gameDetails.backGroundImgTitle}")

                                if (result.backGroundImg!!.isEmpty()){
                                    result.backGroundImg = "https://play-lh.googleusercontent.com/YUBDky2apqeojcw6eexQEpitWuRPOK7kPe_UbqQNv-A4Pi_fXm-YQ8vTCwPKtxIPgius"
                                }

                                products.add(
                                    Product(
                                        i.appId,
                                        result.gameName.orEmpty(),
                                        result.price.orEmpty(),
                                        result.backGroundImg.orEmpty(),
                                        result.editorName.orEmpty(),
                                        result.backGroundImgTitle.orEmpty()
                                    )
                                )

                                inProgress = false
                                isLoaded = true
                                cpt++


                            }catch (e: Exception){
                                println("error : ${e.message}")

                                products.add(
                                    Product(
                                        i.appId,
                                        i.appName.orEmpty(),
                                        "0",
                                        "https://static.vecteezy.com/system/resources/previews/002/326/623/original/black-golden-royal-luxury-background-landing-page-free-vector.jpg",
                                        listOf("Probably got kicked of API ..."),
                                        "https://media.istockphoto.com/id/1352152504/fr/vectoriel/fond-g%C3%A9om%C3%A9trique-abstrait-noir-fonc%C3%A9-d%C3%A9coup%C3%A9-arri%C3%A8re-plan-futuriste-moderne-peut-%C3%AAtre.jpg?s=612x612&w=0&k=20&c=41U5gfwOM5zcOLRNjfWGMoKzDAGGto-8dD54CFBQC4s="
                                    )
                                )

                                cpt++

                            }

                            if (cpt == sendList.size) {
                                println("end of loop")
                                setRecycler(products)
                            }

                        }

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

                    if (!isFinished){
                        return
                    }

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
                    val newList = mutableListOf<Product>()

                    var cpt = 0

                    GlobalScope.launch(Dispatchers.Main) {
                        for (i in list) {

                            try {
                                val result = withContext(Dispatchers.Default) {
                                    println("wait for await !")
                                    delay(500)
                                    RetrofitBuilder.gameDetailsService.getGame(i.appId!!, lang, currency).await()
                                }
                                println("wait for await  ?")
                                println("gameDetails : ${result.gameName}")
                                //println("gameDetails : ${gameDetails.price}")
                                //println("gameDetails : ${gameDetails.backGroundImg}")
                                //println("gameDetails : ${gameDetails.editorName}")
                                //println("gameDetails : ${gameDetails.backGroundImgTitle}")

                                if (result.backGroundImg!!.isEmpty()){
                                    result.backGroundImg = "https://play-lh.googleusercontent.com/YUBDky2apqeojcw6eexQEpitWuRPOK7kPe_UbqQNv-A4Pi_fXm-YQ8vTCwPKtxIPgius"
                                }

                                newList.add(
                                    Product(
                                        i.appId,
                                        result.gameName.orEmpty(),
                                        result.price.orEmpty(),
                                        result.backGroundImg.orEmpty(),
                                        result.editorName.orEmpty(),
                                        result.backGroundImgTitle.orEmpty()
                                    )
                                )

                                inProgress = false
                                isLoaded = true
                                cpt++


                            }catch (e: Exception){
                                println("error : ${e.message}")

                                newList.add(
                                    Product(
                                        i.appId,
                                        i.appName.orEmpty(),
                                        "0",
                                        "https://play-lh.googleusercontent.com/YUBDky2apqeojcw6eexQEpitWuRPOK7kPe_UbqQNv-A4Pi_fXm-YQ8vTCwPKtxIPgius",
                                        listOf("Probably got kick of API"),
                                        "Unknown"
                                    )
                                )

                                cpt++

                            }

                            if (cpt == list.size) {
                                multiplier++
                                println("products GET")
                                isFinished = true
                                println("end of loop")
                                updateRecycler(newList)
                            }

                        }

                    }

                }
            }
        })

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

            recyclerView.adapter = ProductAdapter(list as List<Product>,this@SearchGameFragment)
            println("adapter set")
            recyclerView.layoutManager = LinearLayoutManager(this@SearchGameFragment.context)
            println("layout set")
            recyclerView.setHasFixedSize(true)
            println("has fixed size")
        }
    }

    @WorkerThread
    fun updateRecycler(list: MutableList<Product>) {
        ContextCompat.getMainExecutor(this.requireContext()).execute {
            // This is where your UI code goes.
            println("FINISH !")
            val recyclerView = this.requireView().findViewById<RecyclerView>(R.id.recycler_view)

            println("products GET")

            isFinished = true

            val adapter = recyclerView.adapter as ProductAdapter

            println("newList : $list")

            adapter.updateProducts(list)
            adapter.notifyItemRangeInserted(adapter.itemCount, list.size)
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