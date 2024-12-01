package edu.ap.rentalapp.ui.screens

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import edu.ap.rentalapp.components.OSM
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.ui.theme.Blue
import java.util.Locale


@Composable
fun AddApplianceScreen(modifier: Modifier = Modifier, navController: NavHostController) {

    val paddingInBetween = 10.dp
    val context = LocalContext.current

    val authenticationManager = remember { AuthenticationManager(context) }
    val user = authenticationManager.auth.currentUser

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf(listOf<Uri>()) }
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Select Category") }
    val categories = listOf("Garden", "Kitchen", "Maintenance", "Other")
    var loading by remember { mutableStateOf(false) }

    // Map variables
    var address by remember { mutableStateOf("") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var zoomLevel by remember { mutableDoubleStateOf(18.0) }



    Column(
        modifier = modifier
            .padding(15.dp)
            .fillMaxSize()
    )
    {
        Text(
            text = "Add appliance",
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            modifier = modifier
                .fillMaxWidth()
        )
        HorizontalDivider()

        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            item {
                OutlinedTextField(
                    label = { Text(text = "Name") },
                    value = name,
                    onValueChange = { text ->
                        name = text
                    },
                    shape = ShapeDefaults.Small,
                    modifier = modifier
                        .fillMaxWidth()
                )
            }

            item {
                UploadImagesFromGallery(
                    images = selectedImages,
                    onImagesSelected = { images -> selectedImages = images }
                )
            }

            item {
                OutlinedTextField(
                    placeholder = { Text(text = "Description...") },
                    value = description,
                    onValueChange = { text ->
                        description = text
                    },
                    shape = ShapeDefaults.Small,
                    modifier = modifier
                        .padding(vertical = paddingInBetween)
                        .fillMaxWidth()
                )
            }

            item {
                Column(
                    modifier = modifier
                        .padding(vertical = paddingInBetween)
                        .fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = selectedCategory)
                    }
                    DropdownMenu(
                        modifier = modifier.fillMaxWidth(),
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        for (cat in categories) {
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    placeholder = { Text(text = "Location") },
                    value = address,
                    onValueChange = { address = it },
                    shape = ShapeDefaults.Small,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                        )
                    },
                    modifier = modifier
                        .padding(vertical = paddingInBetween)
                        .fillMaxWidth()
                )
            }

            item {
                Button(
                    onClick = {
                        findGeoLocationFromAddress(
                            address = address,
                            context = context,
                            assignLat = { lat ->
                                latitude = lat
                            },
                            assignLon = { lon ->
                                longitude = lon
                            },
                        )

                    },
                    modifier = modifier
                        .padding(vertical = paddingInBetween)
                        .fillMaxWidth()
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location icon"
                        )
                        Text("Find Location")
                    }

                }

            }

            item {
                OSM(
                    modifier = modifier
                        .padding(vertical = 50.dp)
                        .height(100.dp),
                    latitude = latitude,
                    longitude = longitude,
                    zoomLevel = 18.0,
                    context = context,
                )

            }

            item {
                val validLocation = address.isNotBlank() && longitude != 0.0 && latitude != 0.0
                val isFormValid =
                    name.isNotBlank() && description.isNotBlank() && selectedCategory.isNotEmpty() && selectedImages.isNotEmpty()
                Button(
                    onClick = {
                        if (isFormValid && validLocation) {
                            loading = true
                            uploadApplianceToFirebase(
                                name = name,
                                description = description,
                                category = if (selectedCategory == "Select Category") "Other" else selectedCategory,
                                images = selectedImages,
                                address = address,
                                longitude = longitude,
                                latitude = latitude,
                                userId = user?.uid.toString(),
                                onSuccess = {
                                    loading = false
                                    Log.d("firebase", "Item added successfully!")
                                    Toast.makeText(
                                        context,
                                        "Item added successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onError = { exception ->
                                    loading = false
                                    Log.d("firebase", "Error adding item", exception)
                                    Toast.makeText(
                                        context,
                                        "Error adding item: $exception",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                navController = navController
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Please fill in all fields and select at least one image.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        // navController.navigate("myRentals")
                    },
                    colors = ButtonDefaults.buttonColors(
                        Blue
                    ),
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .padding(top = 50.dp)


                ) {
                    Text(text = "Add")
                }

            }

            item {
                OutlinedButton(onClick = { navController.navigate("myRentals") }
                ) {
                    Text("To rentals")
                }
            }
        }

        if (loading) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

}

// https://www.howtodoandroid.com/pick-image-from-gallery-jetpack-compose/
@Composable
fun UploadImagesFromGallery(
    modifier: Modifier = Modifier,
    images: List<Uri>,
    onImagesSelected: (List<Uri>) -> Unit
) {

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            onImagesSelected(images + it)
        }

    OutlinedIconButton(
        onClick = { galleryLauncher.launch("image/*") },
        shape = ShapeDefaults.Small,
        modifier = modifier
            .padding(top = 15.dp)
            .size(150.dp)
            .background(Blue, shape = ShapeDefaults.Small)


    ) {
        Icon(Icons.Default.Add, contentDescription = "Add images")
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier
            .heightIn(max = 300.dp)
            .padding(vertical = 10.dp)

    ) {
        items(images) { uri ->
            Box {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = null,
                    modifier = modifier
                        .padding(5.dp, 5.dp)
                        .size(100.dp)
                        .clip(shape = ShapeDefaults.Small)
                        .border(BorderStroke(1.dp, Blue))

                )
                Icon(
                    imageVector = Icons.Sharp.Delete,
                    contentDescription = "Remove image",
                    tint = Color.Red,
                    modifier = modifier
                        .align(Alignment.TopEnd)
                        .padding(horizontal = 10.dp)
                        .background(Color.White, ShapeDefaults.Small)
                        .border(1.dp, Color.Black, ShapeDefaults.Small)
                        .clickable(onClick = {
                            val updatedImages = images.toMutableList()
                            updatedImages.remove(uri)
                            onImagesSelected(updatedImages)
                        })
                )

            }

        }
    }

}


fun uploadApplianceToFirebase(
    name: String,
    description: String,
    category: String,
    images: List<Uri>,
    address: String,
    longitude: Double,
    latitude: Double,
    userId: String,
    onSuccess: () -> Unit,
    onError: (Exception) -> Unit,
    navController: NavHostController
    //modifier: Modifier = Modifier
) {
    val storage = FirebaseStorage.getInstance()
    val storageReference = storage.reference

    val firestore = Firebase.firestore

    val imageUrls = mutableListOf<String>()

    val imageUploads = images.map { uri ->
        val imageReference = storageReference.child("images/" + uri.lastPathSegment)

        imageReference.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageReference.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                imageUrls.add(downloadUrl.toString())
            }
    }

    val applianceUpload = Tasks.whenAllComplete(imageUploads)
        .addOnSuccessListener {
            val appliance = hashMapOf(
                "name" to name,
                "description" to description,
                "images" to imageUrls,
                "category" to category,
                "address" to address,
                "latitude" to latitude,
                "longitude" to longitude,
                "userId" to userId

            )

            firestore.collection("myAppliances")
                .add(appliance)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onError(exception) }

            navController.navigate("myRentals")
        }
        .addOnFailureListener { exception -> onError(exception) }

    Tasks.whenAllComplete(applianceUpload)
        .addOnSuccessListener{
            navController.navigate("myRentals")
        }
        .addOnFailureListener { exception -> onError(exception) }


}

fun findGeoLocationFromAddress(
    address: String,
    assignLat: (latitude: Double) -> Unit,
    assignLon: (longitude: Double) -> Unit,
    context: Context
) {
    if (address.isNotBlank()) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val results = geocoder.getFromLocationName(address, 1)
            if (results != null) {
                if (results.isNotEmpty()) {
                    val location = results[0]
                    assignLat(location.latitude)
                    assignLon(location.longitude)
                } else {
                    Toast.makeText(
                        context,
                        "Location not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}