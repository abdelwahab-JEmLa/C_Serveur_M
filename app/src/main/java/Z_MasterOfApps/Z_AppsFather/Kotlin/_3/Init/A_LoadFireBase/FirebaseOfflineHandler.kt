package Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.A_LoadFireBase

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object FirebaseOfflineHandler {
    const val TAG = "FirebaseOfflineHandler"
    private const val CACHE_SIZE_BYTES = 100 * 1024 * 1024 // 100MB

    fun initializeFirebase(app: FirebaseApp) {
        try {
            FirebaseDatabase.getInstance().apply {
                setPersistenceEnabled(true)
                setPersistenceCacheSizeBytes(CACHE_SIZE_BYTES.toLong())
            }
            Log.i(TAG, "Firebase offline persistence initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase persistence", e)
        }
    }

    fun keepSynced(ref: DatabaseReference) {
        try {
            ref.keepSynced(true)
            Log.i(TAG, "Enabled keepSynced for reference: ${ref.key}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enable keepSynced", e)
        }
    }

    suspend fun loadOfflineFirst(ref: DatabaseReference): DataSnapshot? {
        return try {
            Log.i(TAG, "Loading offline data first")
            val offlineSnapshot = ref.get().await()
            setupOnlineSync(ref)
            offlineSnapshot
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load offline data", e)
            null
        }
    }

    suspend fun <T> loadData(
        ref: DatabaseReference,
        parser: (DataSnapshot) -> List<T>
    ): List<T> = suspendCancellableCoroutine { continuation ->
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val items = parser(snapshot)
                    Log.i(TAG, "Successfully loaded ${items.size} items")
                    continuation.resume(items)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse data", e)
                    continuation.resumeWithException(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Data load cancelled: ${error.message}")
                continuation.resumeWithException(error.toException())
            }
        })
    }

    fun setupRealtimeSync(
        ref: DatabaseReference,
        onDataChange: (DataSnapshot) -> Unit,
        onError: (Exception) -> Unit
    ): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    onDataChange(snapshot)
                    Log.i(TAG, "Real-time sync updated data")
                } catch (e: Exception) {
                    Log.e(TAG, "Real-time sync data processing failed", e)
                    onError(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Real-time sync cancelled: ${error.message}")
                onError(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        return listener
    }

    private fun setupOnlineSync(ref: DatabaseReference) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i(TAG, "Online sync received update")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Online sync failed: ${error.message}")
            }
        })
    }

    fun removeListener(ref: DatabaseReference, listener: ValueEventListener) {
        ref.removeEventListener(listener)
        Log.i(TAG, "Removed listener from reference: ${ref.key}")
    }

    inline fun <reified T> parseChild(
        path: String,
        snapshot: DataSnapshot,
        crossinline onSuccess: (List<T>) -> Unit
    ) {
        try {
            val type = object : GenericTypeIndicator<List<T>>() {}
            snapshot.child(path).getValue(type)?.let(onSuccess)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse child: $path", e)
        }
    }
}
