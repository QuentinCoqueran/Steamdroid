package com.example.steamdroid.recycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.steamdroid.R
import com.example.steamdroid.favoris.FavoritesFragment
import com.example.steamdroid.home.HomeFragment
import com.example.steamdroid.model.Product
import com.example.steamdroid.search.SearchGameFragment
import com.example.steamdroid.wishlist.WishListFragment

class ProductAdapter(private var products: List<Product>, private val parentFragment: Fragment) :
    RecyclerView.Adapter<ProductViewHolder>() {
    override fun getItemCount(): Int = products.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.updateView(products[position])
        holder.gameDetailsButton.setOnClickListener {
            val args = Bundle()
            args.putString("gameId", products[position].gameId.toString())
            when (parentFragment) {
                is FavoritesFragment -> {
                    parentFragment.findNavController().navigate(R.id.action_favoritesFragment_to_gameDetailsFragment, args)
                }
                is WishListFragment -> {
                    parentFragment.findNavController().navigate(R.id.action_wishListFragment_to_gameDetailsFragment, args)
                }
                is SearchGameFragment -> {
                    parentFragment.findNavController().navigate(R.id.action_searchGameFragment_to_gameDetailsFragment, args)
                }
                is HomeFragment -> {
                    parentFragment.findNavController().navigate(R.id.action_homeFragment_to_gameDetailsFragment, args)
                }
            }
        }
    }

    fun updateProducts(newProducts: List<Product>) {
        products = products.plus(newProducts)
    }
}