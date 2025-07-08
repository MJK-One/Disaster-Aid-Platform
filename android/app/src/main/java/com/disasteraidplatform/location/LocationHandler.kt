package com.disasteraidplatform.location

import android.content.Context
import android.util.Log
import android.location.Location
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object LocationHandler {

    private const val TAG = "📡LocationHandler"
    private var webSocket: WebSocket? = null
    private var isConnected = false
    private const val WS_URL = "ws://192.168.254.137:8080/api/location-tracking"

    // --- WebSocket 연결 ---
    fun connectWebSocket(token: String) {
        if (isConnected) return

        val request = Request.Builder()
            .url("$WS_URL?token=$token")
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                isConnected = true
                Log.i(TAG, "✅ WebSocket 연결됨")
            }

            override fun onMessage(ws: WebSocket, text: String) {
                Log.i(TAG, "📩 WebSocket 수신: $text")
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "❌ WebSocket 오류", t)
                isConnected = false
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                Log.w(TAG, "🔌 WebSocket 종료 중: $code / $reason")
                isConnected = false
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                Log.i(TAG, "🛑 WebSocket 종료됨: $code / $reason")
                isConnected = false
            }
        })
    }

    // --- 출석용 WebSocket 전송 (volunteerId, 위도, 경도) ---
    fun sendAttendanceViaWebSocket(
        location: Location,
        jwt: String,
        volunteerId: String
    ) {
        if (!isConnected) {
            connectWebSocket(jwt)
        }

        val payload = JSONObject().apply {
            put("type", "location_update")
            put("data", JSONObject().apply {
                put("volunteerId", volunteerId)
                put("latitude", location.latitude)
                put("longitude", location.longitude)
            })
        }

        webSocket?.send(payload.toString())
        Log.d(TAG, "🛰 출석 WebSocket 전송: $payload")
    }

    // --- 위치 기록용 REST API 전송 (province, city만 전송) ---
    fun sendLocationRecordViaApi(
        jwt: String,
        province: String,
        city: String
    ) {
        val json = JSONObject().apply {
            put("province", province)
            put("city", city)
        }

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url("http://192.168.254.137:8080/api/location-record")
            .addHeader("Authorization", "Bearer $jwt")
            .post(body)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "❌ 위치 기록 API 전송 실패", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d(TAG, "✅ 위치 기록 API 전송 성공")
                } else {
                    Log.w(TAG, "⚠️ 위치 기록 API 실패 코드: ${response.code}")
                }
                response.close()
            }
        })
    }

    // --- WebSocket 종료 (옵션) ---
    fun closeWebSocket() {
        webSocket?.close(1000, "종료 요청")
        isConnected = false
    }
}
