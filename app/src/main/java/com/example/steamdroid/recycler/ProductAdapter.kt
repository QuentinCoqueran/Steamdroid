package com.example.steamdroid.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.R
import com.example.steamdroid.model.Product

class ProductAdapter(private var products : List<Product>) : RecyclerView.Adapter<ProductViewHolder>() {
        override fun getItemCount(): Int = products.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            return ProductViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            holder.updateView(products[position])
        }

        fun updateProducts(newProducts: List<Product>) {
            products = products.plus(newProducts)
        }



}

