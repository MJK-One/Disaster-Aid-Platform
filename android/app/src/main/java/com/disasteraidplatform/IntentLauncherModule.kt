package com.disasteraidplatform

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class IntentLauncherModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = "IntentLauncher"

    @ReactMethod
    fun startService(serviceClassName: String, action: String) {
        try {
            val clazz = Class.forName(serviceClassName)
            val intent = Intent(reactContext, clazz)
            intent.action = action

            if (action == "START") {
                if (isServiceRunning(serviceClassName)) {
                    // 이미 실행 중이면 무시
                    return
                }
            }

            reactContext.startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ✅ NativeEventEmitter 사용 시 반드시 필요
    @ReactMethod
    fun addListener(eventName: String?) {
        // JS 쪽에서 addListener('eventName', ...) 호출 시 필요
    }

    @ReactMethod
    fun removeListeners(count: Int?) {
        // JS 쪽에서 removeAllListeners 호출 시 필요
    }

    private fun isServiceRunning(serviceClassName: String): Boolean {
        val activityManager = reactContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (service.service.className == serviceClassName) {
                return true
            }
        }
        return false
    }
}
