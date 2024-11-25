package edu.ap.rentalapp.middleware

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder

import com.google.firebase.auth.FirebaseAuth
import edu.ap.rentalapp.R
import edu.ap.rentalapp.screens.SignInActivity
import edu.ap.rentalapp.screens.UserProfileActivity

abstract class AuthActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        checkAuthentication()

        setContent {
            Scaffold(
                topBar = {
                    SharedTopAppBar(
                        title = getTopBarTitle(),
                        onProfileClick = { onProfileIconClicked() }
                    )
                }
            ) { paddingValues ->
                ScreenContent(Modifier.padding(paddingValues), LocalContext.current)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SharedTopAppBar(title: String, onProfileClick: () -> Unit) {
        val context = LocalContext.current
        val imageLoader = ImageLoader.Builder(context)
            .components { add(SvgDecoder.Factory()) } // Add the SVG decoder
            .build()
        TopAppBar(
            title = { Text(text = title, color = Color.Black) },
            actions = {
                AsyncImage(
                    model = "https://api.dicebear.com/9.x/dylan/svg?seed=${auth.currentUser?.email}",
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onProfileClick() }
                )
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.White
            )
        )
    }
    abstract fun getTopBarTitle(): String

    @Composable
    open fun ScreenContent(modifier: Modifier, context: Context) {
        Text(text = context.getString(R.string.error_missing_activity))
    }
    open fun onProfileIconClicked() {
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
    }
    private fun checkAuthentication() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val signInIntent = Intent(this, SignInActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(signInIntent)
            finish()
        }
    }
}