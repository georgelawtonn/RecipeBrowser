package com.example.recipebrowserc.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "kitchen_items",
    foreignKeys = [
        ForeignKey(
            entity = MeasurementUnit::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("unitId"),
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("unitId")]
)
data class KitchenItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Double,
    val unitId: Int
)