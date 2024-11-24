package edu.ap.rentalapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import edu.ap.rentalapp.ui.theme.Blue

@Composable
fun AddApplianceScreen(modifier: Modifier = Modifier, navController: NavHostController) {

    val db = Firebase.firestore
    val paddingInBetween = 10.dp

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf(listOf<Uri>()) }
    var expanded by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf("Select Category") }
    val categories = listOf("Garden", "Kitchen", "Maintenance")

    val context = LocalContext.current
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
                Column {
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
                    value = "Location",
                    onValueChange = {

                    },
                    shape = ShapeDefaults.Small,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location icon",
                        )
                    },
                    modifier = modifier
                        .fillMaxWidth()
                )
            }

            item {
                Button(
                    onClick = {
                        UploadImagesToFirebase(
                            name = name,
                            description = description,
                            category = selectedCategory,
                            images = selectedImages
                        )

//                        db.collection("myAppliances")
//                            .add(
//                                hashMapOf(
//                                    "name" to name,
//                                    "description" to description,
//                                    "images" to selectedImages,
//                                    "category" to selectedCategory
//                                )
//                            )
//                            .addOnSuccessListener { documentReference ->
//                                Log.d(
//                                    "firebase",
//                                    "DocumentSnapshot added with ID: ${documentReference.id}"
//                                )
//                                //Toast.makeText(context, "Item added successfully!", Toast.LENGTH_SHORT).show()
//                            }
//                            .addOnFailureListener { e ->
//                                Log.w("firebase", "Error adding document", e)
//                            }
                        navController.navigate("myRentals")
                    },
                    colors = ButtonDefaults.buttonColors(
                        Blue
                    ),
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)


                ) {
                    Text(text = "Add")
                }

            }

            item {
                Button(onClick = { navController.navigate("myRentals") }) {
                    Text("My Rentals")
                }
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


fun UploadImagesToFirebase(
    name: String,
    description: String,
    category: String,
    images: List<Uri>,
    //modifier: Modifier = Modifier
) {
    val storage = FirebaseStorage.getInstance()
    val storageReference = storage.reference

    val firestore = Firebase.firestore

    val imageUrls = mutableListOf<String>()

    val uploads = images.map { uri ->
        val imageReference = storageReference.child("images/" + uri.lastPathSegment)

        imageReference.putFile(uri)
            .continueWithTask { task ->
                imageReference.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                imageUrls.add(downloadUrl.toString())
            }
    }

    Tasks.whenAllComplete(uploads)
        .addOnSuccessListener {
            val appliance = hashMapOf(
                "name" to name,
                "description" to description,
                "images" to imageUrls,
                "category" to category
            )

            firestore.collection("myAppliances")
                .add(appliance)
                .addOnSuccessListener { }
                .addOnFailureListener { }
        }
        .addOnFailureListener {}
}