package edu.ap.rentalapp.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.rentalapp.R
import edu.ap.rentalapp.entities.User
import edu.ap.rentalapp.ui.theme.Green
import edu.ap.rentalapp.ui.theme.LightGrey

@SuppressLint("RestrictedApi")
@Composable
fun EditUserNameScreen(
    modifier: Modifier = Modifier,
    context: Context,
    navController: NavController,
    user: User
) {

    var username by remember { mutableStateOf(user.username) }
    val userId = user.userId

    Column(modifier = modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text(text = user.username) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Search icon",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            shape = RoundedCornerShape(99.dp),
            modifier = modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,          // No border when focused
                unfocusedIndicatorColor = Color.Transparent,        // No border when unfocused
                disabledIndicatorColor = Color.Transparent,         // No border when disabled
                focusedContainerColor = LightGrey.copy(0.2f), // Background color when focused
                unfocusedContainerColor = LightGrey.copy(0.2f), // Background color when unfocused
                cursorColor = MaterialTheme.colorScheme.primary                              // Cursor color
            ),
        )
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Green,
                contentColor = Color.White
            ),
            onClick = {
                if (username.isBlank()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_empty_input),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    updateUsername(userId, username)
                    Toast.makeText(context, "Username changed successfully!", Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                }
            },
        ) {
            Text(text = "Save")
        }
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Green,
                contentColor = Color.White
            ),
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text(text = "Cancel")
        }
    }
}

fun updateUsername(userId: String, newName: String) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(userId)

    userRef.update(
        mapOf("username" to newName)
    ).addOnSuccessListener {
        Log.d("UpdateUsername", "Username updated successfully!")
    }.addOnFailureListener { e ->
        Log.e("UpdateUsername", "Error updating username", e)
    }
}