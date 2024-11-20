package edu.ap.rentalapp.screens

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.ap.rentalapp.R
import edu.ap.rentalapp.middleware.AuthActivity

class EditLocationActivity : AuthActivity() {
    override fun getTopBarTitle(): String = "Wijzig locatie"

    @Composable
    override fun ScreenContent(modifier: Modifier, context: Context) {}
}