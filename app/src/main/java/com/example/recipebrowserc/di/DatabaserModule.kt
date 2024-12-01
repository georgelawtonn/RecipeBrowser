package com.example.recipebrowserc.di

import android.content.Context
import androidx.room.Room
import com.example.recipebrowserc.data.AppDatabase
import com.example.recipebrowserc.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "recipe_grocery_app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideGroceryListDao(database: AppDatabase): GroceryListDao {
        return database.groceryListDao()
    }

    @Provides
    fun provideGroceryDao(database: AppDatabase): GroceryDao {
        return database.groceryDao()
    }

    @Provides
    fun provideRecipeDao(database: AppDatabase): RecipeDao {
        return database.recipeDao()
    }

    @Provides
    fun provideIngredientDao(database: AppDatabase): IngredientDao {
        return database.ingredientDao()
    }

    @Provides
    fun provideKitchenDao(database: AppDatabase): KitchenDao {
        return database.kitchenDao()
    }

    @Provides
    fun provideRecipeInstructionDao(database: AppDatabase): RecipeInstructionDao {
        return database.recipeInstructionDao()
    }

    @Provides
    fun provideUnitDao(database: AppDatabase): UnitDao {
        return database.unitDao()
    }
}