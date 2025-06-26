// android/app/src/main/java/com/disasteraidplatform/network/BackendApi.kt
package com.disasteraidplatform.network

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object BackendApi {

    private val client = OkHttpClient()

    fun sendRegion(jwtToken: String, si: String, gu: String?) {
        val json = JSONObject().apply {
            put("si", si)
            put("gu", gu ?: JSONObject.NULL)
        }

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("http://192.168.45.91:8080/api/location/region") // 환경에 맞게 변경
            .addHeader("Authorization", "Bearer $jwtToken")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("백엔드 전송 실패 코드: ${response.code}")
            }
        }
    }
}
