package com.example.steamdroid.game_details

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.steamdroid.R
import com.example.steamdroid.home.HomeFragment
import com.example.steamdroid.recycler.GameReviewAdpater
import com.example.steamdroid.RetrofitBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.Locale

class GameDetailsFragment : Fragment() {
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_details, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeFragment.needSuspend = true
        navController = Navigation.findNavController(view)
        val gameId = arguments?.getString("gameId")?.toInt()

        // Buttons
        val backButton = view.findViewById<ImageView>(R.id.back)
        val likeButton = view.findViewById<ImageView>(R.id.like)
        val wishlistButton = view.findViewById<ImageView>(R.id.whishlist)
        val descriptionButton = view.findViewById<Button>(R.id.description)
        val reviewButton = view.findViewById<Button>(R.id.review)

        // Images
        val backgroundImage = view.findViewById<ImageView>(R.id.game_background)
        val gameIcon = view.findViewById<ImageView>(R.id.gameIcon)
        val backGroundImgTitle = view.findViewById<LinearLayout>(R.id.backGroundImgTitle)

        // Text
        val gameName = view.findViewById<TextView>(R.id.gameName)
        val editorName = view.findViewById<TextView>(R.id.editorName)
        val gameDescription = view.findViewById<TextView>(R.id.gameDescription)

        // Language
        val lang = getString(R.string.lang)
        val currency = getString(R.string.currency)

        val recyclerReview = view.findViewById<RecyclerView>(R.id.recycler_review)

        val db = FirebaseFirestore.getInstance()
        val collectionFav = db.collection("favorites")
        val collectionWish = db.collection("wishlist")

        collectionFav
            .whereEqualTo("id", gameId)
            .whereEqualTo("email", FirebaseAuth.getInstance().currentUser?.email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    likeButton.setImageResource(R.drawable.like)
                } else {
                    likeButton.setImageResource(R.drawable.like_full)
                }
            }

        collectionWish
            .whereEqualTo("id", gameId)
            .whereEqualTo("email", FirebaseAuth.getInstance().currentUser?.email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    wishlistButton.setImageResource(R.drawable.whishlist)
                } else {
                    wishlistButton.setImageResource(R.drawable.whishlist_full)
                }
            }

        descriptionButton.setOnClickListener {
            if (gameDescription.visibility == View.VISIBLE) {
                descriptionButton.setBackgroundResource(R.drawable.btn_border_rounded_transparent_game_description)
                gameDescription.visibility = View.GONE
                reviewButton.setBackgroundResource(R.drawable.button_border_rounded_game_review)
                recyclerReview.visibility = View.VISIBLE

            } else {
                descriptionButton.setBackgroundResource(R.drawable.button_border_rounded_game_description)
                gameDescription.visibility = View.VISIBLE
                reviewButton.setBackgroundResource(R.drawable.btn_border_rounded_transparent_game_review)
                recyclerReview.visibility = View.GONE
            }
        }

        reviewButton.setOnClickListener {
            if (recyclerReview.visibility == View.VISIBLE) {
                descriptionButton.setBackgroundResource(R.drawable.button_border_rounded_game_description)
                gameDescription.visibility = View.VISIBLE
                reviewButton.setBackgroundResource(R.drawable.btn_border_rounded_transparent_game_review)
                recyclerReview.visibility = View.GONE
            } else {
                reviewButton.setBackgroundResource(R.drawable.button_border_rounded_game_review)
                recyclerReview.visibility = View.VISIBLE
                descriptionButton.setBackgroundResource(R.drawable.btn_border_rounded_transparent_game_description)
                gameDescription.visibility = View.GONE
            }
        }

        backButton.setOnClickListener {
            navController.navigateUp()
        }

        if (gameId != null) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val reviews = withContext(Dispatchers.Default) {
                        RetrofitBuilder.gameReviewsService.getGameReviews(gameId, lang, 1).await()
                    }

                    val adapter = GameReviewAdpater(reviews)
                    recyclerReview.adapter = adapter
                    recyclerReview.layoutManager = LinearLayoutManager(context)
                    recyclerReview.setHasFixedSize(true)
                    recyclerReview.adapter = GameReviewAdpater(reviews)
                } catch (e: Exception){
                    println(e)
                }
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val game = withContext(Dispatchers.Default) {
                    RetrofitBuilder.gameDetailsService.getGame(gameId!!, lang, currency).await()
                }
                if (game.gameName != null) {
                    Glide.with(this@GameDetailsFragment).load(game.backGroundImg)
                        .into(backgroundImage)
                    Glide.with(this@GameDetailsFragment).load("https://steamcdn-a.akamaihd.net/steam/apps/$gameId/library_600x900.jpg").into(gameIcon)
                    Glide.with(this@GameDetailsFragment)
                        .load(game.backGroundImgTitle)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                            ) {
                                backGroundImgTitle.background = resource
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                        })
                    game.editorName = game.editorName?.sortedBy { it }
                    editorName.text = game.editorName?.joinToString(separator = "\n")
                    editorName.setTextColor(resources.getColor(R.color.white))
                    gameName.text = game.gameName
                    gameName.setTextColor(resources.getColor(R.color.white))
                    gameDescription.text = game.gameDescription
                    gameDescription.setTextColor(resources.getColor(R.color.white))
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        }

        gameDescription.movementMethod = ScrollingMovementMethod()

        val auth = FirebaseAuth.getInstance()

        likeButton.setOnClickListener {
            val collection = db.collection("favorites")
            collection
                .whereEqualTo("id", gameId)
                .whereEqualTo("email", FirebaseAuth.getInstance().currentUser?.email)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        val docRef = collection.document()
                        docRef.set(
                            mapOf(
                                "id" to gameId,
                                "email" to auth.currentUser?.email,
                            )
                        )
                            .addOnSuccessListener {
                                likeButton.setImageResource(R.drawable.like_full)
                                Toast.makeText(
                                    context,
                                    getString(R.string.like_button_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        documents.forEach {
                            collection.document(it.id).delete()
                        }
                        likeButton.setImageResource(R.drawable.like)
                        Toast.makeText(
                            context,
                            getString(R.string.like_button_delete),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        wishlistButton.setOnClickListener {
            val collection = db.collection("wishlist")
            collection
                .whereEqualTo("id", gameId)
                .whereEqualTo("email", FirebaseAuth.getInstance().currentUser?.email)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        val docRef = collection.document()
                        docRef.set(
                            mapOf(
                                "id" to gameId,
                                "email" to auth.currentUser?.email,
                            )
                        )
                            .addOnSuccessListener {
                                wishlistButton.setImageResource(R.drawable.whishlist_full)
                                Toast.makeText(
                                    context,
                                    getString(R.string.wishlist_button_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        documents.forEach {
                            collection.document(it.id).delete()
                        }
                        wishlistButton.setImageResource(R.drawable.whishlist)
                        Toast.makeText(
                            context,
                            getString(R.string.wishlist_button_delete),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}