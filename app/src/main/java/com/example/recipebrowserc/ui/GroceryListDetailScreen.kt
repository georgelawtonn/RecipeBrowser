package com.example.recipebrowserc.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
                    items(groceryItems) { item ->
                        GroceryItemRow(
                            item = item,
                            unit = viewModel.getUnitById(item.unitId),
                            onItemClick = { editingItem = it },
                            onCheckedChange = { checked ->
                                viewModel.updateGroceryItemCheckedStatus(item.copy(isChecked = checked))
                            }
                        )
                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
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

@Composable
private fun GroceryItemRow(
    item: GroceryItem,
    unit: MeasurementUnit?,
    onItemClick: (GroceryItem) -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${item.quantity} ${unit?.abbreviation ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}