package com.lizwin.trackmylocation.service

import android.Manifest
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.app.NotificationChannel
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.lizwin.trackmylocation.util.LocationServiceUtil

class LocationForegroundService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var updateInterval: Long = 15000L // default 15 seconds
    var count = 0

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startForegroundNotification()
        startLocationUpdates()
    }

    private fun startForegroundNotification() {
        val channelId = "location_channel"
        val channel = NotificationChannel(
            channelId,
            "Location Service",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracking location")
            .setContentText("Sending location updates to server.")
            .build()

        startForeground(1, notification)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            updateInterval
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                sendLocationToApi(location)
            }
        }

        if (LocationServiceUtil.isLocationPermissionGranted(this)) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun sendLocationToApi(location: Location) {
        count++
        Log.e("LocationService", "Location coordinates: (${location.longitude}, ${location.longitude})")
        if(count == 10) {
            val newInterval = (4..8).random() * 1000L
            Log.e("LocationService", "New interval: ${newInterval / 1000L}")
            if (newInterval != updateInterval) {
                updateInterval = newInterval
                count = 0
                restartLocationUpdates()
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun restartLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        startLocationUpdates() // Restart with new interval
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}