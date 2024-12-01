package com.example.recipebrowserc.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.recipebrowserc.data.entity.GroceryItem
import com.example.recipebrowserc.data.entity.MeasurementUnit

data class GroceryItemWithUnit(
    @Embedded val groceryItem: GroceryItem,
    @Relation(
        parentColumn = "unitId",
        entityColumn = "id"
    )
    val measurementUnit: MeasurementUnit
)

