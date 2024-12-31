package com.example.recipebrowserc.ui

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
import com.example.recipebrowserc.data.entity.*
import com.example.recipebrowserc.viewmodel.AddEditRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecipeScreen(
    recipeId: Int? = null,
    onNavigateBack: () -> Unit,
    onNavigateToUnitManager: () -> Unit,
    viewModel: AddEditRecipeViewModel = hiltViewModel()
) {
    var showIngredientDialog by remember { mutableStateOf(false) }
    var showInstructionDialog by remember { mutableStateOf(false) }
    var editingIngredient by remember { mutableStateOf<Ingredient?>(null) }
    var editingInstruction by remember { mutableStateOf<RecipeInstruction?>(null) }

    LaunchedEffect(recipeId) {
        if (recipeId != null) {
            viewModel.loadRecipe(recipeId)
        } else {
            viewModel.clearRecipe()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (recipeId == null) "Create Recipe" else "Edit Recipe") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveRecipe()
                            onNavigateBack()
                        },
                        enabled = viewModel.canSaveRecipe
                    ) {
                        Text("Save")
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
            // Basic Recipe Information
            item {
                OutlinedTextField(
                    value = viewModel.recipeName,
                    onValueChange = { viewModel.updateRecipeName(it) },
                    label = { Text("Recipe Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.author,
                    onValueChange = { viewModel.updateAuthor(it) },
                    label = { Text("Author") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Ingredients Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Ingredients",
                                style = MaterialTheme.typography.titleMedium
                            )
                            IconButton(onClick = { showIngredientDialog = true }) {
                                Icon(Icons.Default.Add, "Add ingredient")
                            }
                        }
                        viewModel.ingredients.forEach { ingredient ->
                            ListItem(
                                headlineContent = { Text(ingredient.name) },
                                supportingContent = {
                                    Text("${ingredient.quantity} ${viewModel.getUnitById(ingredient.unitId)?.abbreviation ?: ""}")
                                },
                                trailingContent = {
                                    Row {
                                        IconButton(onClick = { editingIngredient = ingredient }) {
                                            Icon(Icons.Default.Edit, "Edit ingredient")
                                        }
                                        IconButton(onClick = { viewModel.removeIngredient(ingredient) }) {
                                            Icon(Icons.Default.Delete, "Remove ingredient")
                                        }
                                    }
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }

            // Instructions Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Instructions",
                                style = MaterialTheme.typography.titleMedium
                            )
                            IconButton(onClick = { showInstructionDialog = true }) {
                                Icon(Icons.Default.Add, "Add step")
                            }
                        }
                        viewModel.instructions.forEachIndexed { index, instruction ->
                            ListItem(
                                headlineContent = { Text("Step ${index + 1}") },
                                supportingContent = { Text(instruction.instruction) },
                                trailingContent = {
                                    Row {
                                        IconButton(onClick = { editingInstruction = instruction }) {
                                            Icon(Icons.Default.Edit, "Edit step")
                                        }
                                        IconButton(onClick = { viewModel.removeInstruction(instruction) }) {
                                            Icon(Icons.Default.Delete, "Remove step")
                                        }
                                    }
                                }
                            )
                            if (index < viewModel.instructions.size - 1) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }

        // Dialogs
        if (showIngredientDialog) {
            AddEditIngredientDialog(
                editingIngredient = null,
                onDismiss = { showIngredientDialog = false },
                onSave = { name, quantity, unitId ->
                    viewModel.addIngredient(name, quantity, unitId)
                    showIngredientDialog = false
                },
                onNavigateToUnitManager = onNavigateToUnitManager
            )
        }

        editingIngredient?.let { ingredient ->
            AddEditIngredientDialog(
                editingIngredient = ingredient,
                onDismiss = { editingIngredient = null },
                onSave = { name, quantity, unitId ->
                    viewModel.updateIngredient(ingredient.copy(
                        name = name,
                        quantity = quantity,
                        unitId = unitId
                    ))
                    editingIngredient = null
                },
                onNavigateToUnitManager = onNavigateToUnitManager
            )
        }

        if (showInstructionDialog) {
            AddEditInstructionDialog(
                instruction = null,
                onDismiss = { showInstructionDialog = false },
                onSave = { instruction ->
                    viewModel.addInstruction(instruction)
                    showInstructionDialog = false
                }
            )
        }

        editingInstruction?.let { instruction ->
            AddEditInstructionDialog(
                instruction = instruction,
                onDismiss = { editingInstruction = null },
                onSave = { updatedInstruction ->
                    viewModel.updateInstruction(instruction.copy(
                        instruction = updatedInstruction
                    ))
                    editingInstruction = null
                }
            )
        }
    }
}