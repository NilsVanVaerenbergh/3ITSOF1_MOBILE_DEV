package edu.ap.rentalapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Iron
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import edu.ap.rentalapp.entities.BottomNavItem
import edu.ap.rentalapp.entities.User
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.ui.screens.myRentals.MyRentalsScreen
import edu.ap.rentalapp.ui.shared.NavigationBottomBar
import edu.ap.rentalapp.ui.shared.SharedTopAppBar

@Composable
fun AppScreen() {

    val context = LocalContext.current
    val authenticationManager = remember { AuthenticationManager(context) }
    val isAuthenticated = remember { authenticationManager.isAuthenticated() }

    val navController = rememberNavController()

    val showTopBarRoutes = mapOf(
        "home" to "Home",
        "myReservations" to "Reservations",
        "myRentals" to "My Rentals",
        "profile" to "Profile",
        "editUserName/{user}" to "Edit Username",
        "editLocation" to "Edit Location"
    )
    val showBottomBarRoutes = listOf("home", "myReservations", "myRentals", "profile")//, "editUserName", "editLocation")
    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Outlined.Home, "home"),
        BottomNavItem("Reservations", Icons.Outlined.BookmarkBorder, "myReservations"),
        BottomNavItem("My Rentals", Icons.Outlined.Iron, "myRentals")
    )

    // Observe authentication state
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate("home") {
                popUpTo("signIn") { inclusive = true }
            }
//            RequestLocationPermission( onPermissionGranted = {
//                getCurrentLocation(context) { location ->
//                    if (location != null){
//                        saveUserLocationToFirebase(context, location.latitude, location.longitude)
//                    }
//                }
//            })
        } else {
            navController.navigate("signIn") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute in showTopBarRoutes.keys) {
                SharedTopAppBar(
                    title = showTopBarRoutes[currentRoute] ?: "",
                    navController = navController
                )
            }

        },
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute in showBottomBarRoutes) {
//                SharedBottomBar(navController = navController)
                NavigationBottomBar(
                    navController = navController,
                    bottomNavItems = bottomNavItems
                )
            }
        }
    )
    { innerPadding ->
        // Navigation Graph
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = if (isAuthenticated) "home" else "signIn"
        ) {
            composable("signIn") {
                SignInScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    navController = navController
                )
            }
            composable("signUp") {
                SignUpScreen(
                    onSignUpSuccess = {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    navController = navController
                )
            }
            composable("profile") {
                UserProfileScreen(
                    context = context,
                    navController = navController
                )
            }
            composable(
                route = "editUserName/{user}",
                arguments = listOf(navArgument("user") { type = NavType.StringType })
            ) { backStackEntry ->
                val userData = backStackEntry.arguments?.getString("user")
                val user = Gson().fromJson(userData, User::class.java)
                EditUserNameScreen(
                    context = context,
                    navController = navController,
                    userData = user
                )
            }
            composable(
                route = "editLocation"
            ) {
                EditLocationScreen(
                    context = context,
                    navController = navController
                )
            }
            composable(route = "home") {
                RentalOverViewScreen(
                    //authViewModel = authViewModel, // Pass the ViewModel to log out
                    navController = navController
                )
            }
            composable("addAppliance") {
                AddApplianceScreen(
                    navController = navController
                )
            }
            composable("myRentals") {
                MyRentalsScreen(
                    navController = navController
                )
            }
            composable("myReservations") {
                MyReservationsScreen()
            }
        }
    }
}