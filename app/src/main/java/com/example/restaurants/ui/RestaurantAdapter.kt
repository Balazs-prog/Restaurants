/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.restaurants.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurants.R
import com.example.restaurants.model.Restaurant

/**
 * Adapter for the list of repositories.
 */
class RestaurantAdapter(val viewModel:RestaurantViewModel) : PagedListAdapter<Restaurant, androidx.recyclerview.widget.RecyclerView.ViewHolder>(RESTAURANT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val vw = LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview, parent, false)
        return RestaurantViewHolder(vw)
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val restaurantItem = getItem(position)
        if (restaurantItem != null) {
            (holder as RestaurantViewHolder).bind(restaurantItem)
        }
    }

    companion object {
        private val RESTAURANT_COMPARATOR = object : DiffUtil.ItemCallback<Restaurant>() {
            override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean =
                    oldItem == newItem
        }
    }
    inner class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val name: TextView = view.findViewById(R.id.titleRestaurant)
        private val address: TextView = view.findViewById(R.id.addressRestaurant)
        private val price: TextView = view.findViewById(R.id.priceRestaurant)
        private val image: ImageView = view.findViewById(R.id.imageRestaurant)
        private val imageFav: ImageView = view.findViewById(R.id.favouriteImage)
        private var restaurant: Restaurant? = null

        init {
            view.findViewById<ImageView>(R.id.imageRestaurant).setOnClickListener {
                restaurant?.image_url?.let { url ->
                    RestaurantViewModel.select(restaurant!!)
                    view?.let { Navigation.findNavController(it).navigate(R.id.detailsFragment) }
                }
            }
            view.findViewById<ImageView>(R.id.favouriteImage).setOnClickListener {
                Log.d("RestaurantAdapter", restaurant?.favourite.toString())
                if(restaurant?.favourite == 1) {
                    viewModel.updateFavourites(0,restaurant?.id)
                    Glide.with(itemView)
                            .load(R.drawable.heart_filled)
                            .into(imageFav)
                }else{
                    viewModel.updateFavourites(1,restaurant?.id)
                    Glide.with(itemView)
                            .load(R.drawable.heart)
                            .into(imageFav)
                }
                notifyItemChanged(adapterPosition)
            }
        }
        fun bind(restaurant: Restaurant?) {

            if (restaurant == null) {
                val resources = itemView.resources
                name.text = resources.getString(R.string.loading)
                address.visibility = View.GONE
                price.text = resources.getString(R.string.unknown)
                image.visibility = View.GONE
            } else {
                showRestaurantData(restaurant)
            }

        }

        private fun showRestaurantData(restaurant: Restaurant) {
            this.restaurant = restaurant
            name.text = restaurant.name

            // if the address is missing, hide the TextView
            var addressVisibility = View.GONE
            if (restaurant.address != null) {
                address.text = restaurant.address
                addressVisibility = View.VISIBLE
            }
            address.visibility = addressVisibility

            price.text = restaurant.price.toString()

            Glide.with(itemView)
                    .load(restaurant.image_url)
                    .into(image)
            if(restaurant.favourite == 1) {
                Glide.with(itemView)
                        .load(R.drawable.heart_filled)
                        .into(imageFav)
            }else{
                Glide.with(itemView)
                        .load(R.drawable.heart)
                        .into(imageFav)
            }

        }
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

}
