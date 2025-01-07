# Recipe & Grocery Management App

An Android application built with Jetpack Compose that helps users manage recipes, track kitchen inventory, and create grocery lists.

## Features

### Recipe Management
- Create and store recipes with ingredients and step-by-step instructions
- Edit existing recipes
- Search through recipe collection
- Get recipe suggestions based on available kitchen ingredients

### Kitchen Inventory
- Track ingredients available in your kitchen
- Add, edit, and remove items
- Maintain quantities with customizable measurement units

### Grocery Lists
- Create multiple grocery lists
- Add items manually or directly from recipes
- Mark items as purchased
- Custom measurement unit support
- Edit and delete lists

## Technical Details

### Built With
- Kotlin
- Jetpack Compose for UI
- Material Design 3
- Room Database for local storage
- Hilt for dependency injection
- ViewModel & StateFlow for state management
- Navigation Component for screen navigation

### Architecture
- MVVM (Model-View-ViewModel) architecture
- Clean separation of concerns
- Repository pattern for data operations
- Single activity, multiple composable screens

### Database Schema
- Recipes with ingredients and instructions
- Kitchen inventory items
- Grocery lists and items
- Measurement units with categories
