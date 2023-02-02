package com.example.steamdroid.recycler

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.steamdroid.R
import com.example.steamdroid.model.Product

class ProductViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    val gameDetailsButton = v.findViewById<Button>(R.id.game_details_button)
    private val productImg = v.findViewById<ImageView>(R.id.product_img)
    private val productName = v.findViewById<TextView>(R.id.product_name)
    private val productBrand = v.findViewById<TextView>(R.id.product_brand)
    private val productPrice = v.findViewById<TextView>(R.id.product_price)
    private val productBackgroundImage = v.findViewById<ImageView>(R.id.product_bg_img)

    @SuppressLint("SetTextI18n")
    fun updateView(product: Product) {
        productName.text = product.gameName
        productBrand.text = product.gameEditor?.joinToString(separator = ", ")
        productPrice.text = product.gamePrice.toString()
        Glide.with(productImg.context)
            .load("https://steamcdn-a.akamaihd.net/steam/apps/${product.gameId}/library_600x900.jpg")
            .into(productImg)
        Glide.with(productBackgroundImage.context)
            .load(product.backgroundImage)
            .into(productBackgroundImage)
    }
}