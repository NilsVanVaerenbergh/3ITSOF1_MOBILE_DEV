package edu.ap.rentalapp.ui.screens.rentals

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import edu.ap.rentalapp.components.OSM
import edu.ap.rentalapp.entities.ApplianceDTO
import edu.ap.rentalapp.entities.ApplianceRentalDate
import edu.ap.rentalapp.entities.User
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.extensions.instances.RentalServiceSingleton
import edu.ap.rentalapp.extensions.instances.UserServiceSingleton
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun RentApplianceScreen(modifier: Modifier = Modifier, navController: NavHostController, id: String) {
    val dateFormat = SimpleDateFormat("d/MM/yyyy", Locale.getDefault())
    val paddingInBetween = 10.dp
    val context = LocalContext.current

    val rentalService = RentalServiceSingleton.getInstance(context)
    val userService = UserServiceSingleton.getInstance(context)


    val authenticationManager = remember { AuthenticationManager(context) }
    val user = authenticationManager.auth.currentUser

    val coroutineScope = rememberCoroutineScope()

    var appliance by remember { mutableStateOf<ApplianceDTO?>(null) }
    var userData by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var startDate by remember { mutableStateOf<String?>(null) }
    var endDate by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(id) {
        coroutineScope.launch {
            try {
                isLoading = true
                appliance = rentalService.getRentalAndDatesById(id)
                userData = userService.getUserByIdSuspended(user!!.uid)
            } catch (e: Exception) {
                navController.navigate("rentalsOverview")
                appliance = null
                userData = null
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (appliance == null || userData == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Failed to load appliance details.")
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Box(
                modifier = modifier
                    .aspectRatio(2f)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray)
                    .fillMaxWidth()

            ) {
                OSM(
                    context = context,
                    latitude = userData!!.lat.toDouble(),
                    longitude = userData!!.lon.toDouble(),
                    appliances = listOf(appliance!!) ,
                    modifier = modifier
                )
            }
            Text(
                text = appliance!!.name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(paddingInBetween))
            Text(
                text = appliance!!.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = paddingInBetween)
            )
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Select Start Date: ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = startDate ?: "Not selected",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable {
                            openDatePickerDialog(context, appliance!!.rentalDates) { date ->
                                startDate = date
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(paddingInBetween))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Select End Date: ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = endDate ?: "Not selected",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable {
                            openDatePickerDialog(context, appliance!!.rentalDates) { date ->
                                endDate = date
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(paddingInBetween))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Current total price:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${rentalService.calculatePrice(startDate, endDate, appliance!!.pricePerDay)} €",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Button(

                onClick = {
                    try {
                        if (startDate != null && endDate != null) {
                            val startDate = dateFormat.parse(startDate)
                            val endDate = dateFormat.parse(endDate)
                            coroutineScope.launch {
                                val userId = user?.uid ?: ""
                                val success = rentalService.addRentalDateToAppliance(id, startDate,  endDate, userId)
                                if (success) {
                                    navController.navigate("myReservations");
                                } else {
                                    Toast.makeText(context, "Failed to save rental dates", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error parsing dates: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Rental Dates")
            }
        }

    }
}

fun openDatePickerDialog(
    context: Context,
    applianceRentalDates: List<ApplianceRentalDate>,
    onDateSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    val today = Calendar.getInstance()

    val minDate = today.timeInMillis

    val rentalDateRanges = applianceRentalDates.map { rentalDate ->
        val startDate = rentalDate.startDate
        val endDate = rentalDate.endDate
        Pair(startDate, endDate)
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            val selectedDate = selectedCalendar.time
            val isDateAvailable = rentalDateRanges.none { (startDate, endDate) ->
                selectedDate.after(startDate) && selectedDate.before(endDate)
            }
            if (isDateAvailable) {
                val formattedDate = "$dayOfMonth/${month + 1}/$year"
                onDateSelected(formattedDate)
            } else {
                Toast.makeText(context, "This date is unavailable. Please select another date.", Toast.LENGTH_SHORT).show()
            }
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.minDate = minDate
    datePickerDialog.show()
}