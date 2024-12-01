package com.example.recipebrowserc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebrowserc.data.dao.KitchenDao
import com.example.recipebrowserc.data.dao.UnitDao
import com.example.recipebrowserc.data.entity.KitchenItem
import com.example.recipebrowserc.data.entity.MeasurementUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KitchenViewModel @Inject constructor(
    private val kitchenDao: KitchenDao,
    private val unitDao: UnitDao
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _unitById = MutableStateFlow<Map<Int, MeasurementUnit>>(emptyMap())
    private val allItems = kitchenDao.getAllItems()

    init {
        viewModelScope.launch {
            unitDao.getAllUnits().collect { units ->
                _unitById.value = units.associateBy { it.id }
            }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val filteredItems: StateFlow<List<KitchenItem>> = searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                allItems
            } else {
                allItems.map { items ->
                    items.filter { item ->
                        item.name.contains(query, ignoreCase = true)
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableUnits = unitDao.getAllUnits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getUnitById(id: Int): MeasurementUnit? {
        return _unitById.value[id]
    }

    fun addKitchenItem(name: String, quantity: Double, unitId: Int) {
        viewModelScope.launch {
            val item = KitchenItem(
                name = name.trim(),
                quantity = quantity,
                unitId = unitId
            )
            kitchenDao.insert(item)
        }
    }

    fun updateKitchenItem(item: KitchenItem) {
        viewModelScope.launch {
            kitchenDao.update(item)
        }
    }

    fun deleteKitchenItem(item: KitchenItem) {
        viewModelScope.launch {
            kitchenDao.delete(item)
        }
    }
}