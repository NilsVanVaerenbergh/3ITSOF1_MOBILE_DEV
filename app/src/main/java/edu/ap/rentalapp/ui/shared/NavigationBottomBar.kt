package edu.ap.rentalapp.ui.shared

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import edu.ap.rentalapp.entities.BottomNavItem

@Composable
fun NavigationBottomBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    bottomNavItems: List<BottomNavItem>
) {
    NavigationBar {
        val currentDestination = navController.currentBackStackEntry?.destination?.route
        for (item in bottomNavItems) {
            NavigationBarItem(
                selected = navController.currentDestination?.route == item.route,
                onClick = {
                    if (currentDestination != item.route) {
                        navController.navigate(item.route){ launchSingleTop }
                    }
                },
                icon = { Icon(item.icon, item.label) },
                label = { Text(item.label) },
            )
        }

    }
}