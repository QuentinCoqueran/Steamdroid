package com.example.steamdroid.recycler

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.steamdroid.R
import com.example.steamdroid.model.Product

class GameReviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    /*private val author = v.findViewById<TextView>(R.id.author)
    private val vote = v.findViewById<TextView>(R.id.vote)*/
    private val review = v.findViewById<TextView>(R.id.review)

    @SuppressLint("SetTextI18n")
    fun updateView(product: Product) {

    }
}