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

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.example.restaurants.data.RestaurantRepository
import com.example.restaurants.model.Restaurant
import com.example.restaurants.model.RestaurantSearchResult
import com.example.restaurants.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * The ViewModel works with the [RestaurantRepository] to get the data.
 */
class RestaurantViewModel(private val repository: RestaurantRepository) : ViewModel() {

    var fav = 0
    var userId:Long = 1
    var selected_:MutableLiveData<Restaurant> = RestaurantViewModel.selected
    private val queryLiveData = MutableLiveData<String>()
    private var restaurantResult: LiveData<RestaurantSearchResult> = Transformations.map(queryLiveData) {
        repository.search(it,fav)
    }
    var user: LiveData<User> = repository.searchUser(userId)
    var favouriteRestaurants: LiveData<List<Restaurant>> = repository.searchFavouriteRestaurants()

    var restaurants: LiveData<PagedList<Restaurant>> = Transformations.switchMap(restaurantResult) { it -> it.data }
    val networkErrors: LiveData<String> = Transformations.switchMap(restaurantResult) { it ->
        it.networkErrors
    }
    /**
     * Search a repository based on a query string.
     */
    fun searchRepo(queryString: String, fav:Int) {
        this.fav = fav
        queryLiveData.postValue(queryString)
    }
    fun searchUserAndFavourites(queryLong: Long){
        userId = queryLong
        user= repository.searchUser(userId)
        //favouriteRestaurants = repository.searchFavouriteRestaurants()
    }

    fun searchFavouriteRestaurants(): LiveData<List<Restaurant>> {
        return repository.searchFavouriteRestaurants()
    }

    fun updateFavourites(favourite:Int,id:Long?){
        GlobalScope.launch(Dispatchers.IO) {
            repository.updateFavourites(favourite,id)
        }
    }
    fun updateImageRestaurant(url:String,id:Long?){
        GlobalScope.launch(Dispatchers.IO) {
            repository.updateImageRestaurant(url,id)
        }
    }
    fun updateImageUser(url:String,id:Long?){
        GlobalScope.launch(Dispatchers.IO) {
            repository.updateImageUser(url,id)
        }
    }
    fun updateUser(name:String,email: String,phone:String,address: String, id:Long?){
        GlobalScope.launch(Dispatchers.IO) {
            repository.updateUser(name,email,phone,address,id)
        }
    }
    fun addUser(name:String,email:String,address:String,phone:String,picture:String){
        GlobalScope.launch(Dispatchers.IO) {
            repository.addUser(name,email,address,phone,picture)
        }
    }
    /**
     * Get the last query value.
     */
    fun lastQueryValue(): String? = queryLiveData.value

    companion object{
        val selected = MutableLiveData<Restaurant>()

        fun select(item: Restaurant) {
            selected.value = item
        }
    }



}

