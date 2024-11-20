package edu.ap.rentalapp

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.middleware.AuthActivity


class MainActivity : AuthActivity() {
    override fun getTopBarTitle(): String = "Welkom"

    @Composable
    override fun ScreenContent(modifier: Modifier, context: Context) {
        val authenticationManager = remember { AuthenticationManager(context) }
        Text(
            text = "This is the Home Screen.",
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(100.dp))
        Button(
            onClick = {authenticationManager.signOut()}
        ) { Text("Log uit.") }
    }
}
