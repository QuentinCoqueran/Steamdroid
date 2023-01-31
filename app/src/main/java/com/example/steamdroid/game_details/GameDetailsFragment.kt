package com.example.steamdroid.game_details

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.steamdroid.R
import com.example.steamdroid.recycler.GameReviewAdpater
import java.util.Locale

class GameDetailsFragment : Fragment() {
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.game_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = Navigation.findNavController(view)

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

        val recyclerReview = view.findViewById<RecyclerView>(R.id.recycler_review)

        val currentLocale = Locale.getDefault().language
        val lang = if (currentLocale == "fr") "french" else "english"
        val currency = if (currentLocale == "fr") "fr" else "us"
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
        GameDetailsRequest().getGameReviews(730, lang, 1) { reviews ->
            if (reviews != null) {
                val adapter = GameReviewAdpater(reviews)
                recyclerReview.adapter = adapter
                recyclerReview.layoutManager = LinearLayoutManager(context)
                recyclerReview.setHasFixedSize(true)
                recyclerReview.adapter = GameReviewAdpater(reviews)
            }
        }
        GameDetailsRequest().getGame(730, lang, currency) { game ->
            if (game != null) {
                if (game.gameName != null) {
                    Glide.with(this).load(game.backGroundImg).into(backgroundImage)
                    Glide.with(this).load(game.icone).into(gameIcon)
                    Glide.with(this)
                        .load(game.backGroundImgTitle)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                            ) {
                                backGroundImgTitle.background = resource
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                TODO("Not yet implemented")
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
            }
        }
    }
}