// android/app/src/main/java/com/disasteraidplatform/RecordingService.kt
package com.disasteraidplatform

import android.app.*
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.facebook.react.ReactApplication
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.google.android.gms.location.*
import com.disasteraidplatform.location.LocationHandler

class RecordingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val TAG = "📍RecordingService"

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START" -> startForegroundService()
            "STOP" -> stopSelf()
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, "foreground_channel")
            .setContentTitle("위치 추적 활성화됨")
            .setContentText("현재 위치 수집 중...")
            .setContentIntent(pendingIntent)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()

        startForeground(1, notification)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            60_000L
        ).setWaitForAccurateLocation(false).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location: Location = result.lastLocation ?: return
                Log.d(TAG, "위치: ${location.latitude}, ${location.longitude}")

                sendLocationToReactNative(location.latitude, location.longitude)
                LocationHandler.handleLocationUpdate(applicationContext, location)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun sendLocationToReactNative(latitude: Double, longitude: Double) {
        val reactContext = (application as ReactApplication).reactNativeHost.reactInstanceManager.currentReactContext
        reactContext?.getJSModule(RCTDeviceEventEmitter::class.java)
            ?.emit("onLocationUpdate", mapOf("latitude" to latitude, "longitude" to longitude))
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
