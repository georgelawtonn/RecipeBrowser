package com.example.recipebrowserc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipebrowserc.viewmodel.GroceryListViewModel

@Composable
fun NewGroceryListScreen(
    viewModel: GroceryListViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    var listName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Create New Grocery List",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = listName,
            onValueChange = { listName = it },
            label = { Text("List Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                if (listName.isNotBlank()) {
                    viewModel.createGroceryList(listName)
                    onNavigateBack()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Create List")
        }
    }
}