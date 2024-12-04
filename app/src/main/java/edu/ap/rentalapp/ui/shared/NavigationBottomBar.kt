package edu.ap.rentalapp.ui.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Iron
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ap.rentalapp.entities.BottomNavItem

@Composable
fun NavigationBottomBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    bottomNavItems: List<BottomNavItem>
) {
    NavigationBar(
        modifier = modifier
            //.clip(BottomNavCurve())
    ) {
        val currentDestination = navController.currentBackStackEntry?.destination?.route

        for (item in bottomNavItems) {
            NavigationBarItem(
                selected = navController.currentDestination?.route == item.route,
                onClick = {
                    if (currentDestination != item.route) {
                        navController.navigate(item.route) { launchSingleTop }
                        modifier.clip(BottomNavCurve())
                    }
                },
                icon = { Icon(item.icon, item.label) },
                label = { Text(item.label) },
            )
        }


    }
}

@Preview
@Composable
fun Show(modifier: Modifier = Modifier) {
    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Outlined.Home, "home"),
        BottomNavItem("Reservations", Icons.Outlined.BookmarkBorder, "myReservations"),
        BottomNavItem("My Rentals", Icons.Outlined.Iron, "myRentals")
    )
    val navController = rememberNavController()
    NavigationBottomBar(
        navController = navController,
        bottomNavItems = bottomNavItems
    )

}