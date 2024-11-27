package edu.ap.rentalapp.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.ap.rentalapp.R
import edu.ap.rentalapp.entities.User
import edu.ap.rentalapp.middleware.AuthActivity


class EditUserNameActivity : AuthActivity() {
    override fun getTopBarTitle(): String = "Wijzig naam"
    @Composable
    override fun ScreenContent(modifier: Modifier, context: Context) {
        var userData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("user_data", User::class.java)
        } else {
            intent.getParcelableExtra<User>("user_data")
        }
        var username by remember { mutableStateOf(userData?.username ?: "") }
        Column(modifier = modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { u -> username = u },
                placeholder = { Text(text= userData?.username.toString())}
            )
            Button(
                onClick = {
                        if(username.isEmpty()) {
                            Toast.makeText(context, context.getString(R.string.error_empty_input), Toast.LENGTH_LONG)
                        } else {
                            userData?.username = username
                            Log.d("USER", userData.toString())
                            Toast.makeText(context, "naam aangepast!", Toast.LENGTH_LONG)
                        }
                },
            ) {
                Text(text = "Opslaan")
            }
            Button(
                onClick = {
                    backToProfile()
                }
            ) {
                Text(text = "Annuleren")
            }
        }
    }
    fun backToProfile() {
        val intent = Intent(this, UserProfileActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }
}
