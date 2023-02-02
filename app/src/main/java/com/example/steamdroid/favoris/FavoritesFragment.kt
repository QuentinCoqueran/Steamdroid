package com.example.steamdroid.favoris

import RetrofitBuilder
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.R
import com.example.steamdroid.home.HomeFragment
import com.example.steamdroid.model.Product
import com.example.steamdroid.recycler.ProductAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

class FavoritesFragment : Fragment() {
    private val handler = Handler()
    private var isFinished = true
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favoris, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeFragment.needSuspend = true

        navController = Navigation.findNavController(view)
        //LOADER
        isFinished = false
        //RECYCLER VIEW
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_favoris)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        //////////////////////////////////////////
        var count = 0
        var products: List<Product> = listOf()
        val currentLocale = Locale.getDefault().language
        val lang = if (currentLocale == "fr") "french" else "english"
        val currency = if (currentLocale == "fr") "fr" else "us"
        val auth = FirebaseAuth.getInstance()
        var favoritesListId = listOf<Number>()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("favorites").whereEqualTo("email", auth.currentUser?.email)
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val documents = withContext(Dispatchers.Default) {
                    docRef.get().await()
                }
                if (!documents.isEmpty) {
                    for (document in documents) {
                        favoritesListId = favoritesListId.plus(document.get("id") as Number)
                    }
                }
            } catch (e: Exception) {
                println(e)
            }
            showWaitingDots()
            for (id in favoritesListId) {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val game = withContext(Dispatchers.Default) {
                            delay(500)
                            RetrofitBuilder.gameDetailsService.getGame(id, lang, currency)
                                .await()
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
                        if (count == favoritesListId.size) {
                            val linearMyLikes =
                                view.findViewById<LinearLayout>(R.id.linear_layout_likes)
                            linearMyLikes.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            val adapter = ProductAdapter(products, this@FavoritesFragment)
                            recyclerView.adapter = adapter
                            val closeBtn: ImageView =
                                view.findViewById(R.id.close_favoris)
                            closeBtn.setOnClickListener {
                                navController.navigateUp()
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
            isFinished = true
        }
        val closeBtn: ImageView = view.findViewById(R.id.close_favoris)
        closeBtn.setOnClickListener()
        {
            navController.navigateUp()
        }
    }

    private fun showWaitingDots() {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBarFavorites)
        if (progressBar != null) {
            progressBar.visibility = View.VISIBLE
        }
        updateDots()
    }


    private fun updateDots() {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBarFavorites)
        if (isFinished) {
            if (progressBar != null) {
                progressBar.visibility = View.GONE
            }
            return
        }
        handler.postDelayed({ updateDots() }, 1000)
    }
}