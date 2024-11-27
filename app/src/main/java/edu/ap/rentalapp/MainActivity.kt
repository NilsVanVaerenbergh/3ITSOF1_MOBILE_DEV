package edu.ap.rentalapp

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.middleware.AuthActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.ap.rentalapp.ui.screens.AddApplianceScreen
import edu.ap.rentalapp.ui.screens.MyRentalsScreen
import edu.ap.rentalapp.ui.screens.rentalOverViewScreen

class MainActivity : AuthActivity() {
    override fun getTopBarTitle(): String = "Welkom"
    @Composable
    override fun ScreenContent(modifier: Modifier, context: Context) {
        val authenticationManager = remember { AuthenticationManager(context) }
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
                            MyRentalsScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController
                            )
                        }
                    }
                }
    }
}
