package com.example.recipebrowserc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipebrowserc.data.entity.MeasurementUnit
import com.example.recipebrowserc.data.entity.UnitCategory
import com.example.recipebrowserc.viewmodel.GroceryListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitManagerScreen(
    onNavigateBack: () -> Unit,
    viewModel: GroceryListViewModel = hiltViewModel()
) {
    var showAddUnitDialog by remember { mutableStateOf(false) }
    val units by viewModel.availableUnits.collectAsState()
    var selectedUnit by remember { mutableStateOf<MeasurementUnit?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Units") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddUnitDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add new unit")
            }
        }
    ) { paddingValues ->
        val groupedUnits = units.groupBy { it.category }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            groupedUnits.forEach { (category, unitsInCategory) ->
                item {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(unitsInCategory) { unit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = unit.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = unit.abbreviation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { selectedUnit = unit }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete unit",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        if (showAddUnitDialog) {
            AddUnitDialog(
                onDismiss = { showAddUnitDialog = false },
                onUnitAdded = { showAddUnitDialog = false },
                viewModel = viewModel
            )
        }

        selectedUnit?.let { unit ->
            DeleteUnitConfirmationDialog(
                unit = unit,
                onConfirm = {
                    viewModel.deleteUnit(unit)
                    selectedUnit = null
                },
                onDismiss = { selectedUnit = null }
            )
        }
    }
}

@Composable
private fun AddUnitDialog(
    onDismiss: () -> Unit,
    onUnitAdded: () -> Unit,
    viewModel: GroceryListViewModel
) {
    var name by remember { mutableStateOf("") }
    var abbreviation by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<UnitCategory?>(null) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Unit") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Unit Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = abbreviation,
                    onValueChange = { abbreviation = it },
                    label = { Text("Abbreviation") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                TextButton(
                    onClick = { showCategoryPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedCategory?.displayName ?: "Select Category")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && abbreviation.isNotBlank() && selectedCategory != null) {
                        viewModel.addUnit(name.trim(), abbreviation.trim(), selectedCategory!!)
                        onUnitAdded()
                    }
                },
                enabled = name.isNotBlank() && abbreviation.isNotBlank() && selectedCategory != null
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showCategoryPicker) {
        CategoryPickerDialog(
            onCategorySelected = {
                selectedCategory = it
                showCategoryPicker = false
            },
            onDismiss = { showCategoryPicker = false }
        )
    }
}

@Composable
private fun CategoryPickerDialog(
    onCategorySelected: (UnitCategory) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Category") },
        text = {
            LazyColumn {
                items(UnitCategory.entries.toList()) { category ->
                    TextButton(
                        onClick = { onCategorySelected(category) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(category.displayName)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DeleteUnitConfirmationDialog(
    unit: MeasurementUnit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Unit") },
        text = {
            Text("Are you sure you want to delete ${unit.name}? This action cannot be undone if the unit is not in use.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}