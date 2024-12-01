package com.example.recipebrowserc.data.dao

import androidx.room.*
import com.example.recipebrowserc.data.entity.KitchenItem
import kotlinx.coroutines.flow.Flow

@Dao
interface KitchenDao {
    @Query("SELECT * FROM kitchen_items")
    fun getAllItems(): Flow<List<KitchenItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: KitchenItem): Long

    @Update
    suspend fun update(item: KitchenItem)

    @Delete
    suspend fun delete(item: KitchenItem)
}