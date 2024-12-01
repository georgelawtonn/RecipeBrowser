package com.example.recipebrowserc.data.dao

import androidx.room.*
import com.example.recipebrowserc.data.entity.GroceryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {
    @Query("SELECT * FROM grocery_items WHERE groceryListId = :listId")
    fun getItemsForList(listId: Int): Flow<List<GroceryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GroceryItem): Long

    @Update
    suspend fun update(item: GroceryItem)

    @Delete
    suspend fun delete(item: GroceryItem)

    @Query("SELECT * FROM grocery_items WHERE groceryListId = :listId AND name LIKE '%' || :searchQuery || '%'")
    fun searchItemsInList(listId: Int, searchQuery: String): Flow<List<GroceryItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM grocery_items WHERE unitId = :unitId LIMIT 1)")
    suspend fun isUnitInUse(unitId: Int): Boolean

    @Transaction
    suspend fun insertAndUpdateCount(item: GroceryItem) {
        insert(item)
        updateGroceryListItemCount(item.groceryListId, 1)
    }

    @Transaction
    suspend fun deleteAndUpdateCount(item: GroceryItem) {
        delete(item)
        updateGroceryListItemCount(item.groceryListId, -1)
    }

    @Query("UPDATE grocery_lists SET itemCount = itemCount + :change WHERE id = :listId")
    suspend fun updateGroceryListItemCount(listId: Int, change: Int)
}