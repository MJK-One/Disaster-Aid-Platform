package com.disasteraidplatform

import android.app.Service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.disasteraidplatform.auth.JwtManager
import com.disasteraidplatform.location.LocationHandler

class LocationSenderService : Service() {

    private val TAG = "📍LocationSenderService"
    private val handler = Handler()
    private val intervalMillis = 30_000L
    private val notificationId = 1001
    private val channelId = "location_service_channel"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannelIfNeeded()
        val notification = buildNotification()
        startForeground(notificationId, notification)
        handler.post(sendLocationRunnable)
        return START_STICKY
    }

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "위치 서비스 채널",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("위치 추적 중")
            .setContentText("앱이 백그라운드에서 위치를 전송 중입니다.")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private val sendLocationRunnable = object : Runnable {
        override fun run() {
            sendCachedLocationToServer()
            handler.postDelayed(this, intervalMillis)
        }
    }

    private fun sendCachedLocationToServer() {
        val prefs = getSharedPreferences("location_cache", MODE_PRIVATE)
        val lat = prefs.getFloat("lat", Float.NaN)
        val lng = prefs.getFloat("lng", Float.NaN)
        val province = prefs.getString("province", null)
        val city = prefs.getString("city", null)

        if (lat.isNaN() || lng.isNaN() || province.isNullOrEmpty() || city.isNullOrEmpty()) {
            Log.w(TAG, "❌ 전송할 위치 정보가 부족합니다.")
            return
        }

        val jwt = JwtManager.getToken()
        if (jwt == null) {
            Log.w(TAG, "❌ JWT 토큰 없음")
            return
        }

        val location = Location("").apply {
            latitude = lat.toDouble()
            longitude = lng.toDouble()
        }

        // 🔹 출석 WebSocket용
        val volunteerPrefs = getSharedPreferences("tracking_info", MODE_PRIVATE)
        val volunteerId = volunteerPrefs.getString("volunteerId", null)
        if (!volunteerId.isNullOrEmpty()) {
            LocationHandler.sendAttendanceViaWebSocket(location, jwt, volunteerId)
            Log.d(TAG, "📡 출석 WebSocket 전송")
        }

        // 🔹 주기적 기록 API용
        LocationHandler.sendLocationRecordViaApi(jwt, province, city)
        Log.d(TAG, "📡 위치 기록 API 전송 완료")
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(sendLocationRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
