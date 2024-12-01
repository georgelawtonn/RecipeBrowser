package com.example.recipebrowserc.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipebrowserc.R
import com.example.recipebrowserc.data.entity.GroceryList
import com.example.recipebrowserc.viewmodel.GroceryListViewModel

@Composable
fun GroceryListScreen(
    viewModel: GroceryListViewModel = hiltViewModel(),
    onNavigateToGroceryListDetail: (Int) -> Unit,
    onNavigateToNewList: () -> Unit
) {
    val groceryLists by viewModel.groceryLists.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Grocery Lists",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(groceryLists) { groceryList ->
                GroceryListItem(
                    groceryList = groceryList,
                    onClick = { onNavigateToGroceryListDetail(groceryList.id) }
                )
            }
        }

        FloatingActionButton(
            onClick = onNavigateToNewList,
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Create new grocery list")
        }
    }
}

@Composable
fun GroceryListItem(
    groceryList: GroceryList,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = groceryList.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "View/Edit grocery list details",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}