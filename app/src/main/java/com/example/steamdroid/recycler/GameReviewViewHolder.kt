package com.example.steamdroid.recycler

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.R
import com.example.steamdroid.model.GameReview

class GameReviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private val author = v.findViewById<TextView>(R.id.author)
    private val reviewText = v.findViewById<TextView>(R.id.review_text)

    @SuppressLint("SetTextI18n")
    fun updateView(review: GameReview) {
        author.text = review.author
        reviewText.text = review.review
    }
}