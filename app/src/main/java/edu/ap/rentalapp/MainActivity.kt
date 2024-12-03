package edu.ap.rentalapp

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.middleware.AuthActivity
import edu.ap.rentalapp.ui.screens.AddApplianceScreen
import edu.ap.rentalapp.ui.screens.MyRentalsScreen
import edu.ap.rentalapp.ui.screens.rentalOverViewScreen

class MainActivity : AuthActivity() {
    override fun getTopBarTitle(): String = "Welkom"
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ScreenContent(modifier: Modifier, context: Context) {
        val authenticationManager = remember { AuthenticationManager(context) }
        val navController = rememberNavController()
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        Log.d("route", "ScreenContent: $currentRoute")


        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "rentalsOverview") {
                        composable("addAppliance") {
                            AddApplianceScreen(
                                modifier = Modifier.padding(
                                    innerPadding
                                ), navController = navController
                            )
                        }
                        composable(route = "rentalsOverview") {
                            rentalOverViewScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController
                            )
                        }
                        composable(
                            route = "myRentals"
                        ) {
                            MyRentalsActivity.MyRentalTest(
                                viewModel = MyRentalsViewModel(),
                                navController = navController
                            )
//                            MyRentalsScreen(
//                                modifier = Modifier.padding(innerPadding),
//                                navController = navController
//                            )
                        }
                        composable(route = "test"){
                            Test.TestScreen()
                        }
                    }
                }
    }
}
