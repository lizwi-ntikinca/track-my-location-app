package com.lizwin.trackmylocation.presentation.location

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lizwin.trackmylocation.data.LocationClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LocationViewModel(
    private val locationClient: LocationClient
) : ViewModel() {

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location.asStateFlow()

    init {
        viewModelScope.launch {
            locationClient.getLocationUpdates(6000L)
                .catch { e -> Log.e("LocationVM", "Error: ${e.message}") }
                .collect { loc -> _location.value = loc }
        }
    }
}