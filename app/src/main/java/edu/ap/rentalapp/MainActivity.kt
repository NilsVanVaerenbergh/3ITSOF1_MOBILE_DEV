package edu.ap.rentalapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import edu.ap.rentalapp.ui.theme.RentalAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RentalAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AddApplianceScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AddApplianceScreen(modifier: Modifier = Modifier) {

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
            value = "",
            onValueChange = {

            },
            modifier = modifier
                .padding(10.dp)

        )
        UploadImagesFromGallery()
        OutlinedTextField(
            placeholder = { Text(text = "Description...") },
            value = "",
            onValueChange = {

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

            },
            modifier = modifier
                .padding(10.dp)
        ) {
            Text(text = "Add")
        }

    }

}

// https://www.howtodoandroid.com/pick-image-from-gallery-jetpack-compose/
@OptIn(ExperimentalCoilApi::class, ExperimentalFoundationApi::class)
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

@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    RentalAppTheme {
        AddApplianceScreen()
    }
}