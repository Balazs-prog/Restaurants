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

package com.example.restaurants.db

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.example.restaurants.model.Restaurant
import com.example.restaurants.model.User
import java.util.concurrent.Executor

/**
 * Class that handles the DAO local data source. This ensures that methods are triggered on the
 * correct executor.
 */
class RestaurantLocalCache(
    private val restaurantDao: RestaurantDao,
    private val ioExecutor: Executor

) {
    lateinit var user:LiveData<User>
    /**
     * Insert a list of restaurants in the database, on a background thread.
     */
    fun insert(restaurants: List<Restaurant>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            Log.d("RestaurantLocalCache", "inserting ${restaurants.size} Restaurants")
            restaurantDao.insert(restaurants)
            insertFinished()
        }
    }

    /**
     * Request a LiveData<List<Restaurant>> from the Dao, based on a Restaurant name. If the name contains
     * multiple words separated by spaces, then we're emulating the API behavior and allow
     * any characters between the words.
     * @param name Restaurants name
     */
    fun restaurantsByCity(name: String, favourite: Int): DataSource.Factory<Int, Restaurant> {
        // appending '%' so we can allow other characters to be before and after the query string
        val query = "%${name.replace(' ', '%')}%"
        Log.d("GithubLocalCache", "restaurantsbyname: ${query} Restaurants")
        return restaurantDao.restaurantsByCity(query,favourite)
    }
    fun restaurantById(id: Int): DataSource.Factory<Int, Restaurant> {
        return restaurantDao.restaurantById(id)
    }

    fun searchUser(id:Long): LiveData<User> {
        return restaurantDao.userById()
    }
    fun updateImageRestaurant(url:String,id:Long?){
        restaurantDao.updateImageRestaurant(url,id)
    }
    fun updateImageUser(url:String,id:Long?){
        restaurantDao.updateImageUser(url,id)
    }
    fun updateUser(name:String,email: String,phone:String,address: String, id:Long?){
        restaurantDao.updateUser(name,email,phone,address,id)
    }
    fun addUser(name:String,email:String,address:String,phone:String,picture:String){
        restaurantDao.addUser(name,email,address,phone,picture)
    }
    fun updateFavourites(favourite:Int,id:Long?){
        restaurantDao.updateFavourites(favourite,id)
    }
    fun searchFavouriteRestaurants():LiveData<List<Restaurant>>{
        val x = restaurantDao.searchFavouriteRestaurants()
        Log.d("LocalCache",x.value?.get(0)?.address.toString())
        return x
    }
}
