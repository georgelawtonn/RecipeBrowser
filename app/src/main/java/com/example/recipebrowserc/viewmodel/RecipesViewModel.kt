package com.example.recipebrowserc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebrowserc.data.dao.RecipeDao
import com.example.recipebrowserc.data.entity.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val recipeDao: RecipeDao
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val recipes: Flow<List<Recipe>> = searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                recipeDao.getAllRecipes()
            } else {
                recipeDao.searchRecipes("%$query%")
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun createRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeDao.insert(recipe)
        }
    }

    fun suggestRecipe() {
        // TODO: Implement recipe suggestion logic
        // This could involve selecting a random recipe from the database
        // or implementing more sophisticated recommendation algorithms
    }
}