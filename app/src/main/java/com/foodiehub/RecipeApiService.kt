package com.foodiehub

import retrofit2.http.GET

interface RecipeApiService {
    @GET("Recipe_App_Data.json")
    suspend fun getCategories(): List<RecipeCategory>
}