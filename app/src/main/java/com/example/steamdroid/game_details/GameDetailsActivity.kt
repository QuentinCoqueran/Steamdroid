package com.example.steamdroid.game_details

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.steamdroid.R
import java.util.Locale

class GameDetailsActivity : Activity() {
    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_details)

        // Buttons
        val backButton = findViewById<ImageView>(R.id.back)
        val likeButton = findViewById<ImageView>(R.id.like)
        val wishlistButton = findViewById<ImageView>(R.id.whishlist)
        val descriptionButton = findViewById<Button>(R.id.description)
        val reviewButton = findViewById<Button>(R.id.review)

        // Images
        val backgroundImage = findViewById<ImageView>(R.id.game_background)
        val gameIcon = findViewById<ImageView>(R.id.gameIcon)
        val backGroundImgTitle = findViewById<LinearLayout>(R.id.backGroundImgTitle)

        // Text
        val gameName = findViewById<TextView>(R.id.gameName)
        val editorName = findViewById<TextView>(R.id.editorName)
        val gameDescription = findViewById<TextView>(R.id.gameDescription)

        val recyclerReview = findViewById<RecyclerView>(R.id.recycler_review)

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
        GameDetailsRequest().getGame(730, lang, currency ) { game ->
            if (game != null) {
                if (game.gameName != null) {
                    Glide.with(this).load(game.backGroundImg).into(backgroundImage)
                    Glide.with(this).load(game.icone).into(gameIcon)
                    Glide.with(this)
                        .load(game.backGroundImgTitle)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
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