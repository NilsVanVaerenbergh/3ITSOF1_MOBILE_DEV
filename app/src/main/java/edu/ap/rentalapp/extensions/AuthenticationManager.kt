package edu.ap.rentalapp.extensions

import com.google.android.gms.auth.api.Auth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface AuthResponse {
    data object Succes: AuthResponse
    data class Error(val message: String): AuthResponse
}

class AuthenticationManager {

    private val auth = Firebase.auth
    fun signUpWithEmail(inEmail: String, inPassword : String): Flow<AuthResponse> = callbackFlow {
        auth.createUserWithEmailAndPassword(inEmail, inPassword).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                trySend(AuthResponse.Succes)
            } else {
                trySend(AuthResponse.Error(message = task.exception?.message ?: "Er ging iets verkeerd"))
            }
        }
    }
    fun signInWithEmail(inEmail: String, inPassword : String): Flow<AuthResponse> = callbackFlow {
        auth.signInWithEmailAndPassword(inEmail, inPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(AuthResponse.Succes)
            } else {
                trySend(
                    AuthResponse.Error(
                        message = task.exception?.message ?: "Er ging iets verkeerd"
                    )
                )
            }
        }
    }
}

