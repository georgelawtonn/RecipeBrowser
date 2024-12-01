package com.example.recipebrowserc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebrowserc.data.dao.GroceryListDao
import com.example.recipebrowserc.data.dao.GroceryDao
import com.example.recipebrowserc.data.dao.UnitDao
import com.example.recipebrowserc.data.entity.GroceryList
import com.example.recipebrowserc.data.entity.GroceryItem
import com.example.recipebrowserc.data.entity.MeasurementUnit
import com.example.recipebrowserc.data.entity.UnitCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroceryListViewModel @Inject constructor(
    private val groceryListDao: GroceryListDao,
    private val groceryDao: GroceryDao,
    private val unitDao: UnitDao
) : ViewModel() {

    val groceryLists = groceryListDao.getAllLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentGroceryList = MutableStateFlow<GroceryList?>(null)
    val currentGroceryList: StateFlow<GroceryList?> = _currentGroceryList

    val availableUnits = unitDao.getAllUnits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _unitById = MutableStateFlow<Map<Int, MeasurementUnit>>(emptyMap())

    init {
        viewModelScope.launch {
            unitDao.getAllUnits().collect { units ->
                _unitById.value = units.associateBy { it.id }
            }
        }
    }

    fun createGroceryList(name: String) {
        viewModelScope.launch {
            groceryListDao.insert(GroceryList(name = name))
        }
    }

    fun deleteGroceryList(groceryList: GroceryList) {
        viewModelScope.launch {
            groceryListDao.delete(groceryList)
        }
    }

    fun loadGroceryList(listId: Int) {
        viewModelScope.launch {
            _currentGroceryList.value = groceryListDao.getGroceryListById(listId)
        }
    }

    fun getGroceryItems(listId: Int): Flow<List<GroceryItem>> {
        return groceryDao.getItemsForList(listId)
    }

    fun addGroceryItem(groceryListId: Int, name: String, quantity: Double, unitId: Int) {
        viewModelScope.launch {
            val item = GroceryItem(
                groceryListId = groceryListId,
                name = name,
                quantity = quantity,
                unitId = unitId
            )
            groceryDao.insertAndUpdateCount(item)
        }
    }

    fun updateGroceryItem(item: GroceryItem) {
        viewModelScope.launch {
            groceryDao.update(item)
        }
    }

    fun updateGroceryItemCheckedStatus(item: GroceryItem) {
        viewModelScope.launch {
            groceryDao.update(item)
        }
    }

    fun deleteGroceryItem(item: GroceryItem) {
        viewModelScope.launch {
            groceryDao.deleteAndUpdateCount(item)
        }
    }

    fun getUnitById(id: Int): MeasurementUnit? {
        return _unitById.value[id]
    }

    fun addUnit(name: String, abbreviation: String, category: UnitCategory) {
        viewModelScope.launch {
            val unit = MeasurementUnit(
                name = name,
                abbreviation = abbreviation,
                category = category
            )
            unitDao.insert(unit)
        }
    }

    fun updateUnit(unit: MeasurementUnit) {
        viewModelScope.launch {
            unitDao.update(unit)
        }
    }

    fun deleteUnit(unit: MeasurementUnit) {
        viewModelScope.launch {
            val isUsedInGroceryItems = groceryDao.isUnitInUse(unit.id)
            if (!isUsedInGroceryItems) {
                unitDao.delete(unit)
            }
        }
    }

    fun getUnitsByCategory(category: UnitCategory): Flow<List<MeasurementUnit>> {
        return unitDao.getUnitsByCategory(category)
    }

    fun getAvailableCategories(): List<UnitCategory> {
        return UnitCategory.entries
    }
}