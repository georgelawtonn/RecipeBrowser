package com.example.recipebrowserc.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipebrowserc.data.entity.Recipe
import com.example.recipebrowserc.viewmodel.RecipesViewModel

@Composable
fun RecipesScreen(
    viewModel: RecipesViewModel = hiltViewModel(),
    onNavigateToRecipeDetails: (Int) -> Unit
) {
    val recipes by viewModel.recipes.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::setSearchQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        if (recipes.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No recipes found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(recipes) { recipe ->
                    RecipeListItem(
                        recipe = recipe,
                        onClick = { onNavigateToRecipeDetails(recipe.id) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { /* TODO: Implement create recipe */ }) {
                Text("Create Recipe")
            }
            Button(onClick = { viewModel.suggestRecipe() }) {
                Text("Suggest Recipe")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Recipe Name/Author Query") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true
    )
}

@Composable
fun RecipeListItem(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = recipe.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = recipe.author, style = MaterialTheme.typography.bodyMedium)
            // TODO: Add image loading logic
        }
    }
}