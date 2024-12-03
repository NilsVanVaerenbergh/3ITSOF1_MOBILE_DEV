package edu.ap.rentalapp.middleware

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.ap.rentalapp.extensions.instances.UserServiceSingleton.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException


object PreferencesKeys {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
}

class AuthViewModel(context: Context) : ViewModel() {

    //private val Context.dataStore by preferencesDataStore(name = "user_preferences")

    private val dataStore = context.applicationContext.dataStore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    private val loggedInKey = booleanPreferencesKey("is_logged_in")
    var isLoggedIn = mutableStateOf(false)
        private set

    // State to observe login status
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    // Returns the current authenticated user
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // Handles login state changes
    init {
        viewModelScope.launch {
            dataStore.data
                .catch { exception ->
                    // Handle errors, such as IOExceptions
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences -> preferences[loggedInKey] ?: false }
                .collect { isLoggedIn ->
                    _isAuthenticated.value = isLoggedIn
                }

        }

        fun signIn() {
            isLoggedIn.value = true
        }
        // Sign out the user
        fun signOut() {
            isLoggedIn.value = false
            auth.signOut()
        }

        fun setLoggedIn(isLoggedIn: Boolean) {
            viewModelScope.launch {
                dataStore.edit { preferences ->
                    preferences[loggedInKey] = isLoggedIn
                }
            }
        }

    }
}