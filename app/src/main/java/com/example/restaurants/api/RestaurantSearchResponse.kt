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

import com.example.restaurants.model.Restaurant
import com.google.gson.annotations.SerializedName

/**
 * Data class to hold repo responses from API calls.
 */
data class RestaurantSearchResponse(
        @SerializedName("count") val total: Int = 0,
        @SerializedName("per_page") val perPage: Int = 0,
        @SerializedName("current_page") val currentPage: Int = 0,
        @SerializedName("restaurants") val items: List<Restaurant> = emptyList(),
        val nextPage: Int? = null
)
