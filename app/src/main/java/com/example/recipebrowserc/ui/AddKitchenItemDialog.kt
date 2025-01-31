package com.example.recipebrowserc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipebrowserc.data.entity.KitchenItem
import com.example.recipebrowserc.data.entity.MeasurementUnit
import com.example.recipebrowserc.viewmodel.KitchenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditKitchenItemDialog(
    editingItem: KitchenItem? = null,
    onDismiss: () -> Unit,
    onItemSaved: (name: String, quantity: Double, unitId: Int) -> Unit,
    onNavigateToUnitManager: () -> Unit,
    viewModel: KitchenViewModel = hiltViewModel()
) {
    var itemName by remember { mutableStateOf(editingItem?.name ?: "") }
    var quantity by remember { mutableStateOf(editingItem?.quantity?.toString() ?: "") }
    var showUnitPicker by remember { mutableStateOf(false) }
    val units by viewModel.availableUnits.collectAsState()
    var selectedUnit by remember {
        mutableStateOf(editingItem?.let { viewModel.getUnitById(it.unitId) })
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (editingItem == null) "Add Kitchen Item" else "Edit Kitchen Item") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Column {
                    Text(
                        text = "Unit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showUnitPicker = true }
                        ) {
                            Text(selectedUnit?.name ?: "Select Unit")
                        }
                        IconButton(onClick = onNavigateToUnitManager) {
                            Icon(Icons.Default.Add, "Add new unit")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val quantityValue = quantity.toDoubleOrNull() ?: 0.0
                    if (itemName.isNotBlank() && selectedUnit != null) {
                        onItemSaved(itemName, quantityValue, selectedUnit!!.id)
                    }
                },
                enabled = itemName.isNotBlank() && quantity.isNotBlank() && selectedUnit != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showUnitPicker) {
        UnitPickerDialog(
            units = units,
            onUnitSelected = {
                selectedUnit = it
                showUnitPicker = false
            },
            onDismiss = { showUnitPicker = false }
        )
    }
}