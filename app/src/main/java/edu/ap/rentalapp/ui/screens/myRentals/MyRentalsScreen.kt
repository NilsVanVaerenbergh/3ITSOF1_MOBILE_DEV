package edu.ap.rentalapp.ui.screens.myRentals

import android.content.Context
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import edu.ap.rentalapp.components.CategorySelect
import edu.ap.rentalapp.components.filterItemsByCategory
import edu.ap.rentalapp.entities.ApplianceDTO
import edu.ap.rentalapp.entities.ApplianceRentalDate
import edu.ap.rentalapp.extensions.instances.RentalServiceSingleton
import edu.ap.rentalapp.ui.theme.Purple40
import java.text.SimpleDateFormat
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
                    MyRentalCard(
                        appliance = appliance,
                        context = context,
                        navController = navController
                    )
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

@Composable
fun MyRentalCard(appliance: ApplianceDTO, context: Context, navController: NavHostController) {

    val rentalService = RentalServiceSingleton.getInstance(context)
    val rentalDates = remember { mutableStateOf<List<ApplianceRentalDate>>(emptyList()) }

    LaunchedEffect(Unit) {
        rentalDates.value = rentalService.getRentalDatesForAppliance(appliance.id)
        //Log.d("rentals", "MyRentalCard: $rentalDates")
    }
    val hasRentalDates = rentalDates.value.isNotEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("rental/${appliance.id}")
            },
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
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = appliance.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 4.dp)
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
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.Bottom)
                    .align(Alignment.CenterVertically)
            ) {
                if (!hasRentalDates) {
                    Text(
                        text = "Not rented",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        modifier = Modifier
                            .background(Color.Red, shape = ShapeDefaults.Small)
                            .padding(2.dp)
                    )
                } else {
                    Text(
                        text = SimpleDateFormat(
                            "dd MMM",
                            Locale.getDefault()
                        ).format(rentalDates.value[0].endDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        modifier = Modifier
                            .background(Color.Green, shape = ShapeDefaults.Small)
                            .padding(2.dp)
                    )
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