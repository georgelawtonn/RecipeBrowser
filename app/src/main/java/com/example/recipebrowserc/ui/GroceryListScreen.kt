package com.example.recipebrowserc.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipebrowserc.data.entity.GroceryList
import com.example.recipebrowserc.viewmodel.GroceryListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListScreen(
    viewModel: GroceryListViewModel = hiltViewModel(),
    onNavigateToGroceryListDetail: (Int) -> Unit,
    onNavigateToNewList: () -> Unit
) {
    val groceryLists by viewModel.groceryLists.collectAsState(initial = emptyList())
    var showDeleteConfirmation by remember { mutableStateOf<GroceryList?>(null) }
    var deletedItems = remember { mutableStateListOf<Int>() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewList
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Create new grocery list")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (groceryLists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                ) {
                    Text(
                        text = "No grocery lists yet\nTap + to create one",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = groceryLists,
                        key = { it.id }
                    ) { groceryList ->
                        AnimatedVisibility(
                            visible = !deletedItems.contains(groceryList.id),
                            exit = shrinkVertically(
                                animationSpec = tween(durationMillis = 300)
                            ) + fadeOut()
                        ) {
                            ListItem(
                                headlineContent = { Text(groceryList.name) },
                                trailingContent = {
                                    IconButton(
                                        onClick = { showDeleteConfirmation = groceryList }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete list",
                                            tint = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                },
                                modifier = Modifier.clickable {
                                    onNavigateToGroceryListDetail(groceryList.id)
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
    }

    showDeleteConfirmation?.let { groceryList ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Delete Grocery List") },
            text = { Text("Are you sure you want to delete '${groceryList.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deletedItems.add(groceryList.id)
                        viewModel.deleteGroceryList(groceryList)
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