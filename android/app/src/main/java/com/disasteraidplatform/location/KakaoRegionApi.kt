// android/app/src/main/java/com/disasteraidplatform/location/KakaoRegionApi.kt
package com.disasteraidplatform.location

import android.util.Log
import com.disasteraidplatform.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class Region(val si: String, val gu: String?)

object KakaoRegionApi {
    private const val TAG = "📍KakaoRegionApi"
    private val client = OkHttpClient()

    suspend fun fetchRegion(longitude: Double, latitude: Double): Region = withContext(Dispatchers.IO) {
        try {
            val url = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=$longitude&y=$latitude"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "카카오 API 실패 코드: ${response.code}")
                    return@withContext Region("", null)
                }

                val body = response.body?.string()
                if (body == null) {
                    Log.e(TAG, "응답 바디가 null입니다.")
                    return@withContext Region("", null)
                }

                val json = JSONObject(body)
                val documents = json.optJSONArray("documents")
                if (documents == null || documents.length() == 0) {
                    Log.w(TAG, "카카오 API 응답에 documents 없음")
                    return@withContext Region("", null)
                }

                val region = documents.getJSONObject(0)
                val siRaw = region.optString("region_1depth_name", "")
                val guRaw = region.optString("region_2depth_name", "")

                return@withContext RegionParser.parse(siRaw, guRaw)
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchRegion 실패", e)
            return@withContext Region("", null)
        }
    }
}
