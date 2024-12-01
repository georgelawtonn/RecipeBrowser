package com.example.recipebrowserc.data.dao

import androidx.room.*
import com.example.recipebrowserc.data.entity.Recipe
import com.example.recipebrowserc.data.model.RecipeWithInstructions
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchRecipes(query: String): Flow<List<Recipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe): Long

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeWithInstructions(recipeId: Int): RecipeWithInstructions?

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: Int): Recipe?
}