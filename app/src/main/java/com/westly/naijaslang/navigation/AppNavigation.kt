package com.westly.naijaslang.navigation

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.westly.naijaslang.R
import com.westly.naijaslang.core.theme.NaijaTheme
import com.westly.naijaslang.feature.detail.WordDetailScreen
import com.westly.naijaslang.feature.favourites.FavouritesScreen
import com.westly.naijaslang.feature.home.HomeScreen
import com.westly.naijaslang.feature.settings.SettingsScreen

/** Routes of the three tabs and of the Word Detail screen (opened from Home or Favourites). */
object Routes {
    const val HOME = "home"
    const val FAVOURITES = "favourites"
    const val SETTINGS = "settings"
    const val DETAIL = "detail/{id}"

    fun detail(id: String) = "detail/${Uri.encode(id)}"
}

private class TabItem(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
)

private val tabs = listOf(
    TabItem(Routes.HOME, R.string.tab_home, Icons.Outlined.Home, Icons.Filled.Home),
    TabItem(Routes.FAVOURITES, R.string.tab_favourites, Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite),
    // The gear stays an outline when selected, as in the design.
    TabItem(Routes.SETTINGS, R.string.tab_settings, Icons.Outlined.Settings, Icons.Outlined.Settings),
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val onDetail = destination?.route == Routes.DETAIL

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        // Each screen draws its own header under the status bar.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AppBottomBar(
                // Word Detail keeps Home selected, as in the design.
                isSelected = { route ->
                    if (onDetail) {
                        route == Routes.HOME
                    } else {
                        destination?.hierarchy?.any { it.route == route } == true
                    }
                },
                onTabClick = { route ->
                    if (onDetail) navController.popBackStack()
                    navController.navigateToTab(route)
                },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(150)) },
            exitTransition = { fadeOut(tween(100)) },
            popEnterTransition = { fadeIn(tween(150)) },
            popExitTransition = { fadeOut(tween(100)) },
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenSettings = { navController.navigateToTab(Routes.SETTINGS) },
                    onOpenWord = { id -> navController.navigate(Routes.detail(id)) },
                )
            }
            composable(Routes.FAVOURITES) {
                FavouritesScreen(onOpenWord = { id -> navController.navigate(Routes.detail(id)) })
            }
            composable(Routes.SETTINGS) { SettingsScreen() }
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { entry ->
                WordDetailScreen(
                    entryId = entry.arguments?.getString("id").orEmpty(),
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun AppBottomBar(
    isSelected: (String) -> Boolean,
    onTabClick: (String) -> Unit,
) {
    val colors = NaijaTheme.colors

    Column {
        HorizontalDivider(thickness = 1.dp, color = colors.divider)
        NavigationBar(
            containerColor = colors.navBar,
            tonalElevation = 0.dp,
        ) {
            for (tab in tabs) {
                val selected = isSelected(tab.route)
                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabClick(tab.route) },
                    icon = {
                        Icon(
                            imageVector = if (selected) tab.selectedIcon else tab.icon,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(tab.label),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.gold,
                        selectedTextColor = colors.gold,
                        unselectedIconColor = colors.navUnselected,
                        unselectedTextColor = colors.navUnselected,
                        indicatorColor = Color.Transparent,
                    ),
                )
            }
        }
    }
}
