package com.example.recipebrowserc.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingredients",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("recipeId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MeasurementUnit::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("unitId"),
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("recipeId"),
        Index("unitId")
    ]
)
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: Int,
    val name: String,
    val quantity: Double,
    val unitId: Int
)