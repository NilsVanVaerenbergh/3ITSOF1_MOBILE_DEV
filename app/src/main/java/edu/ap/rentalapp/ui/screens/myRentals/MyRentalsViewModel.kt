package edu.ap.rentalapp.ui.screens.myRentals

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class MyAppliance(
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val images: List<String> = emptyList(),
    val address: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val userId: String = ""
)

class MyRentalsViewModel : ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth

    val state = mutableStateOf(emptyList<MyAppliance>())
    val userId: String = ""
//    private val _appliances = MutableStateFlow<List<MyAppliance>>(emptyList())
//    val appliances: StateFlow<List<MyAppliance>> = _appliances

    //    fun getDataForUser(userId: String){
//        viewModelScope.launch {
//            _appliances.value = fetchAppliances(userId)
//        }
//    }
//        val context = LocalContext.current
//        val authenticationManager = remember { AuthenticationManager(context) }
//        val user = authenticationManager.auth.currentUser
//        val userId = user?.uid.toString()



    private fun getData() {
        val user = auth.currentUser?.uid.toString()
        Log.d("user", "getData: $user")
        viewModelScope.launch {
            state.value = fetchAppliances(user)
        }
    }

    init {
        getData()
    }


}

suspend fun fetchAppliances(userId: String): List<MyAppliance> {
    val firestore = FirebaseFirestore.getInstance()
    val appliances = emptyList<MyAppliance>().toMutableList()

    try {
        firestore.collection("myAppliances")
            .whereEqualTo("userId", userId)
            .get().await().map { app ->
                val result = app.toObject(MyAppliance::class.java)
                appliances += result
            }
    } catch (e: FirebaseFirestoreException) {
        Log.d("data", "fetchAppliances: $e")
    }

    return appliances
}
