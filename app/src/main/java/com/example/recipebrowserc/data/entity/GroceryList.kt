package com.example.recipebrowserc.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_lists")
data class GroceryList(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val itemCount: Int = 0  // New property to store the count of items
)