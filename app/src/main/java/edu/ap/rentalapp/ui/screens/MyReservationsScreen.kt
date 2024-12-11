package edu.ap.rentalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import edu.ap.rentalapp.entities.ApplianceDTO
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.extensions.RentalService
import edu.ap.rentalapp.extensions.instances.RentalServiceSingleton
import edu.ap.rentalapp.ui.theme.Green
import edu.ap.rentalapp.ui.theme.LightGrey
import edu.ap.rentalapp.ui.theme.Red
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

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
                RentalItem(rental = rental, rentalService,navController = navController)
            }
        }
    }
}

@Composable
fun RentalItem(rental: ApplianceDTO, rentalService: RentalService, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("rental/${rental.id}")
            },
        colors = CardDefaults.cardColors(containerColor = LightGrey.copy(0.1f)),
        shape = RoundedCornerShape(8.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.Gray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = rental.images.firstOrNull(),
                    contentDescription = "Appliance Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = rental.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = rental.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    modifier = Modifier
                        .background(Green, shape = ShapeDefaults.Small)
                        .padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.Bottom)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "${
                        rentalService.calculatePrice(
                            rental.rentalDates[0].startDate.toString(),
                            rental.rentalDates[0].endDate.toString(),
                            rental.pricePerDay
                        )
                    } €",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    modifier = Modifier
                        .background(Green, shape = ShapeDefaults.Small)
                        .padding(2.dp)
                )
            }
        }
    }
}