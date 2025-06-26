// android/app/src/main/java/com/disasteraidplatform/location/LocationHandler.kt
package com.disasteraidplatform.location

import android.content.Context
import android.location.Location
import android.util.Log
import com.disasteraidplatform.auth.JwtManager
import com.disasteraidplatform.network.BackendApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object LocationHandler {

    private const val TAG = "📍LocationHandler"

    fun handleLocationUpdate(context: Context, location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val region = KakaoRegionApi.fetchRegion(location.longitude, location.latitude)
                Log.d(TAG, "시: ${region.si}, 구: ${region.gu}")

                val jwt = JwtManager.getToken()
                if (jwt != null) {
                    BackendApi.sendRegion(jwt, region.si, region.gu)
                } else {
                    Log.w(TAG, "JWT 토큰 없음, 서버 전송 생략")
                }
            } catch (e: Exception) {
                Log.e(TAG, "위치 처리 실패", e)
            }
        }
    }
}
