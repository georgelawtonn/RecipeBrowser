package com.example.recipebrowserc.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recipebrowserc.data.converter.UnitConverters
import com.example.recipebrowserc.data.dao.*
import com.example.recipebrowserc.data.entity.*

@Database(
    entities = [
        GroceryList::class,
        GroceryItem::class,
        Recipe::class,
        Ingredient::class,
        KitchenItem::class,
        RecipeInstruction::class,
        MeasurementUnit::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(UnitConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groceryListDao(): GroceryListDao
    abstract fun groceryDao(): GroceryDao
    abstract fun recipeDao(): RecipeDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun kitchenDao(): KitchenDao
    abstract fun recipeInstructionDao(): RecipeInstructionDao
    abstract fun unitDao(): UnitDao
}