package com.disasteraidplatform

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceManager
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.google.android.gms.location.*
import com.disasteraidplatform.location.LocationHandler

class RecordingService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private val TAG = "📍RecordingService"
    private var isTracking = false

    private var retryCount = 0
    private val maxRetry = 3
    private val retryDelayMillis = 3000L

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START" -> {
                if (!isTracking) {
                    startForegroundService()
                    isTracking = true
                } else {
                    Log.d(TAG, "이미 위치 추적 중")
                }
            }
            "STOP" -> {
                if (isTracking) {
                    stopLocationUpdates()
                    isTracking = false
                }
                stopSelf()
            }
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
                val location = result.lastLocation ?: return
                Log.d(TAG, "위치: ${location.latitude}, ${location.longitude}")

                // ✅ 위치 캐싱 추가
                cacheLocation(location.latitude, location.longitude)

                sendLocationToReactNative(location.latitude, location.longitude)
                LocationHandler.handleLocationUpdate(applicationContext, location)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
        stopForeground(true)
    }

    private fun sendLocationToReactNative(latitude: Double, longitude: Double) {
        val reactApp = application as ReactApplication
        val reactInstanceManager: ReactInstanceManager = reactApp.reactNativeHost.reactInstanceManager
        val currentContext = reactInstanceManager.currentReactContext

        val params: WritableMap = Arguments.createMap().apply {
            putDouble("latitude", latitude)
            putDouble("longitude", longitude)
        }

        if (currentContext != null) {
            retryCount = 0
            currentContext
                .getJSModule(RCTDeviceEventEmitter::class.java)
                .emit("onLocationUpdate", params)
        } else {
            if (retryCount < maxRetry) {
                Log.w(TAG, "ReactContext가 없습니다. emit 대기 중... 재시도 ${retryCount + 1}/$maxRetry")
                retryCount++
                Handler(Looper.getMainLooper()).postDelayed({
                    sendLocationToReactNative(latitude, longitude)
                }, retryDelayMillis)
            } else {
                Log.e(TAG, "ReactContext 연결 실패 - 더 이상 재시도하지 않음.")
                retryCount = 0
            }

            reactInstanceManager.addReactInstanceEventListener(object : ReactInstanceManager.ReactInstanceEventListener {
                override fun onReactContextInitialized(context: ReactContext) {
                    context
                        .getJSModule(RCTDeviceEventEmitter::class.java)
                        .emit("onLocationUpdate", params)
                    reactInstanceManager.removeReactInstanceEventListener(this)
                }
            })
        }
    }

    // ✅ 캐시 저장 함수 추가
    private fun cacheLocation(latitude: Double, longitude: Double) {
        val prefs = getSharedPreferences("location_cache", Context.MODE_PRIVATE)
        prefs.edit()
            .putFloat("lat", latitude.toFloat())
            .putFloat("lng", longitude.toFloat())
            .apply()
    }

    override fun onDestroy() {
        stopLocationUpdates()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
