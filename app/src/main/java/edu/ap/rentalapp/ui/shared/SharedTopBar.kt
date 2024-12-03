package edu.ap.rentalapp.ui.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import com.google.firebase.auth.FirebaseAuth
import edu.ap.rentalapp.ui.theme.Purple40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedTopAppBar(title: String, navController: NavController) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components { add(SvgDecoder.Factory()) } // Add the SVG decoder
        .build()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
                    .clickable { navController.navigate("profile") }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Purple40
        )
    )
}