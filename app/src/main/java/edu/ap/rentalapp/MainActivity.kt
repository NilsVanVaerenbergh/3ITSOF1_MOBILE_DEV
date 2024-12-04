package edu.ap.rentalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import edu.ap.rentalapp.ui.screens.AppScreen
import edu.ap.rentalapp.ui.theme.RentalAppTheme

class MainActivity : ComponentActivity() {
//    override fun getTopBarTitle(): String = "Welkom"
//    @OptIn(ExperimentalMaterial3Api::class)
//    @Composable
//    override fun ScreenContent(modifier: Modifier, context: Context) {
//        val authenticationManager = remember { AuthenticationManager(context) }
//        val navController = rememberNavController()
//        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
//        Log.d("route", "ScreenContent: $currentRoute")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RentalAppTheme {
                AppScreen()
            }
        }
    }

}

