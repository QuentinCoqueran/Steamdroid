package com.example.steamdroid.recycler

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.steamdroid.R
import com.example.steamdroid.game_details.GameDetailsRequest
import com.example.steamdroid.model.GameReview

class GameReviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private val author = v.findViewById<TextView>(R.id.author)
    private val content = v.findViewById<TextView>(R.id.content)
    private val star1 = v.findViewById<ImageView>(R.id.star1)
    private val star2 = v.findViewById<ImageView>(R.id.star2)
    private val star3 = v.findViewById<ImageView>(R.id.star3)
    private val star4 = v.findViewById<ImageView>(R.id.star4)
    private val star5 = v.findViewById<ImageView>(R.id.star5)

    @SuppressLint("SetTextI18n")
    fun updateView(review: GameReview) {
        GameDetailsRequest().getReviewerName(review.author, "json") { userName ->
            if (userName != null)
                author.text = userName
            else
                println("USER NAME IS NULL")
        }
        if (!review.vote) {
            star1.setImageResource(R.drawable.whishlist)
            star2.setImageResource(R.drawable.whishlist)
            star3.setImageResource(R.drawable.whishlist)
            star4.setImageResource(R.drawable.whishlist)
            star5.setImageResource(R.drawable.whishlist)
        }
        content.text = review.content

    }
}