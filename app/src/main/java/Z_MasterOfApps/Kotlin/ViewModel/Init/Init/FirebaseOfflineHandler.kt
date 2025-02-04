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

suspend fun loadData(app: FirebaseApp, viewModel: ViewModelInitApp) {
    try {
        // 1. Initialize Firebase
        if (!isInitialized) {
            try {
                FirebaseDatabase.getInstance(app).apply {
                    setPersistenceEnabled(true)
                    setPersistenceCacheSizeBytes(100L * 1024L * 1024L)
                }
                isInitialized = true
                Log.i(TAG, "Firebase persistence enabled")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to enable persistence", e)
            }
        }

        viewModel.loadingProgress = 0.1f

        // 2. Enable offline persistence for references
        val refs = listOf(
            _ModelAppsFather.ref_HeadOfModels,
            _ModelAppsFather.produitsFireBaseRef,
            B_ClientsDataBase.refClientsDataBase
        ).onEach { it.keepSynced(true) }

        // 3. Check connection and get data
        val isOnline = withTimeoutOrNull(3000L) {
            refs[1].child("test").setValue(true).await()
            refs[1].child("test").removeValue().await()
            true
        } ?: false

        // 4. Load data based on connection status
        val snapshots = if (isOnline) {
            Log.i(TAG, "🟢 Online mode")
            FirebaseListeners.setupRealtimeListeners(viewModel)
            refs.map { it.get().await() }
        } else {
            Log.w(TAG, "🔴 Offline mode")
            FirebaseDatabase.getInstance().goOffline()
            refs.map { withTimeoutOrNull(5000L) { it.get().await() } }
                .also { FirebaseDatabase.getInstance().goOnline() }
        }

        // 5. Parse data
        val (headModels, products, clients) = snapshots

        // 6. Update ViewModel
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
                        init_besoin_To_Be_Updated = map["besoin_To_Be_Updated"] as? Boolean
                            ?: false,
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

                // Update grossists
                grossistsDataBase.clear()
                headModels?.child("C_GrossistsDataBase")?.children?.forEach { snap ->
                    val map = snap.value as? Map<*, *> ?: return@forEach
                    C_GrossistsDataBase(
                        id = snap.key?.toLongOrNull() ?: return@forEach,
                        nom = map["nom"] as? String ?: ""
                    ).apply {
                        snap.child("statueDeBase")
                            .getValue(C_GrossistsDataBase.StatueDeBase::class.java)?.let {
                            statueDeBase = it
                        }
                        grossistsDataBase.add(this)
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
