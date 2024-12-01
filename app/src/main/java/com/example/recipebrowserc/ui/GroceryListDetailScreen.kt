package com.example.recipebrowserc.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipebrowserc.data.entity.GroceryItem
import com.example.recipebrowserc.data.entity.MeasurementUnit
import com.example.recipebrowserc.viewmodel.GroceryListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListDetailScreen(
    groceryListId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToUnitManager: () -> Unit,
    viewModel: GroceryListViewModel = hiltViewModel()
) {
    val groceryItems by viewModel.getGroceryItems(groceryListId).collectAsState(initial = emptyList())
    var showAddItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<GroceryItem?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<GroceryItem?>(null) }

    LaunchedEffect(groceryListId) {
        viewModel.loadGroceryList(groceryListId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = viewModel.currentGroceryList.value?.name ?: "Grocery List",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddItemDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (groceryItems.isEmpty()) {
                EmptyListMessage(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = groceryItems,
                        key = { it.id }
                    ) { item ->
                        ListItem(
                            headlineContent = {
                                Text(item.name)
                            },
                            supportingContent = {
                                Text(
                                    "${item.quantity} ${viewModel.getUnitById(item.unitId)?.abbreviation ?: ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            leadingContent = {
                                Checkbox(
                                    checked = item.isChecked,
                                    onCheckedChange = { checked ->
                                        viewModel.updateGroceryItemCheckedStatus(item.copy(isChecked = checked))
                                    }
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
                        Divider()
                    }
                }
            }

            if (showAddItemDialog) {
                AddEditGroceryItemDialog(
                    groceryListId = groceryListId,
                    onDismiss = { showAddItemDialog = false },
                    onItemAdded = { showAddItemDialog = false },
                    onNavigateToUnitManager = onNavigateToUnitManager
                )
            }

            editingItem?.let { item ->
                AddEditGroceryItemDialog(
                    groceryListId = groceryListId,
                    editingItem = item,
                    onDismiss = { editingItem = null },
                    onItemAdded = { editingItem = null },
                    onNavigateToUnitManager = onNavigateToUnitManager
                )
            }

            showDeleteConfirmation?.let { item ->
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmation = null },
                    title = { Text("Delete Item") },
                    text = { Text("Are you sure you want to delete '${item.name}'?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteGroceryItem(item)
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
    }
}

@Composable
private fun EmptyListMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No items in this list",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Tap + to add items",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}