package com.example.recipebrowserc.data.entity

enum class UnitCategory(val displayName: String) {
    WEIGHT("Weight"),
    VOLUME("Volume"),
    COUNT("Count"),
    LENGTH("Length"),
    TEMPERATURE("Temperature"),
    TIME("Time");

    companion object {
        fun fromString(value: String?): UnitCategory? =
            entries.find { it.name == value || it.displayName == value }
    }
}