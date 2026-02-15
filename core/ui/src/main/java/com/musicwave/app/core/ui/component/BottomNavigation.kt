package com.musicwave.app.core.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation item model.
 */
data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

/**
 * Default navigation items.
 * TODO: Customize these for your app's navigation structure.
 */
object NavItems {
    val Home = NavItem("Home", Icons.Default.Home, "home")
    val Search = NavItem("Search", Icons.Default.Search, "search")
}

/**
 * Bottom navigation bar component.
 */
@Composable
fun AppBottomNavigation(
    items: List<NavItem>,
    currentRoute: String,
    onItemClick: (NavItem) -> Unit
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onItemClick(item) }
            )
        }
    }
}
