package Z_MasterOfApps.Kotlin.ViewModel.Init.Init

import Z_MasterOfApps.Kotlin.ViewModel.FirebaseListeners
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

object FirebaseOfflineHandler {
    private const val TIMEOUT_MS = 5000L
    private var isInitialized = false

    fun initializeFirebase(app: FirebaseApp) {
        if (!isInitialized) {
            try {
                FirebaseDatabase.getInstance(app).apply {
                    setPersistenceEnabled(true)
                    setPersistenceCacheSizeBytes(100L * 1024L * 1024L)
                    Log.d("Firebase", "Persistence configured")
                }
                isInitialized = true
                Log.i("Firebase", "Firebase initialized successfully")
            } catch (e: Exception) {
                Log.e("Firebase", "Initialization failed", e)
                throw e
            }
        }
    }

    suspend fun loadData(
        ref_HeadOfModels: DatabaseReference,
        ref: DatabaseReference,
        refClientsDataBase: DatabaseReference,
        viewModel: ViewModelInitApp? = null
    ): Triple<DataSnapshot?,DataSnapshot?, DataSnapshot?>  {
        if (!isInitialized) {
            Log.e("Firebase", "Firebase not initialized")
            return   Triple(null,null, null)
        }

        return try {
            ref_HeadOfModels.keepSynced(true)
            ref.keepSynced(true)
            refClientsDataBase.keepSynced(true)

            val isOnline = checkConnection(ref)

            if (isOnline) {
                Log.i("Firebase", "🟢 Online mode")
                handleOnlineOperations(ref_HeadOfModels,ref, refClientsDataBase, viewModel)
            } else {
                Log.w("Firebase", "🔴 Offline mode")
                handleOfflineOperations(ref_HeadOfModels,ref, refClientsDataBase)
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Critical load error", e)
            Triple(null,null, null)
        }
    }

    private suspend fun checkConnection(ref: DatabaseReference): Boolean {
        return try {
            withTimeoutOrNull(3000L) {
                val testRef = ref.child("connection_test").apply { keepSynced(false) }
                testRef.setValue(true).await()
                testRef.removeValue().await()
                true
            } ?: false
        } catch (e: Exception) {
            Log.w("Firebase", "Connection check failed: ${e.message}")
            false
        }
    }

    private suspend fun handleOnlineOperations(
        ref_HeadOfModels: DatabaseReference,
        ref: DatabaseReference,
        refClientsDataBase: DatabaseReference,
        viewModel: ViewModelInitApp?
    ): Triple<DataSnapshot?,DataSnapshot?, DataSnapshot?> {
        return try {
            val ref_HeadOfModels_data = ref_HeadOfModels.get().await()
            val data = ref.get().await()
            val data2 = refClientsDataBase.get().await()
            viewModel?.let { FirebaseListeners.setupRealtimeListeners(it) }
            Triple(ref_HeadOfModels_data,data, data2)
        } catch (e: Exception) {
            Log.e("Firebase", "Online operation failed", e)
            Triple(null,null, null)
        }
    }

    private suspend fun handleOfflineOperations(
        ref_HeadOfModels:  DatabaseReference,
        ref: DatabaseReference,
        refClientsDataBase: DatabaseReference
    ): Triple<DataSnapshot?,DataSnapshot?, DataSnapshot?> {
        return try {
            FirebaseDatabase.getInstance().goOffline()
            val data = withTimeoutOrNull(TIMEOUT_MS) { ref.get().await() }
            val data2 = withTimeoutOrNull(TIMEOUT_MS) { refClientsDataBase.get().await() }
            val ref_HeadOfModels_data = withTimeoutOrNull(TIMEOUT_MS) { ref_HeadOfModels.get().await() }
            FirebaseDatabase.getInstance().goOnline()
            Triple(ref_HeadOfModels_data,data, data2)
        } catch (e: Exception) {
            Log.e("Firebase", "Offline operation failed", e)
            Triple(null,null, null)
        }
    }


    // À l'intérieur de l'objet FirebaseOfflineHandler
    inline fun <reified T> parseChild(
        path: String,
        snapshot: DataSnapshot,
        crossinline onSuccess: (List<T>) -> Unit
    ) {
        try {
            val list = snapshot.child(path)
                .getValue(object : GenericTypeIndicator<List<T>>() {})
                ?: emptyList()

            onSuccess(list)
        } catch (e: Exception) {
            Log.e("Firebase", "Parse error for path '$path'", e)
        }
    }
}
