package edu.ap.rentalapp.ui.screens.rentals

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import edu.ap.rentalapp.components.CategorySelect
import edu.ap.rentalapp.components.OSM
import edu.ap.rentalapp.components.filterItemsByCategory
import edu.ap.rentalapp.components.getAddressFromLatLng
import edu.ap.rentalapp.components.getCurrentLocation
import edu.ap.rentalapp.components.saveUserLocationToFirebase
import edu.ap.rentalapp.entities.ApplianceDTO
import edu.ap.rentalapp.entities.User
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.extensions.RentalService
import edu.ap.rentalapp.extensions.instances.RentalServiceSingleton
import edu.ap.rentalapp.extensions.instances.UserServiceSingleton
import edu.ap.rentalapp.ui.theme.Green
import edu.ap.rentalapp.ui.theme.LightGrey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RentalOverViewScreen(modifier: Modifier = Modifier, navController: NavHostController, permissionState: PermissionState) {

    val context = LocalContext.current

    val authenticationManager = remember { AuthenticationManager(context) }
    val userService = remember { UserServiceSingleton.getInstance(context) }
    val user = authenticationManager.getCurrentUser()
    val userId = user?.uid.toString()
    var userData by remember { mutableStateOf<User?>(null) }

    var address by remember { mutableStateOf("") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var radiusInKm by remember { mutableDoubleStateOf(0.0) }
    var maxRadius by remember { mutableDoubleStateOf(30.0) }


    val rentalService = RentalServiceSingleton.getInstance(context)
    val rentalList = remember { mutableStateOf<List<ApplianceDTO>>(emptyList()) }
    val loading = remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("Category") }
    var searchText by remember { mutableStateOf("") }

    var hasAskedForPermission by rememberSaveable { mutableStateOf(false) }
    var userLocation by remember { mutableStateOf<Location?>(null) }

    val filteredAppliances = remember(radiusInKm, selectedCategory, searchText) {
        filterAppliances(
            rentalList.value,
            userData,
            radiusInKm,
            selectedCategory,
            searchText
        )
    }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        if (!hasAskedForPermission) {
            hasAskedForPermission = true
            permissionState.launchPermissionRequest()
        }
    }
    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted && userLocation == null) {
            getCurrentLocation(context) { location ->
                if (location != null) {
                    saveUserLocationToFirebase(context, location.latitude, location.longitude)
                    userLocation = location
                }
            }
        }
    }

    LaunchedEffect(user) {
        if (user != null) {
            userService.getUserByUserId(userId).onEach { result ->
                if (result.isSuccess) {
                    val document = result.getOrNull()
                    if (document != null && document.exists()) {
                        userData = document.toObject(User::class.java)
                        latitude = userData?.lat?.toDouble() ?: 0.0
                        longitude = userData?.lon?.toDouble() ?: 0.0
                        address = getAddressFromLatLng(context, latitude, longitude).toString()
                    }
                }
            }.launchIn(coroutineScope)
        }
        coroutineScope.launch {
            fetchRentals(userId, rentalService, rentalList, loading)
        }
    }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = loading.value)
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = modifier
                .aspectRatio(2f)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.LightGray)
                .fillMaxWidth()

        ) {
            OSM(
                context = context,
                latitude = latitude,
                longitude = longitude,
                radius = radiusInKm,
                appliances = filteredAppliances,
                modifier = modifier
            )
        }
        Spacer(modifier = modifier.padding(vertical = 4.dp))

        RadiusSlider(
            isLoading = loading,
            radius = radiusInKm,
            maxRadius = maxRadius.toFloat(),
            onRadiusChange = { radiusInKm = it },
            onMaxRadiusChange = { maxRadius = it }
        )

        Row(
            modifier = modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { text -> searchText = text },
                placeholder = {
                    Text(
                        "Search...",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                shape = RoundedCornerShape(99.dp),
                modifier = modifier.padding(horizontal = 8.dp, vertical = 3.dp).height(46.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,          // No border when focused
                    unfocusedIndicatorColor = Color.Transparent,        // No border when unfocused
                    disabledIndicatorColor = Color.Transparent,         // No border when disabled
                    focusedContainerColor = LightGrey.copy(0.2f), // Background color when focused
                    unfocusedContainerColor = LightGrey.copy(0.2f), // Background color when unfocused
                    cursorColor = MaterialTheme.colorScheme.primary                              // Cursor color
                ),
            )
            Column {
                CategorySelect(
                    setCategory = { selectedCategory = it },
                    enabled = !loading.value
                )
            }
        }

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                coroutineScope.launch {
                    fetchRentals(userId, rentalService, rentalList, loading)
                }
            }
        ) {
            Column(modifier = modifier.fillMaxWidth()) {
                if (rentalList.value.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth() // Ensures the Column takes up the full width
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center, // Centers content vertically
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentVeryDissatisfied,
                            contentDescription = "Sad",
                            tint = Color.Gray.copy(0.6f)
                        )
                        Text(
                            text = "No rentals available",
                            modifier = modifier
                                .padding(20.dp)
                                .align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray.copy(0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (filteredAppliances.isNotEmpty()) {
                            items(filteredAppliances) { appliance ->
                                CustomCard(GeoPoint(
                                    userData!!.lat.toDouble(),
                                    userData!!.lon.toDouble()
                                ),appliance, navController)
                            }
                        } else {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth() // Ensures the Column takes up the full width
                                        .fillParentMaxHeight(),
                                    verticalArrangement = Arrangement.Center, // Centers content vertically
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SentimentVeryDissatisfied,
                                        contentDescription = "Sad",
                                        tint = Color.Gray.copy(0.6f)
                                    )
                                    Text(
                                        text = "Nothing found",
                                        modifier = modifier
                                            .padding(20.dp)
                                            .align(Alignment.CenterHorizontally),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray.copy(0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun filterAppliances(
    appliances: List<ApplianceDTO>,
    userData: User?,
    radiusInKm: Double,
    category: String,
    searchText: String
): List<ApplianceDTO> {

    val filteredByCategory = filterItemsByCategory(appliances, category)

    val filteredByText = filteredByCategory.filter { appliance ->
        appliance.name.lowercase().contains(searchText.lowercase())
    }

    return filteredByText.filter { appliance ->
        Log.d("location", "filterAppliancesByRadius: ${appliance.name}")
        calculateDistance(
            userData!!.lat.toDouble(),
            userData.lon.toDouble(),
            appliance.latitude,
            appliance.longitude
        ) <= radiusInKm * 1000
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun RadiusSlider(
    isLoading: MutableState<Boolean>,
    radius: Double,
    onRadiusChange: (Double) -> Unit,
    minRadius: Float = 0f,
    maxRadius: Float,
    stepSize: Float = 0.5f,
    onMaxRadiusChange: (Double) -> Unit
) {

    var showDialog by remember { mutableStateOf(false) }

    // Function to round to nearest step size (0.5 km)
    fun roundToStep(value: Float, stepSize: Float): Float {
        return (value / stepSize).roundToInt() * stepSize
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.Center
    ) {
        // Dynamic radius display
        Text(
            text = "Radius: ${String.format(" % .1f", radius)} km",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary
            //modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Minimum label
            Text(
                text = "${minRadius.toInt()} km",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 8.dp)
            )

            // Slider
            Slider(
                enabled = !isLoading.value,
                value = radius.toFloat(),
                onValueChange = {
                    val roundedValue = roundToStep(it, stepSize)
                    onRadiusChange(roundedValue.toDouble())
                },
                valueRange = minRadius..maxRadius,
                thumb = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location icon",
                        tint = Green,
                    )
                },
                colors = SliderDefaults.colors(activeTrackColor = Green),
                steps = ((maxRadius - minRadius) / stepSize).toInt() - 1,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${maxRadius.toInt()} km",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { showDialog = true }
            )

            if (showDialog) {
                BasicAlertDialog(
                    onDismissRequest = { showDialog = false },
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(BorderStroke(2.dp, Color.Black))
                ) {
                    var newMaxRadius by remember { mutableStateOf(maxRadius.toString()) }

                    Column {
                        TextField(
                            value = newMaxRadius,
                            onValueChange = { newMaxRadius = it },
                            label = { Text("Enter new max radius (km)") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )

                        Row {
                            TextButton(onClick = {
                                val newRadius = newMaxRadius.toDouble()
                                if (newRadius > 0) {
                                    onMaxRadiusChange(newRadius)
                                }
                                showDialog = false
                            }) {
                                Text("OK")
                            }
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Calculates distance between 2 locations (in meters)
 * @param lat1 latitude of first location (Double)
 * @param lon1 longitude of first location (Double)
 * @param lat2 latitude of second location (Double)
 * @param lon2 longitude of second location (Double)
 * @return Returns Float in meters
 */
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val result = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, result)
    Log.d("location", "calculateDistance: $lat1:$lon1 <-> $lat2:$lon2")
    return result[0] // Distance in meters
}

// Function to fetch rentals from the service and update state
suspend fun fetchRentals(
    userId: String,
    rentalService: RentalService,
    rentalList: MutableState<List<ApplianceDTO>>,
    loading: MutableState<Boolean>
) {
    loading.value = true
    try {
        val rentals = rentalService.getListOfRentalsWithDates()
        rentalList.value = rentals.filter { a -> a.rentalDates.isEmpty() && a.userId != userId }
    } catch (e: Exception) {
        rentalList.value = emptyList()
    } finally {
        loading.value = false
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CustomCard(homeLocation: GeoPoint, appliance: ApplianceDTO, navController: NavHostController) {

    val distance = calculateDistance(
        homeLocation.latitude,
        homeLocation.longitude,
        appliance.latitude,
        appliance.longitude
    ) / 1000
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("rental/${appliance.id}")
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = LightGrey.copy(0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth().padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.Gray, RoundedCornerShape(8.dp)).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = appliance.images.firstOrNull(),
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
                    .weight(1f)
            ) {
                Text(
                    text = appliance.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = appliance.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontFamily = FontFamily.SansSerif),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.Bottom)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "~${String.format(" % .2f", distance)} km",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "€${appliance.pricePerDay}/day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}