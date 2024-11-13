package edu.ap.rentalapp.middleware

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

import com.google.firebase.auth.FirebaseAuth
import edu.ap.rentalapp.screens.SignInActivity

abstract class AuthActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        checkAuthentication()
    }
    private fun checkAuthentication() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val signInIntent = Intent(this, SignInActivity::class.java)
            signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(signInIntent)
            finish()
        }
    }
}