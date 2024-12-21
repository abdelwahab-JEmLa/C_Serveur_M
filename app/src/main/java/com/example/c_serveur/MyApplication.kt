package com.example.c_serveur

import android.app.Application
import com.example.c_serveur.Modules.Z.Archives.AppDatabase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    private fun initializeDatabase() {
        database = AppDatabase.DatabaseModule.getDatabase(this)
    }

    private fun initializeFirebase() {
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    override fun onCreate() {
        super.onCreate()
        initializeDatabase()
        initializeFirebase()
    }
}
