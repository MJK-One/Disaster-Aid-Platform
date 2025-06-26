// android/app/src/main/java/com/disasteraidplatform/location/KakaoRegionApi.kt
package com.disasteraidplatform.location

import com.disasteraidplatform.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class Region(val si: String, val gu: String?)

object KakaoRegionApi {

    private val client = OkHttpClient()

    fun fetchRegion(longitude: Double, latitude: Double): Region {
        val url = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=$longitude&y=$latitude"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("카카오 API 실패 코드: ${response.code}")

            val body = response.body?.string() ?: throw Exception("응답 바디 없음")
            val json = JSONObject(body)
            val documents = json.getJSONArray("documents")
            if (documents.length() == 0) return Region("", null)

            val region = documents.getJSONObject(0)
            val siRaw = region.getString("region_1depth_name")
            val guRaw = region.getString("region_2depth_name")

            return RegionParser.parse(siRaw, guRaw)
        }
    }
}
