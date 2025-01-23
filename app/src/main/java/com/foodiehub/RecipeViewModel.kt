package com.foodiehub

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch

class RecipeViewModel(private val context: Context) : ViewModel() {
    private val _categories = mutableStateOf<List<RecipeCategory>>(emptyList())
    val categories: State<List<RecipeCategory>> = _categories

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _selectedCategory = mutableStateOf<RecipeCategory?>(null)
    val selectedCategory: State<RecipeCategory?> = _selectedCategory

    private val _filteredRecipes = mutableStateOf<List<Recipe>>(emptyList())
    val filteredRecipes: State<List<Recipe>> = _filteredRecipes

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_cache", Context.MODE_PRIVATE)

    private val _favorites = mutableStateListOf<Recipe>()
    val favorites: List<Recipe> get() = _favorites

    fun toggleFavorite(recipe: Recipe) {
        if (_favorites.contains(recipe)) {
            _favorites.remove(recipe)
        } else {
            _favorites.add(recipe)
        }
    }

    init {
        // Check if cached data exists
        val cachedData = sharedPreferences.getString("categories_data", null)
        if (cachedData != null) {
            val categoriesList = Gson().fromJson(cachedData, Array<RecipeCategory>::class.java).toList()
            _categories.value = categoriesList
            _filteredRecipes.value = categoriesList.flatMap { it.recipes }
        } else {
            fetchRecipes()
        }
    }


    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterRecipes()
    }

    fun setSelectedCategory(category: RecipeCategory?) {
        _selectedCategory.value = category
        filterRecipes()
    }

    private fun filterRecipes() {
        val query = searchQuery.value
        val category = selectedCategory.value

        val filtered = _categories.value
            .flatMap { it.recipes }
            .filter { recipe ->
                (category == null || recipe in category.recipes) &&
                        recipe.name.contains(query, ignoreCase = true)
            }

        _filteredRecipes.value = filtered
    }

    private fun fetchRecipes() {
        viewModelScope.launch {
            try {
                val response = ApiClient.retrofit.getCategories()
                _categories.value = response
                _filteredRecipes.value = response.flatMap { it.recipes }

                // Cache the fetched data
                val categoriesJson = Gson().toJson(response)
                sharedPreferences.edit().putString("categories_data", categoriesJson).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun getRecipeById(recipeId: Int): Recipe? {
        return filteredRecipes.value.find { it.id == recipeId }
    }

}