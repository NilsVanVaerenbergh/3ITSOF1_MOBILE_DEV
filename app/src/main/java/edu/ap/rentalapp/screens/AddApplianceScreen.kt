package edu.ap.rentalapp.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import edu.ap.rentalapp.ui.theme.Blue
import edu.ap.rentalapp.ui.theme.RentalAppTheme

@Composable
fun AddApplianceScreen(modifier: Modifier = Modifier) {

    val paddingInBetween = 10.dp

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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
                UploadImagesFromGallery()
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
                        Log.d("textfields", "Name: $name\nDescription: $description")
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
            selectImages += it
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
        items(selectImages) { uri ->
            Image(
                painter = rememberImagePainter(uri),
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
                modifier = modifier
                    .padding(5.dp, 5.dp)
                    .size(100.dp)
                    .clip(shape = ShapeDefaults.Small)
                    .border(BorderStroke(1.dp, Blue))
                    .clickable(onClick = { selectImages -= uri })

            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    RentalAppTheme {
        AddApplianceScreen()
    }
}