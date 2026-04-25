package com.musicwave.app.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Artist : Screen("artist/{artistId}") {
        fun createRoute(artistId: Long) = "artist/$artistId"
    }
    data object Release : Screen("album/{albumId}") {
        fun createRoute(albumId: Long) = "album/$albumId"
    }
    data object Favorites : Screen("favorites")
    data object Player : Screen("player")
}
