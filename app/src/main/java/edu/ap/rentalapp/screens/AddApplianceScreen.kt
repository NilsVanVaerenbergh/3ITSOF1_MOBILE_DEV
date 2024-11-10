package edu.ap.rentalapp.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

@Composable
fun AddApplianceScreen(modifier: Modifier = Modifier) {

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column {
        Text(
            text = "Add appliance",
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp)

        )
        HorizontalDivider()
        OutlinedTextField(
            label = { Text(text = "Name") },
            value = name,
            onValueChange = { text ->
                name = text
            },
            modifier = modifier
                .padding(10.dp)

        )
        UploadImagesFromGallery()
        OutlinedTextField(
            placeholder = { Text(text = "Description...") },
            value = description,
            onValueChange = { text ->
                description = text
            },
            modifier = modifier
                .padding(10.dp)

        )
        OutlinedTextField(
            placeholder = { Text(text = "Location") },
            value = "Location",
            onValueChange = {

            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location icon",
                )
            },
            modifier = modifier
                .padding(10.dp)

        )

        Button(
            onClick = {
                Log.d("textfields", "Name: $name\nDescription: $description")
            },
            modifier = modifier
                .padding(10.dp)
        ) {
            Text(text = "Add")
        }

    }

}

// https://www.howtodoandroid.com/pick-image-from-gallery-jetpack-compose/
@OptIn(ExperimentalCoilApi::class)
@Composable
fun UploadImagesFromGallery(modifier: Modifier = Modifier) {

    var selectImages by remember { mutableStateOf(listOf<Uri>()) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            selectImages = it
        }


    Button(
        onClick = { galleryLauncher.launch("image/*") },
        modifier = Modifier
            .wrapContentSize()
            .padding(10.dp)
    ) {
        Text(text = "Pick Image From Gallery")
    }
    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
        items(selectImages) { uri ->
            Image(
                painter = rememberImagePainter(uri),
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp, 8.dp)
                    .size(100.dp)
                    .clickable {

                    }
            )
        }
    }

}