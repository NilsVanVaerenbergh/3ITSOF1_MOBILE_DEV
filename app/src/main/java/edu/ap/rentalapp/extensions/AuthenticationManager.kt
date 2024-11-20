package edu.ap.rentalapp.extensions

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.Auth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import edu.ap.rentalapp.MainActivity
import edu.ap.rentalapp.R
import edu.ap.rentalapp.extensions.instances.UserServiceSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface AuthResponse {
    data object Succes: AuthResponse
    data class Error(val message: String): AuthResponse
}

class AuthenticationManager(private val context: Context) {
    public val auth = Firebase.auth
    fun signUpWithEmail(inEmail: String, inPassword : String): Flow<AuthResponse> = callbackFlow {
        auth.createUserWithEmailAndPassword(inEmail, inPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val userService = UserServiceSingleton.getInstance(context)
                    val username = inEmail.substringBefore("@")
                    userService.saveUserData(userId = userId, email = inEmail, username = username, location = null)
                        .onEach { result ->
                            result.onSuccess {
                                trySend(AuthResponse.Succes)
                            }.onFailure { exception ->
                                trySend(
                                    AuthResponse.Error(
                                        message = exception.message ?: context.getString(R.string.error_global)
                                    )
                                )
                            }
                        }
                        .launchIn(CoroutineScope(coroutineContext))
                } else {
                    trySend(
                        AuthResponse.Error(
                            message = context.getString(R.string.error_global)
                        )
                    )
                }
            } else {
                trySend(
                    AuthResponse.Error(
                        message = task.exception?.message ?: context.getString(R.string.error_global)
                    )
                )
            }
        }
        awaitClose()
    }
    fun signInWithEmail(inEmail: String, inPassword : String): Flow<AuthResponse> = callbackFlow {
        auth.signInWithEmailAndPassword(inEmail, inPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(AuthResponse.Succes)
            } else {
                trySend(
                    AuthResponse.Error(
                        message = task.exception?.message ?: context.getString(R.string.error_global)
                    )
                )
            }
        }
        awaitClose()
    }

    fun signOut() {
        auth.signOut()
        this.context.startActivity(Intent(this.context, MainActivity::class.java))
    }
}

