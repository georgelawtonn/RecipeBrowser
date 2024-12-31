package com.example.recipebrowserc.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebrowserc.data.dao.*
import com.example.recipebrowserc.data.entity.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditRecipeViewModel @Inject constructor(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
    private val instructionDao: RecipeInstructionDao,
    private val unitDao: UnitDao
) : ViewModel() {

    var recipeName by mutableStateOf("")
        private set

    var author by mutableStateOf("")
        private set

    private var editingRecipeId: Int? = null

    private val _ingredients = mutableStateListOf<Ingredient>()
    val ingredients: List<Ingredient> = _ingredients

    private val _instructions = mutableStateListOf<RecipeInstruction>()
    val instructions: List<RecipeInstruction> = _instructions

    val availableUnits = unitDao.getAllUnits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _unitById = MutableStateFlow<Map<Int, MeasurementUnit>>(emptyMap())

    init {
        viewModelScope.launch {
            unitDao.getAllUnits().collect { units ->
                _unitById.value = units.associateBy { it.id }
            }
        }
    }

    val canSaveRecipe: Boolean
        get() = recipeName.isNotBlank() && ingredients.isNotEmpty() && instructions.isNotEmpty()

    fun updateRecipeName(name: String) {
        recipeName = name
    }

    fun updateAuthor(newAuthor: String) {
        author = newAuthor
    }

    fun getUnitById(id: Int): MeasurementUnit? {
        return _unitById.value[id]
    }

    fun addIngredient(name: String, quantity: Double, unitId: Int) {
        _ingredients.add(
            Ingredient(
                recipeId = editingRecipeId ?: 0,
                name = name.trim(),
                quantity = quantity,
                unitId = unitId
            )
        )
    }

    fun updateIngredient(updatedIngredient: Ingredient) {
        val index = _ingredients.indexOfFirst { it.id == updatedIngredient.id }
        if (index != -1) {
            _ingredients[index] = updatedIngredient
        }
    }

    fun removeIngredient(ingredient: Ingredient) {
        _ingredients.remove(ingredient)
    }

    fun addInstruction(instruction: String) {
        _instructions.add(
            RecipeInstruction(
                recipeId = editingRecipeId ?: 0,
                stepNumber = _instructions.size + 1,
                instruction = instruction.trim()
            )
        )
    }

    fun updateInstruction(updatedInstruction: RecipeInstruction) {
        val index = _instructions.indexOfFirst { it.id == updatedInstruction.id }
        if (index != -1) {
            _instructions[index] = updatedInstruction
        }
    }

    fun removeInstruction(instruction: RecipeInstruction) {
        val index = _instructions.indexOf(instruction)
        if (index != -1) {
            _instructions.removeAt(index)
            // Renumber remaining instructions
            for (i in index until _instructions.size) {
                _instructions[i] = _instructions[i].copy(stepNumber = i + 1)
            }
        }
    }

    fun clearRecipe() {
        editingRecipeId = null
        recipeName = ""
        author = ""
        _ingredients.clear()
        _instructions.clear()
    }

    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            editingRecipeId = recipeId

            recipeDao.getRecipeWithInstructions(recipeId)?.let { recipeWithInstructions ->
                recipeName = recipeWithInstructions.recipe.name
                author = recipeWithInstructions.recipe.author

                // Load instructions
                _instructions.clear()
                _instructions.addAll(recipeWithInstructions.instructions)
            }

            // Load ingredients separately
            ingredientDao.getIngredientsForRecipe(recipeId).collect { loadedIngredients ->
                _ingredients.clear()
                _ingredients.addAll(loadedIngredients)
            }
        }
    }

    fun saveRecipe() {
        viewModelScope.launch {
            val recipe = Recipe(
                id = editingRecipeId ?: 0,
                name = recipeName.trim(),
                author = author.trim()
            )

            val recipeId = if (editingRecipeId == null) {
                recipeDao.insert(recipe).toInt()
            } else {
                recipeDao.update(recipe)
                editingRecipeId!!
            }

            // Save ingredients
            ingredientDao.deleteIngredientsForRecipe(recipeId)
            val ingredientsWithRecipeId = ingredients.map { ingredient ->
                ingredient.copy(recipeId = recipeId)
            }
            ingredientDao.insertAll(ingredientsWithRecipeId)

            // Save instructions
            instructionDao.deleteAllInstructionsForRecipe(recipeId)
            val instructionsWithRecipeId = instructions.mapIndexed { index, instruction ->
                instruction.copy(
                    recipeId = recipeId,
                    stepNumber = index + 1
                )
            }
            instructionDao.insertAll(instructionsWithRecipeId)

            clearRecipe()
        }
    }
}