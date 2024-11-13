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
import edu.ap.rentalapp.ui.theme.RentalAppTheme

class MainActivity : AuthActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RentalAppTheme {
               mainScreen()
            }
        }
    }
}

@Composable
fun mainScreen() {
    val context = LocalContext.current
    val authenticationManager = remember { AuthenticationManager(context) }
    Text(
        text = "Hello from main!",
    )
    Spacer(modifier = Modifier.height(100.dp))
    Button(
        onClick = {authenticationManager.signOut()}
    ) { Text("Log uit.") }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RentalAppTheme {
        mainScreen()
    }
}