package com.lizwin.trackmylocation.presentation.location

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationServices
import com.lizwin.trackmylocation.data.DefaultLocationClient

class LocationViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val client = DefaultLocationClient(
            context,
            LocationServices.getFusedLocationProviderClient(context)
        )
        return LocationViewModel(client) as T
    }
}