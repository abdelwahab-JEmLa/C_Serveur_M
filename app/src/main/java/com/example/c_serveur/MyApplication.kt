package com.example.c_serveur

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

class MyApplication : Application() {
    private fun initializeFirebase() {
        FirebaseApp.initializeApp(this)

        // Configure Firebase Database
        FirebaseDatabase.getInstance().apply {
            // Enable persistence
            setPersistenceEnabled(true)

            // Set the disk persistence size to 100MB
            setPersistenceCacheSizeBytes(100 * 1024 * 1024)
        }
    }

    override fun onCreate() {
        super.onCreate()
        initializeFirebase()
    }
}

// Add this companion object to handle offline data
object FirebaseOfflineHandler {
    fun keepSynced(ref: DatabaseReference) {
        ref.keepSynced(true)
    }

    suspend fun loadOfflineFirst(ref: DatabaseReference): DataSnapshot? {
        return try {
            // First try to load from disk cache
            val offlineSnapshot = ref.get().await()

            // Then setup online listener for updates
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Handle online updates
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Online sync failed", error.toException())
                }
            })

            offlineSnapshot
        } catch (e: Exception) {
            Log.e("Firebase", "Offline load failed", e)
            null
        }
    }
}
