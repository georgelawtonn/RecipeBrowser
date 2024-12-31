package com.example.recipebrowserc.ui

import androidx.compose.foundation.layout.*
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
import com.example.recipebrowserc.data.entity.Ingredient
import com.example.recipebrowserc.data.entity.RecipeInstruction
import com.example.recipebrowserc.viewmodel.AddEditRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditIngredientDialog(
    editingIngredient: Ingredient? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, quantity: Double, unitId: Int) -> Unit,
    onNavigateToUnitManager: () -> Unit,
    viewModel: AddEditRecipeViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf(editingIngredient?.name ?: "") }
    var quantity by remember { mutableStateOf(editingIngredient?.quantity?.toString() ?: "") }
    var showUnitPicker by remember { mutableStateOf(false) }
    val units by viewModel.availableUnits.collectAsState()
    var selectedUnit by remember {
        mutableStateOf(editingIngredient?.let { viewModel.getUnitById(it.unitId) })
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editingIngredient == null) "Add Ingredient" else "Edit Ingredient") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Ingredient Name") },
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
                    if (name.isNotBlank() && selectedUnit != null) {
                        onSave(name.trim(), quantityValue, selectedUnit!!.id)
                    }
                },
                enabled = name.isNotBlank() && quantity.isNotBlank() && selectedUnit != null
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditInstructionDialog(
    instruction: RecipeInstruction? = null,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var instructionText by remember { mutableStateOf(instruction?.instruction ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (instruction == null) "Add Step" else "Edit Step") },
        text = {
            OutlinedTextField(
                value = instructionText,
                onValueChange = { instructionText = it },
                label = { Text("Step Instructions") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (instructionText.isNotBlank()) {
                        onSave(instructionText.trim())
                    }
                },
                enabled = instructionText.isNotBlank()
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
}