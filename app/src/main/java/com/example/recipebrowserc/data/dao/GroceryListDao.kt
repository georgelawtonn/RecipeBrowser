package com.example.recipebrowserc.data.dao

import androidx.room.*
import com.example.recipebrowserc.data.entity.GroceryList
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryListDao {
    @Query("SELECT * FROM grocery_lists")
    fun getAllLists(): Flow<List<GroceryList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(groceryList: GroceryList): Long

    @Update
    suspend fun update(groceryList: GroceryList)

    @Delete
    suspend fun delete(groceryList: GroceryList)

    @Query("SELECT * FROM grocery_lists WHERE id = :listId")
    suspend fun getGroceryListById(listId: Int): GroceryList?

    @Query("UPDATE grocery_lists SET itemCount = itemCount + :change WHERE id = :listId")
    suspend fun updateItemCount(listId: Int, change: Int)
}