package edu.ap.rentalapp.ui.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import com.google.firebase.auth.FirebaseAuth
import edu.ap.rentalapp.ui.theme.DarkGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedTopAppBar(title: String, navController: NavController) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components { add(SvgDecoder.Factory()) } // Add the SVG decoder
        .build()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val noShowBackArrowScreens = listOf("home", "myReservations", "myRentals", "profile", "rental/{id}")
    val showBackArrow =
        navController.currentBackStackEntryAsState().value?.destination?.route !in noShowBackArrowScreens


    TopAppBar(
        title = { Text(text = title, color = Color.White) },
        navigationIcon = {
            if (showBackArrow) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        },
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
            containerColor = DarkGrey
        )
    )
}