//package edu.ap.rentalapp.presentation.sign_in
//
//import android.content.Context
//import android.content.IntentSender
//import com.google.android.gms.auth.api.identity.BeginSignInRequest
//import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
//import com.google.android.gms.auth.api.identity.SignInClient
//import com.google.firebase.Firebase
//import com.google.firebase.auth.auth
//
//class GoogleAuthUiClient(private val context: Context, private  val oneTapClient: SignInClient) {
//    private val auth = Firebase.auth
//
//    suspend fun signIn(): IntentSender? {
//        val result = try {
//            oneTapClient.beginSignIn()
//        } catch (e: Exception) {
//
//        }
//    }
//    private fun buildSignInRequest(): BeginSignInRequest {
//        return BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
//            GoogleIdTokenRequestOptions.builder()
//                .setSupported(true)
//                .setFilterByAuthorizedAccounts(false)
//                .setServerClientId(context.getString(R.string.web_client_id))
//                .setAutoSelectEnabled()
//        )
//    }
//    suspend fun  SignOut() {
//
//    }
//
//
//}