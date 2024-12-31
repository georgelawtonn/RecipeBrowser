package com.example.recipebrowserc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebrowserc.data.dao.*
import com.example.recipebrowserc.data.entity.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeWithMatchPercentage(
    val recipe: Recipe,
    val matchPercentage: Float
)

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
    private val kitchenDao: KitchenDao
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _showingSuggestions = MutableStateFlow(false)
    val showingSuggestions: StateFlow<Boolean> = _showingSuggestions

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val recipes: Flow<List<Recipe>> = combine(
        searchQuery.debounce(300),
        _showingSuggestions
    ) { query, showingSuggestions ->
        if (showingSuggestions) {
            // Get kitchen ingredients first
            val kitchenItems = kitchenDao.getAllItemsOneShot()
            val kitchenIngredientNames = kitchenItems.map { it.name.lowercase() }.toSet()

            // Get all recipes with their ingredients
            val recipesWithIngredients = recipeDao.getAllRecipesWithIngredients()

            // Calculate match percentages
            val recipesWithPercentages = recipesWithIngredients.map { recipeWithIngredients ->
                val recipe = recipeWithIngredients.recipe
                val ingredients = recipeWithIngredients.ingredients

                val matchingIngredients = ingredients.count { ingredient ->
                    kitchenIngredientNames.contains(ingredient.name.lowercase())
                }

                val percentage = if (ingredients.isNotEmpty()) {
                    matchingIngredients.toFloat() / ingredients.size
                } else {
                    0f
                }

                RecipeWithMatchPercentage(recipe, percentage)
            }

            // Sort by percentage descending
            recipesWithPercentages
                .sortedByDescending { it.matchPercentage }
                .map { it.recipe }
        } else if (query.isBlank()) {
            recipeDao.getAllRecipesOneShot()
        } else {
            recipeDao.searchRecipesOneShot("%$query%")
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _showingSuggestions.value = false
    }

    fun suggestRecipe() {
        _showingSuggestions.value = true
        _searchQuery.value = ""
    }

    fun clearSuggestions() {
        _showingSuggestions.value = false
    }
}