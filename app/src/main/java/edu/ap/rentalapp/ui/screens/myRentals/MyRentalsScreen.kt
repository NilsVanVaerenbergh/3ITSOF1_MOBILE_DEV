package edu.ap.rentalapp.ui.screens.myRentals

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import edu.ap.rentalapp.components.CategorySelect
import edu.ap.rentalapp.components.filterItemsByCategory
import edu.ap.rentalapp.entities.ApplianceDTO
import edu.ap.rentalapp.ui.theme.Purple40
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun MyRentalsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: MyRentalsViewModel = viewModel()
) {

    var searchText by remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val appliances = viewModel.applianceData.value
    //Log.d("data", "MyRentalsScreen: $appliances")

    var selectedCategory by remember { mutableStateOf("Category") }

    val radiusInKm by remember { mutableDoubleStateOf(100.0) } // Default to 5km
    //val filteredItems = filterItemsByDistance(appliances, 51.216962, 4.399859, radiusInKm)


    Column {

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            OutlinedTextField(
                value = searchText,
                onValueChange = { text ->
                    searchText = text
                },
                placeholder = { Text("Search...") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon"
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            )


            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = modifier
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    CategorySelect(
                        setCategory = { selectedCategory = it }
                    )
                }
//                Spacer(modifier = modifier)
//                Column(
//                    modifier = modifier
//                        .padding(vertical = 10.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//
//                ) {
//                    OutlinedButton(
//                        onClick = { expandedDistance = !expandedDistance },
//                        modifier = modifier
//                    ) {
//                        Text(if (selectedDistance != "Distance") "< $selectedDistance km" else "Distance")
//                    }
//                    DropdownMenu(
//                        expanded = expandedDistance,
//                        onDismissRequest = { expandedDistance = false },
//                        modifier = modifier.fillMaxWidth()
//                    ) {
//                        for (distance in options) {
//                            DropdownMenuItem(
//                                onClick = {
//                                    radiusInKm = distance
//                                    expandedDistance = false
//                                    selectedDistance = distance.toString()
//                                },
//                                text = { Text("Within $distance km") }
//                            )
//                        }
//                        DropdownMenuItem(
//                            onClick = {
//                                selectedDistance = "Distance"
//                                radiusInKm = 100.0
//                                expandedDistance = false
//                            },
//                            text = {
//                                Row {
//                                    Icon(
//                                        imageVector = Icons.Default.Close,
//                                        contentDescription = "Remove"
//                                    )
//                                    Text("Remove filter(s)")
//                                }
//                            }
//                        )
//                    }
//                }
            }

            LazyColumn(
                modifier = modifier
                    .padding(horizontal = 15.dp)
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(filterItemsByCategory(appliances, selectedCategory).filter { appliance ->
                    appliance.name.lowercase().contains(
                        searchText.lowercase()
                    )
                }) { appliance ->
                    ApplianceItemBox(context = context, appliance = appliance, navController = navController)
                }
            }

            OutlinedButton(
                onClick = { navController.navigate("addAppliance") },
                modifier = modifier
                    .padding(15.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Row {
                    Text("Add appliance")
                    Spacer(modifier = modifier)
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add symbol"
                    )
                }
            }
        }
    }


}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val result = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, result)
    return result[0] // Distance in meters
}

fun filterItemsByDistance(
    items: List<MyAppliance>,
    userLat: Double,
    userLon: Double,
    radiusInKm: Double,
): List<MyAppliance> {
    return items.filter { item ->
        val distanceInMeters = calculateDistance(userLat, userLon, item.latitude, item.longitude)
        distanceInMeters <= radiusInKm * 1000 // Convert km to meters
    }
}


suspend fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                //addresses[0].getAddressLine(0) // Full address
                val add = "${addresses[0].postalCode}, ${addresses[0].locality}"
                return@withContext add
            } else {
                "Address not found"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error fetching address"
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ApplianceItemBox(appliance: ApplianceDTO, context: Context, navController: NavHostController) {
    val distance =
        calculateDistance(51.216962, 4.399859, appliance.latitude, appliance.longitude) / 1000
    var address by remember { mutableStateOf("loading...") }

    LaunchedEffect(appliance) {
        address = getAddressFromLatLng(context, appliance.latitude, appliance.longitude).toString()

    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray, shape = ShapeDefaults.Small)
            //.border(BorderStroke(1.dp, Color.Black), shape = ShapeDefaults.Small)
            .padding(8.dp).clickable {
                navController.navigate("rental/${appliance.id}")
            },


    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image on the left
            val imageUrl = appliance.images.firstOrNull() // Get the first image or null
            if (imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Appliance Image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .size(80.dp) // Adjust size as needed
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(BorderStroke(1.dp, Color.Black))

                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Gray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No Image", color = Color.White, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp)) // Space between image and text

            // Text content
            Row {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = appliance.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = appliance.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        modifier = Modifier
                            .background(Purple40, shape = ShapeDefaults.Small)
                            .padding(2.dp)
                    )
                }
                Column {
                    Text("${String.format(" % .2f", distance)} km")
                    Text(address)
                }
            }
        }
    }
}

@Preview
@Composable
fun Show(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    MyRentalsScreen(
        navController = navController,

        )

}