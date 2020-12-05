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

import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.restaurants.model.Restaurant
import com.example.restaurants.model.User

/**
 * Room data access object for accessing the [Repo] table.
 */
@Dao
interface RestaurantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<Restaurant>)

    // Do a similar query as the search API:
    // Look for repos that contain the query string in the name or in the description
    // and order those results descending by city
    @Query("SELECT * FROM restaurant WHERE (city LIKE :queryString AND favourite=:favourite) ORDER BY city DESC")
    fun restaurantsByCity(queryString: String, favourite:Int): DataSource.Factory<Int, Restaurant>

    @Query("SELECT * FROM restaurant WHERE id = :queryInt")
    fun restaurantById(queryInt: Int): DataSource.Factory<Int, Restaurant>

    @Query("SELECT * FROM restaurant")
    fun searchFavouriteRestaurants(): LiveData<List<Restaurant>>

    @Query("SELECT id,name,email,phone,address,picture FROM user WHERE id = (SELECT MAX(id) from user)")
    fun userById(): LiveData<User>

    @Query("INSERT INTO user(name,email,address,phone,picture) VALUES (:name,:email,:address,:phone,:picture)")
    fun addUser(name:String,email:String,address:String,phone:String,picture:String)

    @Query("UPDATE restaurant SET favourite = :favourite where id = :id")
    fun updateFavourites(favourite: Int, id:Long?)

    @Query("UPDATE restaurant SET image_url = :url where id = :id")
    fun updateImageRestaurant(url: String, id:Long?)

    @Query("UPDATE user SET picture = :url where id = :id")
    fun updateImageUser(url: String, id:Long?)

    @Query("UPDATE user SET name=:name, email=:email,phone=:phone,address=:address where id = :id")
    fun updateUser(name:String,email: String,phone:String,address: String, id:Long?)


}
