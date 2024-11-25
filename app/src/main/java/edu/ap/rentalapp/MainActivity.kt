package edu.ap.rentalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.middleware.AuthActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.ap.rentalapp.ui.theme.RentalAppTheme
import edu.ap.rentalapp.ui.screens.AddApplianceScreen
import edu.ap.rentalapp.ui.screens.MyRentalsScreen

class MainActivity : AuthActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RentalAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "addAppliance") {
                        composable("addAppliance") {
                            AddApplianceScreen(
                                modifier = Modifier.padding(
                                    innerPadding
                                ), navController = navController
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
    }
}
