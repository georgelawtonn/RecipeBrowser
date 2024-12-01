package com.example.recipebrowserc.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.recipebrowserc.data.entity.Recipe
import com.example.recipebrowserc.data.entity.RecipeInstruction

data class RecipeWithInstructions(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val instructions: List<RecipeInstruction>
)