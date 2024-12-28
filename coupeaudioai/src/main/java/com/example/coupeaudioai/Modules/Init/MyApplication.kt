package com.example.coupeaudioai.Modules.Init

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {
    private fun initializeFirebase() {
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    override fun onCreate() {
        super.onCreate()
        initializeFirebase()
    }
}
