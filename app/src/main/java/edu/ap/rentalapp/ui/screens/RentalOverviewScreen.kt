package edu.ap.rentalapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import edu.ap.rentalapp.entities.Appliance
import edu.ap.rentalapp.extensions.RentalService
import edu.ap.rentalapp.extensions.instances.RentalServiceSingleton
import kotlinx.coroutines.launch

@Composable
fun rentalOverViewScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val rentalService = RentalServiceSingleton.getInstance(context)
    val rentalList = remember { mutableStateOf<List<Appliance>>(emptyList()) }
    val loading = remember { mutableStateOf(true) }

    // Get the CoroutineScope for launching coroutines
    val coroutineScope = rememberCoroutineScope()

    // Fetch rentals when the composable is launched
    LaunchedEffect(Unit) {
        // Launch a coroutine inside LaunchedEffect
        coroutineScope.launch {
            fetchRentals(rentalService, rentalList, loading)
        }
    }

    // Swipe refresh state to handle pull-to-refresh
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = loading.value)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            // Trigger the refresh when pulled down
            coroutineScope.launch {
                fetchRentals(rentalService, rentalList, loading)
            }
        }
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            // Loading indicator
            if (loading.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                // If no rentals are available, show a message
                if (rentalList.value.isEmpty()) {
                    Text(
                        text = "No rentals available",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    // Show the rental list
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(rentalList.value) { appliance ->
                            CustomCard(appliance)
                        }
                    }
                }
            }
        }
    }
}

// Function to fetch rentals from the service and update state
private suspend fun fetchRentals(
    rentalService: RentalService,
    rentalList: MutableState<List<Appliance>>,
    loading: MutableState<Boolean>
) {
    loading.value = true
    try {
        // Fetch rentals from the service
        val rentals = rentalService.getListOfRentals()
        rentalList.value = rentals
    } catch (e: Exception) {
        // Handle exceptions if necessary
        rentalList.value = emptyList()
    } finally {
        loading.value = false
    }
}
@Composable
fun CustomCard(appliance: Appliance) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = appliance.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
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
                    text = "Today",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                IconButton(
                    onClick = { /* Handle button click */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}