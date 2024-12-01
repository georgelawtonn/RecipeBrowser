package com.example.recipebrowserc.data.entity

import androidx.room.*

@Entity(
    tableName = "recipe_instructions",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipeId")]
)
data class RecipeInstruction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: Int,
    val stepNumber: Int,
    val instruction: String
)