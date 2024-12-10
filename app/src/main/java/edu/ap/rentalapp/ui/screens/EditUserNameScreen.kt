package edu.ap.rentalapp.ui.screens

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.rentalapp.R
import edu.ap.rentalapp.entities.User

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
            placeholder = { Text(text = user.username) }
        )
        Button(
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