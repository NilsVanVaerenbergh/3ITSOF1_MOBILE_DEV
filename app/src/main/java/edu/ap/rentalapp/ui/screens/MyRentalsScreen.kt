package edu.ap.rentalapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.rentalapp.R

data class MyAppliance(
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val images: String = ""
)

@Composable
fun MyRentalsScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val firestore = FirebaseFirestore.getInstance()
    val rentals = firestore.collection("myAppliances")
    var data by remember { mutableStateOf(listOf<MyAppliance>()) }
    val isLoading = remember { mutableStateOf(false) }
/*
    LaunchedEffect(Unit) {
        firestore.collection("myAppliances")
            .get()
            .addOnSuccessListener { querySnapshot ->
                appliances.value = querySnapshot.mapNotNull { it.toObject(MyAppliance::class.java) }
                isLoading.value = false
                Log.d("firebase", "Read Gelukt")
            }
            .addOnFailureListener { exception ->
                Log.e("firebase", "Error fetching documents", exception)
                isLoading.value = false
            }
    }
    */



    rentals.get().addOnSuccessListener { appliance ->
        for (rental in appliance.documents) {
            //var data = listOf<MyAppliance>()
            val i = MyAppliance(
                name = rental["name"].toString(),
                description = rental["description"].toString(),
                category = rental["category"].toString(),
                images = rental["images"].toString()
            )
            data += i

        }
    }


    //Log.d("firebase", "${listRentals.size}")

    Column {
        Text(
            text = "My rentals",
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            modifier = modifier
                .fillMaxWidth()
        )
        HorizontalDivider()

        if(isLoading.value){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        else {
            LazyColumn(
                modifier = modifier
                    .padding(15.dp)
                    .fillMaxSize()
            ) {
                item {
                    Column {
                        for (rental in data) {
                            Box(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .border(BorderStroke(1.dp, Color.Black))
                            ) {
                                Row {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                        contentScale = ContentScale.FillWidth,
                                        contentDescription = null,
                                    )
                                    Column {
                                        Text(rental.name)
                                        Text(rental.category)
                                    }
                                }
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(5.dp)
                                )
                            }
                        }
                    }

                }
                item {
                    OutlinedButton(onClick = { navController.navigate("addAppliance") }) { Text("Return") }

                }

            }
        }
    }
}

