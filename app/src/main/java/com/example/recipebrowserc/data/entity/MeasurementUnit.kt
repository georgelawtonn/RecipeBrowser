package com.example.recipebrowserc.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.recipebrowserc.data.converter.UnitConverters

@Entity(tableName = "units")
@TypeConverters(UnitConverters::class)
data class MeasurementUnit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val abbreviation: String,
    val category: UnitCategory
)