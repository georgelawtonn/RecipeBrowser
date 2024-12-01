package com.example.recipebrowserc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipebrowserc.data.entity.KitchenItem
import com.example.recipebrowserc.viewmodel.KitchenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenScreen(
    viewModel: KitchenViewModel = hiltViewModel(),
    onNavigateToUnitManager: () -> Unit
) {
    var showAddItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<KitchenItem?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<KitchenItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val kitchenItems by viewModel.filteredItems.collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddItemDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add item")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.setSearchQuery(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search items...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true
            )

            if (kitchenItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isEmpty())
                            "No items in your pantry\nTap + to add items"
                        else
                            "No items match your search",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = kitchenItems,
                        key = { it.id }
                    ) { item ->
                        ListItem(
                            headlineContent = { Text(item.name) },
                            supportingContent = {
                                Text(
                                    "${item.quantity} ${viewModel.getUnitById(item.unitId)?.abbreviation ?: ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = { editingItem = item }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit item"
                                        )
                                    }
                                    IconButton(onClick = { showDeleteConfirmation = item }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete item",
                                            tint = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Dialogs remain the same...
    // Add/Edit Dialog
    if (showAddItemDialog) {
        AddEditKitchenItemDialog(
            editingItem = null,
            onDismiss = { showAddItemDialog = false },
            onItemSaved = { name, quantity, unitId ->
                viewModel.addKitchenItem(name, quantity, unitId)
                showAddItemDialog = false
            },
            onNavigateToUnitManager = onNavigateToUnitManager
        )
    }

    editingItem?.let { item ->
        AddEditKitchenItemDialog(
            editingItem = item,
            onDismiss = { editingItem = null },
            onItemSaved = { name, quantity, unitId ->
                viewModel.updateKitchenItem(item.copy(
                    name = name,
                    quantity = quantity,
                    unitId = unitId
                ))
                editingItem = null
            },
            onNavigateToUnitManager = onNavigateToUnitManager
        )
    }

    showDeleteConfirmation?.let { item ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Delete Item") },
            text = { Text("Are you sure you want to delete '${item.name}' from your pantry?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteKitchenItem(item)
                        showDeleteConfirmation = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}