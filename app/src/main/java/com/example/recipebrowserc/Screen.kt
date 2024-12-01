package com.example.recipebrowserc

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int,
    @DrawableRes val iconResourceId: Int
) {
    object GroceryList : Screen(
        "grocery_list",
        R.string.grocery_list,
        R.drawable.ic_grocery_list
    )

    object Kitchen : Screen(
        "kitchen",
        R.string.kitchen,
        R.drawable.ic_kitchen
    )

    object Recipes : Screen(
        "recipes",
        R.string.recipes,
        R.drawable.ic_recipes
    )

    companion object {
        fun fromRoute(route: String?): Screen {
            return when (route) {
                GroceryList.route -> GroceryList
                Kitchen.route -> Kitchen
                Recipes.route -> Recipes
                null -> GroceryList
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
        }
    }
}