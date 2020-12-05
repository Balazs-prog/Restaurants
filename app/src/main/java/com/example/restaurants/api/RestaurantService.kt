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

package com.example.restaurants.api

import android.util.Log
import com.example.restaurants.model.Restaurant
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val TAG = "BE API"

/**
 * Search repos based on a query.
 * Trigger a request to the API with the following params:
 * @param query searchRestaurant keyword
 * @param page request page index
 * @param itemsPerPage number of repositories to be returned by the API per page
 *
 * The result of the request is handled by the implementation of the functions passed as params
 * @param onSuccess function that defines how to handle the list of restaurants received
 * @param onError function that defines how to handle request failure
 */
fun searchRepos(
    service: RestaurantService,
    query: String,
    page: Int,
    itemsPerPage: Int,
    onSuccess: (restaurants: List<Restaurant>) -> Unit,
    onError: (error: String) -> Unit
) {
    Log.d(TAG, "query: $query, page: $page, itemsPerPage: $itemsPerPage")

    val apiQuery = query //+ IN_QUALIFIER

    service.searchRepos(apiQuery, page, itemsPerPage).enqueue(
            object : Callback<RestaurantSearchResponse> {
                override fun onFailure(call: Call<RestaurantSearchResponse>?, t: Throwable) {
                    Log.d(TAG, "fail to get data")
                    onError(t.message ?: "unknown error")
                }

                override fun onResponse(
                    call: Call<RestaurantSearchResponse>?,
                    response: Response<RestaurantSearchResponse>
                ) {
                    Log.d(TAG, "got a response $response")
                    if (response.isSuccessful) {
                        val repos = response.body()?.items ?: emptyList()
                        onSuccess(repos)
                    } else {
                        onError(response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            }
    )
}

/**
 * API communication setup via Retrofit.
 */
interface RestaurantService {
    /**
     * Get restaurants by city.
     */
    @GET("restaurants")
    fun searchRepos(
        @Query("city") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): Call<RestaurantSearchResponse>

    companion object {
        private const val BASE_URL = "https://opentable.herokuapp.com/api/"

        fun create(): RestaurantService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RestaurantService::class.java)
        }
    }
}