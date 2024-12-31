package com.example.recipebrowserc.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebrowserc.viewmodel.RecipeDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToGroceryList: (Int) -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    var showAddToGroceryListDialog by remember { mutableStateOf(false) }
    val recipe by viewModel.recipe.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState(initial = emptyList())
    val instructions by viewModel.instructions.collectAsState(initial = emptyList())
    val groceryLists by viewModel.groceryLists.collectAsState(initial = emptyList())

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(recipeId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit recipe")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Author section
            recipe?.author?.takeIf { it.isNotBlank() }?.let { author ->
                item {
                    Text(
                        text = "By $author",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            item {
                FilledTonalButton(
                    onClick = { showAddToGroceryListDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Ingredients to a Grocery List")
                }
            }

            // Ingredients section
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Ingredients",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        ingredients.forEach { ingredient ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    ingredient.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "${ingredient.quantity} ${viewModel.getUnitById(ingredient.unitId)?.abbreviation ?: ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (ingredient != ingredients.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Instructions section
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Instructions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        instructions.forEach { instruction ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "Step ${instruction.stepNumber}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    instruction.instruction,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            if (instruction != instructions.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showAddToGroceryListDialog) {
            var newListName by remember { mutableStateOf("") }
            var showNewListInput by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showAddToGroceryListDialog = false },
                title = { Text("Add to Grocery List") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (showNewListInput) {
                            OutlinedTextField(
                                value = newListName,
                                onValueChange = { newListName = it },
                                label = { Text("List Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        } else {
                            Button(
                                onClick = { showNewListInput = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Create New List")
                            }

                            if (groceryLists.isNotEmpty()) {
                                Text(
                                    "Or choose existing list:",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                groceryLists.forEach { list ->
                                    ListItem(
                                        headlineContent = { Text(list.name) },
                                        modifier = Modifier.clickable {
                                            viewModel.addIngredientsToGroceryList(list.id)
                                            showAddToGroceryListDialog = false
                                            onNavigateToGroceryList(list.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    if (showNewListInput) {
                        TextButton(
                            onClick = {
                                if (newListName.isNotBlank()) {
                                    viewModel.viewModelScope.launch {
                                        val newListId = viewModel.createNewListWithIngredients(newListName.trim())
                                        showAddToGroceryListDialog = false
                                        onNavigateToGroceryList(newListId)
                                    }
                                }
                            },
                            enabled = newListName.isNotBlank()
                        ) {
                            Text("Create")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            if (showNewListInput) {
                                showNewListInput = false
                                newListName = ""
                            } else {
                                showAddToGroceryListDialog = false
                            }
                        }
                    ) {
                        Text(if (showNewListInput) "Back" else "Cancel")
                    }
                }
            )
        }
    }
}