package com.musicwave.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.musicwave.app.feature.artist.ArtistScreen
import com.musicwave.app.feature.browse.BrowseScreen
import com.musicwave.app.feature.favorites.FavoritesScreen
import com.musicwave.app.feature.player.MiniPlayer
import com.musicwave.app.feature.player.PlayerScreen
import com.musicwave.app.feature.release.ReleaseScreen
import com.musicwave.app.feature.search.SearchScreen
import com.musicwave.app.ui.screen.SplashScreen
import com.musicwave.app.core.ui.theme.SpotifyBg

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(Screen.Home.route, "Beranda", Icons.Filled.Home),
        BottomNavItem(Screen.Search.route, "Cari", Icons.Filled.Search),
        BottomNavItem(Screen.Favorites.route, "Favorit", Icons.Filled.Favorite)
    )

    Column(modifier = modifier) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val isSplash = currentDestination?.route == Screen.Splash.route

        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.weight(1f)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                BrowseScreen(onAlbumClick = { id ->
                    navController.navigate(Screen.Release.createRoute(id))
                })
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onArtistClick = { id -> navController.navigate(Screen.Artist.createRoute(id)) },
                    onAlbumClick = { id -> navController.navigate(Screen.Release.createRoute(id)) }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(onAlbumClick = { id ->
                    navController.navigate(Screen.Release.createRoute(id))
                })
            }
            composable(
                route = Screen.Artist.route,
                arguments = listOf(navArgument("artistId") { type = NavType.LongType })
            ) { backStack ->
                ArtistScreen(
                    artistId = backStack.arguments?.getLong("artistId") ?: 0L,
                    onAlbumClick = { id -> navController.navigate(Screen.Release.createRoute(id)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.Release.route,
                arguments = listOf(navArgument("albumId") { type = NavType.LongType })
            ) { backStack ->
                ReleaseScreen(
                    albumId = backStack.arguments?.getLong("albumId") ?: 0L,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Player.route) {
                PlayerScreen(onBack = { navController.popBackStack() })
            }
        }

        MiniPlayer(onOpen = {
            navController.navigate(Screen.Player.route)
        })

        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEach { item ->
                val selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == item.route } == true
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp),
                            tint = if (selected) MaterialTheme.colorScheme.onBackground
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    label = {
                        Text(
                            item.label,
                            fontSize = 11.sp,
                            color = if (selected) MaterialTheme.colorScheme.onBackground
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    }
}
