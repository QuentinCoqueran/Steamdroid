package com.example.steamdroid.recycler

import com.example.steamdroid.RetrofitBuilder
import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.R
import com.example.steamdroid.model.GameReview
import kotlinx.coroutines.*

class GameReviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private val author = v.findViewById<TextView>(R.id.author)
    private val content = v.findViewById<TextView>(R.id.content)
    private val star1 = v.findViewById<ImageView>(R.id.star1)
    private val star2 = v.findViewById<ImageView>(R.id.star2)
    private val star3 = v.findViewById<ImageView>(R.id.star3)
    private val star4 = v.findViewById<ImageView>(R.id.star4)
    private val star5 = v.findViewById<ImageView>(R.id.star5)

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n")
    fun updateView(review: GameReview) {

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val userName = withContext(Dispatchers.Default){
                    RetrofitBuilder.gameReviewsNameService.getReviewerName("03B490F87625680CB895CD79AB9F59F2", "json", review.author).await()
                }

                author.text = userName
                content.text = review.content
                if (!review.vote) {
                    star1.setImageResource(R.drawable.whishlist)
                    star2.setImageResource(R.drawable.whishlist)
                    star3.setImageResource(R.drawable.whishlist)
                    star4.setImageResource(R.drawable.whishlist)
                    star5.setImageResource(R.drawable.whishlist)
                }
            } catch (_: Exception){
            }
        }
    }
}