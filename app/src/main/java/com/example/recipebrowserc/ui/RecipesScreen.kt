package com.example.recipebrowserc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipebrowserc.data.entity.Recipe
import com.example.recipebrowserc.viewmodel.RecipesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    viewModel: RecipesViewModel = hiltViewModel(),
    onNavigateToRecipeDetails: (Int) -> Unit,
    onNavigateToAddRecipe: () -> Unit
) {
    val recipes by viewModel.recipes.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showingSuggestions by viewModel.showingSuggestions.collectAsState()

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                SmallFloatingActionButton(
                    onClick = { viewModel.suggestRecipe() },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        Icons.Outlined.Star,
                        contentDescription = "Suggest recipes based on kitchen inventory"
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                FloatingActionButton(
                    onClick = onNavigateToAddRecipe
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Recipe")
                }
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
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = {
                    Text(
                        if (showingSuggestions) "Showing recipes based on your kitchen inventory"
                        else "Search recipes..."
                    )
                },
                leadingIcon = {
                    if (showingSuggestions) {
                        Icon(Icons.Outlined.Star, contentDescription = "Suggestions active")
                    } else {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                trailingIcon = if (showingSuggestions) {
                    {
                        IconButton(onClick = { viewModel.clearSuggestions() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear suggestions")
                        }
                    }
                } else null,
                singleLine = true,
                enabled = !showingSuggestions
            )

            if (recipes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            showingSuggestions -> "No recipes match your kitchen inventory"
                            searchQuery.isBlank() -> "No recipes yet\nTap + to add a recipe"
                            else -> "No recipes match your search"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = recipes,
                        key = { it.id }
                    ) { recipe ->
                        ListItem(
                            headlineContent = { Text(recipe.name) },
                            supportingContent = recipe.author.takeIf { it.isNotBlank() }?.let {
                                { Text(it) }
                            },
                            trailingContent = {
                                IconButton(onClick = { onNavigateToRecipeDetails(recipe.id) }) {
                                    Icon(
                                        Icons.Default.ArrowForward,
                                        contentDescription = "View recipe details",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
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
}