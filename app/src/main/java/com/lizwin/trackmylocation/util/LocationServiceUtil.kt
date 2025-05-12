package com.lizwin.trackmylocation.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object LocationServiceUtil {
    fun isLocationPermissionGranted(context: Context) : Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            12
        )
    }

    fun checkAndRequestPermission(context: Context, activity: Activity) {
        if (!isLocationPermissionGranted(context)) {
            requestPermission(activity)
        }
    }
}