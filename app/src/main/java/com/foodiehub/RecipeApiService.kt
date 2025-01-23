package com.foodiehub

import retrofit2.http.GET

interface RecipeApiService {
    @GET("refs/heads/main/Recipe_App_Data.json")
    suspend fun getCategories(): List<RecipeCategory>
}