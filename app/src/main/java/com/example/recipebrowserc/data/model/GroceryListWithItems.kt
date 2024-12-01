package com.example.recipebrowserc.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.recipebrowserc.data.entity.GroceryList
import com.example.recipebrowserc.data.entity.GroceryItem

data class GroceryListWithItems(
    @Embedded val groceryList: GroceryList,
    @Relation(
        parentColumn = "id",
        entityColumn = "groceryListId"
    )
    val items: List<GroceryItem>
)