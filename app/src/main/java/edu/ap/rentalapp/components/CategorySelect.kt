package edu.ap.rentalapp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.ap.rentalapp.entities.ApplianceDTO
import edu.ap.rentalapp.ui.theme.Green
import edu.ap.rentalapp.ui.theme.Purple40

@Composable
fun CategorySelect(
    modifier: Modifier = Modifier,
    setCategory: (category: String) -> Unit,
    enabled: Boolean = true
) {
    val categories = listOf("Garden", "Kitchen", "Maintenance", "Other")
    var expandedCat by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Category") }


    OutlinedButton(
        onClick = { expandedCat = !expandedCat },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selectedCategory != "Category") Green else Color.White,
            contentColor = if (selectedCategory != "Category") Color.White else Green
        ),
        border = BorderStroke(2.dp, Green),
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Remove",
        )
    }
    DropdownMenu(
        expanded = expandedCat,
        onDismissRequest = { expandedCat = false },
        modifier = modifier.fillMaxWidth()
    ) {
        for (cat in categories) {
            DropdownMenuItem(
                onClick = {
                    selectedCategory = cat
                    setCategory(selectedCategory)
                    expandedCat = false
                },
                text = { Text(cat) }
            )
        }
        DropdownMenuItem(
            onClick = {
                selectedCategory = "Category"
                setCategory(selectedCategory)
                expandedCat = false
            },
            text = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove"
                    )
                    Text("Remove filter(s)")
                }
            }
        )
    }
}

fun filterItemsByCategory(
    items: List<ApplianceDTO>,
    category: String
): List<ApplianceDTO> {
    return if (category != "Category") {
        items.filter { item -> item.category == category }
    } else items
}