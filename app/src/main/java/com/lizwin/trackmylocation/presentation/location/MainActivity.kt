package com.lizwin.trackmylocation.presentation.location

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.lizwin.trackmylocation.R
import com.lizwin.trackmylocation.service.LocationForegroundService
import com.lizwin.trackmylocation.service.LocationService
import com.lizwin.trackmylocation.ui.theme.TrackMyLocationTheme
import com.lizwin.trackmylocation.util.LocationServiceUtil

class MainActivity : ComponentActivity() {

    private lateinit var locationViewModel: LocationViewModel

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE_LOCATION
            ),
            1001
        )

        locationViewModel = ViewModelProvider(
            this,
            LocationViewModelFactory(applicationContext)
        )[LocationViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            val location by locationViewModel.location.collectAsState()

            TrackMyLocationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                LocationServiceUtil.checkAndRequestPermission(context = this@MainActivity, activity = activity)
                                /*Intent(applicationContext, LocationService::class.java).apply {
                                    action = LocationService.START
                                    startService(this)
                                }*/
                                val intent = Intent(
                                    applicationContext,
                                    LocationForegroundService::class.java
                                )
                                ContextCompat.startForegroundService(applicationContext, intent)
                            }
                        ) {
                            Text(stringResource(R.string.start))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = location?.let {
                                stringResource(R.string.coordinates, it.latitude, it.longitude)
                            } ?: stringResource(R.string.coordinates_waiting)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                location?.let {
                                    val gmmIntentUri = Uri.parse("geo:${it.latitude},${it.longitude}?q=${it.latitude},${it.longitude}(Current+Location)")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }

                                    if (mapIntent.resolveActivity(packageManager) != null) {
                                        startActivity(mapIntent)
                                    } else {
                                        Toast.makeText(applicationContext,
                                            getString(R.string.google_maps_is_not_installed), Toast.LENGTH_SHORT).show()
                                    }
                                } ?: Toast.makeText(this@MainActivity,
                                    getString(R.string.location_not_available), Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text(stringResource(R.string.open_in_google_maps))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                Intent(applicationContext, LocationService::class.java).apply {
                                    action = LocationService.STOP
                                    startService(this)
                                }
                            }
                        ) {
                            Text(stringResource(R.string.stop_tracking))
                        }
                    }
                }
            }
        }
    }
}
