package edu.ap.rentalapp.ui.screens.rentals

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import edu.ap.rentalapp.entities.ApplianceDTO
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.extensions.instances.RentalServiceSingleton
import edu.ap.rentalapp.extensions.instances.UserServiceSingleton
import edu.ap.rentalapp.ui.theme.Green
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ApplianceScreen(modifier: Modifier = Modifier, navController: NavHostController, id: String) {

    val context = LocalContext.current

    val rentalService = RentalServiceSingleton.getInstance(context)
    val userService = UserServiceSingleton.getInstance(context)

    val authenticationManager = remember { AuthenticationManager(context) }
    val user = authenticationManager.auth.currentUser
    val pagerState = rememberPagerState()

    val coroutineScope = rememberCoroutineScope()

    var appliance by remember { mutableStateOf<ApplianceDTO?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(id) {
        coroutineScope.launch {
            try {
                isLoading = true
                appliance = rentalService.getRentalAndDatesById(id)
            } catch (e: Exception) {
                navController.navigate("rentalsOverview")
                appliance = null
            } finally {
                isLoading = false
            }
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (appliance == null) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Failed to load appliance details.")
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            HorizontalPager(
                count = appliance!!.images.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) { page ->
                AsyncImage(
                    model = appliance!!.images[page],
                    contentDescription = "Appliance Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )
            }
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                activeColor = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = appliance!!.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Category: ${appliance!!.category}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Description:",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = appliance!!.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Price:",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = appliance!!.pricePerDay.toString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Address:",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = appliance!!.address,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // My rentals
            if (user!!.uid == appliance!!.userId) {
                if (appliance!!.rentalDates.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        var renterName by remember { mutableStateOf("") }
                        LaunchedEffect(Unit) {
                            renterName =
                                userService.getUserByIdSuspended(appliance!!.rentalDates[0].rentedByUserId)?.username
                                    ?: ""
                        }

                        Icon(
                            Icons.Default.Person,
                            "Profile Icon",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(Modifier.padding(1.dp))
                        Text(
                            text = renterName.ifEmpty { "Unknown" },
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                        )
                    }
                    Text(
                        text = "Rented:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.padding( top = 8.dp , bottom = 16.dp)
                    ) {
                        Text(
                            text = "${
                                SimpleDateFormat(
                                    "dd MMM yyyy",
                                    Locale.getDefault()
                                ).format(appliance!!.rentalDates[0].startDate)
                            } - ",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                        )

                        Text(
                            text = SimpleDateFormat(
                                "dd MMM yyyy",
                                Locale.getDefault()
                            ).format(appliance!!.rentalDates[0].endDate),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                        )

                    }
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green,
                        contentColor = Color.White
                    ),
                    onClick = {
                        if (appliance!!.rentalDates.isEmpty()) {
                            rentalService.deleteAppliance(appliance!!.id).onEach { response ->
                                if (response.isSuccess) {
                                    Toast.makeText(
                                        context,
                                        "Succesfully deleted",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    navController.navigate("myRentals")
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to delete. Try again later",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }.launchIn(coroutineScope)
                        } else {
                            Toast.makeText(
                                context,
                                "You cannot delete this appliance as someone is renting it",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) { Text(text = "Delete appliance") }
            }
            // My reservations
            else if (appliance!!.rentalDates.isNotEmpty() && appliance!!.rentalDates[0].rentedByUserId == user.uid) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    var ownerName by remember { mutableStateOf("") }
                    LaunchedEffect(Unit) {
                        ownerName =
                            userService.getUserByIdSuspended(appliance!!.userId)?.username ?: ""
                    }

                    Icon(
                        Icons.Default.Person,
                        "Profile Icon",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.padding(1.dp))
                    Text(
                        text = ownerName.ifEmpty { "Unknown" },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                    )
                }

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green,
                        contentColor = Color.White
                    ),
                    onClick = {
                        Toast.makeText(
                            context,
                            appliance!!.rentalDates[0].Id,
                            Toast.LENGTH_SHORT
                        ).show()
                        rentalService.deleteRentalDate(appliance!!.rentalDates[0].Id)
                            .onEach { response ->
                                if (response.isSuccess) {
                                    Toast.makeText(
                                        context,
                                        "Succesfully cancelled renting",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    navController.navigate("myReservations")
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to delete. Try again later",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }.launchIn(coroutineScope)
                    }
                ) { Text(text = "Cancel renting") }
            }
            // Rent appliance
            else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    var ownerName by remember { mutableStateOf("") }
                    LaunchedEffect(Unit) {
                        ownerName =
                            userService.getUserByIdSuspended(appliance!!.userId)?.username ?: ""
                    }

                    Icon(
                        Icons.Default.Person,
                        "Profile Icon",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.padding(1.dp))
                    Text(
                        text = ownerName.ifEmpty { "Unknown" },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                    )
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green,
                        contentColor = Color.White
                    ),
                    onClick = {
                        navController.navigate("rental/${id}/rent")
                    }
                ) {
                    Text(text = "Choose dates")
                }
            }
        }
    }
}

