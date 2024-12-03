package edu.ap.rentalapp.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Iron
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun SharedBottomBar(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    BottomAppBar {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = {
                navController.navigate("home")
            }) {
                Icon(imageVector = Icons.Outlined.Home, contentDescription = "Home")
            }
            VerticalDivider()
            IconButton(onClick = {
                navController.navigate("myReservations")
            }) {
                Icon(imageVector = Icons.Outlined.BookmarkBorder, contentDescription = "Reservations")
            }
            VerticalDivider()
            IconButton(onClick = {
                navController.navigate("myRentals")
            }) {
                Icon(imageVector = Icons.Outlined.Iron, contentDescription = "Rentals")
            }
        }

    }

}

@Preview
@Composable
fun Show() {
    val navController = rememberNavController()
    SharedBottomBar(navController = navController)
}