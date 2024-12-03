package edu.ap.rentalapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import edu.ap.rentalapp.entities.User
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.ui.screens.myRentals.MyRentalsScreen

@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    val authenticationManager = remember { AuthenticationManager(context) }
    val isAuthenticated = remember { authenticationManager.isAuthenticated() }
    
    val navController = rememberNavController()

    // Observe authentication state
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate("home") {
                popUpTo("signIn") { inclusive = true }
            }
        } else {
            navController.navigate("signIn") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    // Navigation Graph
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "home" else "signIn"
    ) {
        composable("signIn") {
            SignInScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("signIn") { inclusive = true }
                    }
                },
                navController = navController
            )
        }
        composable("signUp"){
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate("home") {
                        popUpTo("signUp") { inclusive = true }
                    }
                },
                navController = navController
            )
        }
        composable("profile"){
            UserProfileScreen(
                context = context,
                navController = navController
            )
        }
        composable(
            route = "editUserName/{user}",
            arguments = listOf(navArgument("user") {type = NavType.StringType})
        ){ backStackEntry ->
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
        ){
            EditLocationScreen(
                context = context,
                navController = navController
            )
        }
        composable("home") {
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
    }
}