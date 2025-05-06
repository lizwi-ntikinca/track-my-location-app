package com.lizwin.trackmylocation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.lizwin.trackmylocation.service.LocationService.Companion.CHANNEL_ID

class TrackMyLocationApp: Application() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
           val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
           manager.createNotificationChannel(channel)
        }
    }
}