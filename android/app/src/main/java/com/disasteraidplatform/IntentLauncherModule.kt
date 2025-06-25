package com.disasteraidplatform

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
            reactContext.startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
