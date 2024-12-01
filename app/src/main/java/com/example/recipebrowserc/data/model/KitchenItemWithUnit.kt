package com.example.recipebrowserc.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.recipebrowserc.data.entity.KitchenItem
import com.example.recipebrowserc.data.entity.MeasurementUnit

data class KitchenItemWithUnit(
    @Embedded val kitchenItem: KitchenItem,
    @Relation(
        parentColumn = "unitId",
        entityColumn = "id"
    )
    val measurementUnit: MeasurementUnit
)