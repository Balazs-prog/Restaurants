package com.example.restaurants.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.restaurants.api.RestaurantService
import com.example.restaurants.api.searchRepos
import com.example.restaurants.db.RestaurantLocalCache
import com.example.restaurants.model.Restaurant

class RestaurantBoundaryCallback(
        private val query: String,
        private val service: RestaurantService,
        private val cache: RestaurantLocalCache,
        private val fav: Int
) : PagedList.BoundaryCallback<Restaurant>() {

    private var lastRequestedPage = 1

    private val _networkErrors = MutableLiveData<String>()

    val networkErrors: LiveData<String>
        get() = _networkErrors

    private var isRequestInProgress = false

    companion object {
        private const val NETWORK_PAGE_SIZE = 25
    }

    private fun requestAndSaveData(query: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        searchRepos(service, query, lastRequestedPage, NETWORK_PAGE_SIZE, { Restaurants ->
            if(fav == 0){
                cache.insert(Restaurants) {
                lastRequestedPage++
                isRequestInProgress = false
                }
            }

        }, { error ->
            _networkErrors.postValue(error)
            isRequestInProgress = false
        })
    }


    override fun onZeroItemsLoaded() {
        requestAndSaveData(query)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Restaurant) {
        requestAndSaveData(query)
    }
}