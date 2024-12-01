package com.example.recipebrowserc.data.dao

import androidx.room.*
import com.example.recipebrowserc.data.entity.MeasurementUnit
import com.example.recipebrowserc.data.entity.UnitCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao {
    @Query("SELECT * FROM units ORDER BY category, name ASC")
    fun getAllUnits(): Flow<List<MeasurementUnit>>

    @Query("SELECT * FROM units WHERE category = :category ORDER BY name ASC")
    fun getUnitsByCategory(category: UnitCategory): Flow<List<MeasurementUnit>>

    @Query("SELECT * FROM units WHERE id = :id")
    suspend fun getUnitById(id: Int): MeasurementUnit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(unit: MeasurementUnit): Long

    @Update
    suspend fun update(unit: MeasurementUnit)

    @Delete
    suspend fun delete(unit: MeasurementUnit)
}