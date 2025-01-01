package com.example.recipebrowserc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebrowserc.data.dao.*
import com.example.recipebrowserc.data.entity.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
    private val instructionDao: RecipeInstructionDao,
    private val groceryListDao: GroceryListDao,
    private val groceryDao: GroceryDao,
    private val unitDao: UnitDao
) : ViewModel() {

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe

    private val _recipeId = MutableStateFlow(0)

    val ingredients = _recipeId.flatMapLatest { id ->
        ingredientDao.getIngredientsForRecipe(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val instructions = _recipeId.flatMapLatest { id ->
        instructionDao.getInstructionsForRecipe(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val groceryLists = groceryListDao.getAllLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _unitById = MutableStateFlow<Map<Int, MeasurementUnit>>(emptyMap())

    init {
        viewModelScope.launch {
            unitDao.getAllUnits().collect { units ->
                _unitById.value = units.associateBy { it.id }
            }
        }
    }

    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            _recipeId.value = recipeId
            _recipe.value = recipeDao.getRecipeById(recipeId)
        }
    }

    fun getUnitById(id: Int): MeasurementUnit? {
        return _unitById.value[id]
    }

    suspend fun createNewListWithIngredients(listName: String): Int {
        val listId = groceryListDao.insert(GroceryList(name = listName)).toInt()
        addIngredientsToGroceryList(listId)
        return listId
    }

    fun deleteRecipe(recipeId: Int) {
        viewModelScope.launch {
            recipe.value?.let { recipeDao.delete(it) }
        }
    }

    fun addIngredientsToGroceryList(groceryListId: Int) {
        viewModelScope.launch {
            ingredients.value.forEach { ingredient ->
                val groceryItem = GroceryItem(
                    groceryListId = groceryListId,
                    name = ingredient.name,
                    quantity = ingredient.quantity,
                    unitId = ingredient.unitId
                )
                groceryDao.insertAndUpdateCount(groceryItem)
            }
        }
    }
}