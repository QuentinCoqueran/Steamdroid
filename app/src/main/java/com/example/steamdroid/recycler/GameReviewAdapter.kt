package com.example.steamdroid.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.R
import com.example.steamdroid.model.GameReview
class GameReviewAdpater(private var reviews : List<GameReview>) : RecyclerView.Adapter<GameReviewViewHolder>() {
    override fun getItemCount(): Int = reviews.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameReviewViewHolder {
        return GameReviewViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.game_review, parent,false)
        )
    }

    override fun onBindViewHolder(holder: GameReviewViewHolder, position: Int) {
        holder.updateView(reviews[position])
    }
}

