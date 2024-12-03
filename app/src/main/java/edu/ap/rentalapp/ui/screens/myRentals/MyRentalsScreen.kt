package edu.ap.rentalapp.ui.screens.myRentals

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import edu.ap.rentalapp.ui.theme.Purple40

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
    Log.d("data", "MyRentalsScreen: $appliances")


    Column {
        Text(
            text = "My rentals",
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            modifier = modifier
                .fillMaxWidth()
        )
        HorizontalDivider()

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
                    .padding(top = 30.dp)

            )


            LazyColumn(
                modifier = modifier
                    .padding(15.dp)
                    .fillMaxSize()
            ) {
                items(appliances.filter { appliance ->
                    appliance.name.lowercase().contains(searchText.lowercase())
                }) { appliance ->
                    ApplianceItemBox(appliance = appliance)
                }
                item {

                    OutlinedButton(
                        onClick = { navController.navigate("addAppliance") },
                        modifier = modifier
                            .padding(top = 15.dp)
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
    }


}

@Composable
fun ApplianceItemBox(appliance: MyAppliance) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray, shape = ShapeDefaults.Small)
            //.border(BorderStroke(1.dp, Color.Black), shape = ShapeDefaults.Small)
            .padding(8.dp)


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
        }
    }
}