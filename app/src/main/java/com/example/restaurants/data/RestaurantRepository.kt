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

package com.example.restaurants.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import com.example.restaurants.api.RestaurantService
import com.example.restaurants.db.RestaurantLocalCache
import com.example.restaurants.model.Restaurant
import com.example.restaurants.model.RestaurantSearchResult
import com.example.restaurants.model.User

/**
 * Repository class that works with local and remote data sources.
 */
class RestaurantRepository(
    private val service: RestaurantService,
    private val cache: RestaurantLocalCache
) {

    /**
     * Search restaurants whose names match the query.
     */
    fun search(query: String, fav:Int): RestaurantSearchResult {
        Log.d("RestaurantRepository", "New query: $query")

        // Get data source factory from the local cache

        val dataSourceFactory = cache.restaurantsByCity(query,fav)
        val repoBoundaryCallback = RestaurantBoundaryCallback(query, service, cache, fav)
        val networkErrors = repoBoundaryCallback.networkErrors

        // Get the paged list
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(repoBoundaryCallback)
                .build()

        return RestaurantSearchResult(data, networkErrors)
    }
    fun updateFavourites(favourite:Int,id:Long?){
        cache.updateFavourites(favourite,id)
    }
    fun addUser(name:String,email:String,address:String,phone:String,picture:String){
        cache.addUser(name,email,address,phone,picture)
    }
    fun searchUser(id:Long): LiveData<User> {
        return cache.searchUser(id)
    }
    fun updateImageRestaurant(url:String,id:Long?){
        cache.updateImageRestaurant(url,id)
    }
    fun updateImageUser(url:String,id:Long?){
        cache.updateImageUser(url,id)
    }
    fun updateUser(name:String,email: String,phone:String,address: String, id:Long?){
        cache.updateUser(name,email,phone,address,id)
    }
    fun searchFavouriteRestaurants():LiveData<List<Restaurant>>{
        return cache.searchFavouriteRestaurants()
    }
    companion object {
        private const val DATABASE_PAGE_SIZE = 25
    }
}
