package com.example.recipebrowserc.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "grocery_items",
    foreignKeys = [
        ForeignKey(
            entity = GroceryList::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("groceryListId"),
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
        Index("groceryListId"),
        Index("unitId")
    ]
)
data class GroceryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val groceryListId: Int,
    val name: String,
    val quantity: Double,
    val unitId: Int,
    val isChecked: Boolean = false
)