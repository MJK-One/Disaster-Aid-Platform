package com.disasteraidplatform

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*

class TrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private val TAG = "📍TrackingService"

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationTracking()
        return START_STICKY
    }

    private fun startLocationTracking() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5_000L  // 5초 간격
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                Log.d(TAG, "위치 수집: ${location.latitude}, ${location.longitude}")

                cacheLocation(location.latitude, location.longitude)

                // React Native로 위치 이벤트 전송 필요 시 여기서 구현 가능
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    private fun cacheLocation(latitude: Double, longitude: Double) {
        val prefs = getSharedPreferences("location_cache", Context.MODE_PRIVATE)
        prefs.edit()
            .putFloat("lat", latitude.toFloat())
            .putFloat("lng", longitude.toFloat())
            .apply()
    }

    override fun onDestroy() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        locationCallback = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
