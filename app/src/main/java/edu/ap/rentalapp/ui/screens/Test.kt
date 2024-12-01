package edu.ap.rentalapp.ui.screens

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.ap.rentalapp.middleware.AuthActivity


class Test: AuthActivity() {
    var x = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            x = "test plks"
        }
    }
    override fun getTopBarTitle(): String{
        return "test"
    }

    companion object{
        @Composable
        fun TestScreen(modifier: Modifier = Modifier) {
            Text("Hello there",
                modifier = modifier.padding(vertical = 100.dp))

        }
    }
}