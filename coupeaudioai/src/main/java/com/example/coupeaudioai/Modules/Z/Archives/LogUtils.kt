package com.example.coupeaudioai.Modules.Z.Archives

import android.util.Log

object LogUtils {
    private const val APP_TAG = "MyApp"
    
    fun d(tag: String, message: String) {
        Log.d("$APP_TAG/$tag", message)
    }
    
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e("$APP_TAG/$tag", message, throwable)
    }
    
    fun w(tag: String, message: String) {
        Log.w("$APP_TAG/$tag", message)
    }
    
    fun i(tag: String, message: String) {
        Log.i("$APP_TAG/$tag", message)
    }
}
