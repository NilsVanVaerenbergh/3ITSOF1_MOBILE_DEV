package edu.ap.rentalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import edu.ap.rentalapp.entities.ApplianceDTO
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.extensions.instances.RentalServiceSingleton
import kotlinx.coroutines.launch

@Composable
fun MyReservationsScreen(modifier: Modifier = Modifier) {
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
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No reservations found")
        }
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(rentalsState.value) { rental ->
                RentalItem(rental = rental)
            }
        }
    }
}

@Composable
fun RentalItem(rental: ApplianceDTO) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
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