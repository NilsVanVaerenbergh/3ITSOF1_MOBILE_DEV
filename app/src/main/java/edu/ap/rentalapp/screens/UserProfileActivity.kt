package edu.ap.rentalapp.screens

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
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

class UserProfileActivity : AuthActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RentalAppTheme {
                userProfileScreen()
            }
        }
    }
}

@Composable
fun userProfileScreen() {
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
fun UserProfilePreview() {
    RentalAppTheme {
        userProfileScreen()
    }
}