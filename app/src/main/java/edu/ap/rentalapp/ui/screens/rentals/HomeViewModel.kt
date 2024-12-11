package edu.ap.rentalapp.ui.screens.rentals

import android.location.Location
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel(){

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation.asStateFlow()

    fun updateUserLocation(location: Location?) {
        _userLocation.value = location
    }

    private val _selectedCategory = MutableStateFlow("Category")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    fun updateSearchText(text: String){
        _searchText.value = text
    }

    private val _maxRadius = MutableStateFlow(10.0)
    val maxRadius: StateFlow<Double> = _maxRadius.asStateFlow()

    fun updateMaxRadius(maxRadius: Double){
        _maxRadius.value = maxRadius
    }
}