package Z_MasterOfApps.Kotlin.ViewModel.Init.Init

import Z_MasterOfApps.Kotlin.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin.Model.C_GrossistsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.FirebaseListeners
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

private const val TAG = "Firebase"
private var isInitialized = false

// Initialize Firebase persistence at app startup
fun initializeFirebase(app: FirebaseApp) {
    if (!isInitialized) {
        try {
            FirebaseDatabase.getInstance(app).apply {
                setPersistenceEnabled(true)
                setPersistenceCacheSizeBytes(100L * 1024L * 1024L)
            }
            isInitialized = true
            Log.i(TAG, "Firebase persistence enabled successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enable persistence", e)
            // Continue without persistence rather than crashing
        }
    }
}

suspend fun loadData(app: FirebaseApp, viewModel: ViewModelInitApp) {
    try {
        viewModel.loadingProgress = 0.1f

        // 1. Enable offline persistence for references
        val refs = listOf(
            _ModelAppsFather.ref_HeadOfModels,
            _ModelAppsFather.produitsFireBaseRef,
            B_ClientsDataBase.refClientsDataBase
        ).onEach { it.keepSynced(true) }

        // 2. Check connection and get data
        val isOnline = withTimeoutOrNull(3000L) {
            refs[1].child("test").setValue(true).await()
            refs[1].child("test").removeValue().await()
            true
        } ?: false

        // 3. Load data based on connection status
        Log.d(TAG, "Starting data load - Online mode: $isOnline")
        val snapshots = if (isOnline) {
            Log.i(TAG, "🟢 Online mode")
            FirebaseListeners.setupRealtimeListeners(viewModel)
            refs.map {
                Log.d(TAG, "Fetching data from reference: ${it.key}")
                it.get().await().also { snapshot ->
                    Log.d(TAG, "Received snapshot for ${it.key}: exists=${snapshot.exists()}, childrenCount=${snapshot.childrenCount}")
                }
            }
        } else {
            Log.w(TAG, "🔴 Offline mode")
            FirebaseDatabase.getInstance().goOffline()
            refs.map { ref ->
                Log.d(TAG, "Attempting offline fetch from reference: ${ref.key}")
                withTimeoutOrNull(5000L) {
                    ref.get().await().also { snapshot ->
                        Log.d(TAG, "Received offline snapshot for ${ref.key}: exists=${snapshot.exists()}, childrenCount=${snapshot.childrenCount}")
                    }
                }
            }.also { FirebaseDatabase.getInstance().goOnline() }
        }

        // 4. Parse data
        val (headModels, products, clients) = snapshots

        // Log detailed information about headModels
        Log.d(TAG, "HeadModels snapshot details:" +
                "\n - Is null: ${headModels == null}" +
                "\n - Exists: ${headModels?.exists()}" +
                "\n - Has children: ${headModels?.hasChildren()}" +
                "\n - Children count: ${headModels?.childrenCount}" +
                "\n - Key: ${headModels?.key}" +
                "\n - Reference: ${headModels?.ref?.key}")

        // 5. Update ViewModel
        withContext(Dispatchers.Main) {
            viewModel.modelAppsFather.apply {
                // Update products
                produitsMainDataBase.clear()
                products?.children?.forEach { snap ->
                    val map = snap.value as? Map<*, *> ?: return@forEach
                    val prod = ProduitModel(
                        id = snap.key?.toLongOrNull() ?: return@forEach,
                        itsTempProduit = map["itsTempProduit"] as? Boolean ?: false,
                        init_nom = map["nom"] as? String ?: "",
                        init_besoin_To_Be_Updated = map["besoin_To_Be_Updated"] as? Boolean ?: false,
                        initialNon_Trouve = map["non_Trouve"] as? Boolean ?: false,
                        init_visible = false
                    ).apply {
                        snap.child("statuesBase").getValue(ProduitModel.StatuesBase::class.java)
                            ?.let {
                                statuesBase = it
                                statuesBase.imageGlidReloadTigger = 0
                            }
                    }
                    produitsMainDataBase.add(prod)
                }

                // Update clients
                clientDataBase.clear()
                clients?.children?.forEach { snap ->
                    val map = snap.value as? Map<*, *> ?: return@forEach
                    B_ClientsDataBase(
                        id = snap.key?.toLongOrNull() ?: return@forEach,
                        nom = map["nom"] as? String ?: ""
                    ).apply {
                        snap.child("statueDeBase")
                            .getValue(B_ClientsDataBase.StatueDeBase::class.java)?.let {
                                statueDeBase = it
                            }
                        snap.child("gpsLocation")
                            .getValue(B_ClientsDataBase.GpsLocation::class.java)?.let {
                                gpsLocation = it
                            }
                        clientDataBase.add(this)
                    }
                }

                // Update grossists with better error handling
                grossistsDataBase.clear()
                if (headModels == null) {
                    Log.e(TAG, "headModels is null - unable to update grossists")
                } else {
                    val grossistsNode = headModels.child("C_GrossistsDataBase")
                    Log.d(TAG, "Grossists node details:" +
                            "\n - Exists: ${grossistsNode.exists()}" +
                            "\n - Children count: ${grossistsNode.childrenCount}" +
                            "\n - Path: ${grossistsNode.ref.path}")

                    if (!grossistsNode.exists()) {
                        // Create default grossist if none exist
                        Log.w(TAG, "No grossists found - creating default entry")
                        grossistsDataBase.add(C_GrossistsDataBase(
                            id = 1,
                            nom = "Default Grossist",
                            statueDeBase = C_GrossistsDataBase.StatueDeBase(
                                cUnClientTemporaire = true
                            )
                        ))
                    } else {
                        grossistsNode.children.forEach { snap ->
                            try {
                                val map = snap.value as? Map<*, *> ?: run {
                                    Log.e(TAG, "Failed to cast grossist value to Map for key: ${snap.key}")
                                    return@forEach
                                }
                                C_GrossistsDataBase(
                                    id = snap.key?.toLongOrNull() ?: run {
                                        Log.e(TAG, "Invalid grossist ID format: ${snap.key}")
                                        return@forEach
                                    },
                                    nom = map["nom"] as? String ?: "Non Defini"
                                ).apply {
                                    snap.child("statueDeBase")
                                        .getValue(C_GrossistsDataBase.StatueDeBase::class.java)?.let {
                                            statueDeBase = it
                                        }
                                    grossistsDataBase.add(this)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing grossist ${snap.key}", e)
                            }
                        }
                    }
                }
            }
            viewModel.loadingProgress = 1.0f
            Log.d(TAG, "✅ Data loaded successfully")
        }
    } catch (e: Exception) {
        Log.e(TAG, "💥 Loading failed", e)
        viewModel.loadingProgress = -1f
        throw e
    }
}
