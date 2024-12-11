package edu.ap.rentalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import edu.ap.rentalapp.entities.ApplianceDTO
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.extensions.instances.RentalServiceSingleton
import kotlinx.coroutines.launch

@Composable
fun MyReservationsScreen(modifier: Modifier = Modifier,
                         navController: NavHostController) {
    val rentalsState = remember { mutableStateOf<List<ApplianceDTO>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val rentalService = RentalServiceSingleton.getInstance(context)

    val authenticationManager = remember { AuthenticationManager(context) }
    val user = authenticationManager.auth.currentUser
    val userId = user?.uid ?: ""
    LaunchedEffect(userId) {
        coroutineScope.launch {
            val rentals = rentalService.getRentalsByUserId(userId)
            rentalsState.value = rentals
        }
    }

    if (rentalsState.value.isEmpty()) {
        Column(modifier = Modifier
            .fillMaxWidth() // Ensures the Column takes up the full width
            .fillMaxHeight(),verticalArrangement = Arrangement.Center, // Centers content vertically
            horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.SentimentVeryDissatisfied, contentDescription = "Sad", tint = Color.Gray.copy(0.6f))
            Text(
                text = "No reservations found",
                modifier = modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray.copy(0.6f)
            )
        }
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(rentalsState.value) { rental ->
                RentalItem(rental = rental, navController = navController)
            }
        }
    }
}

@Composable
fun RentalItem(rental: ApplianceDTO, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .padding(16.dp).clickable {
                navController.navigate("rental/${rental.id}")
            }
    ) {
        Text(text = "Appliance: ${rental.name ?: "Unknown"}", style = MaterialTheme.typography.titleMedium)
        Text(text = "Rental Dates:", style = MaterialTheme.typography.titleSmall)
        rental.rentalDates.forEach { date ->
            Text(
                text = " - From: ${date.startDate} To: ${date.endDate}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}