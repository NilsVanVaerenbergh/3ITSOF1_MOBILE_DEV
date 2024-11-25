package edu.ap.rentalapp.screens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import edu.ap.rentalapp.middleware.AuthActivity

class EditLocationActivity : AuthActivity() {
    override fun getTopBarTitle(): String = "Wijzig locatie"

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun ScreenContent(modifier: Modifier, context: Context) {
    }
}