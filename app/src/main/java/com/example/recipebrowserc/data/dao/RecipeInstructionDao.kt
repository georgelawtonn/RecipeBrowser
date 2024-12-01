package com.example.recipebrowserc.data.dao

import androidx.room.*
import com.example.recipebrowserc.data.entity.RecipeInstruction
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeInstructionDao {
    @Query("SELECT * FROM recipe_instructions WHERE recipeId = :recipeId ORDER BY stepNumber")
    fun getInstructionsForRecipe(recipeId: Int): Flow<List<RecipeInstruction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(instruction: RecipeInstruction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(instructions: List<RecipeInstruction>): List<Long>

    @Update
    suspend fun update(instruction: RecipeInstruction)

    @Delete
    suspend fun delete(instruction: RecipeInstruction)

    @Query("DELETE FROM recipe_instructions WHERE recipeId = :recipeId")
    suspend fun deleteAllInstructionsForRecipe(recipeId: Int): Int
}