package com.example.recipebrowserc.data.converter

import androidx.room.TypeConverter
import com.example.recipebrowserc.data.entity.UnitCategory

class UnitConverters {
    @TypeConverter
    fun toUnitCategory(value: String): UnitCategory {
        return UnitCategory.valueOf(value)
    }

    @TypeConverter
    fun fromUnitCategory(category: UnitCategory): String {
        return category.name
    }
}