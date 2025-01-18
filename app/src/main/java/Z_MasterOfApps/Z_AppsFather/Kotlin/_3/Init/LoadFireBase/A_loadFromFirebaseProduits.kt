package Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.LoadFireBase

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object LoadFromFirebaseProduits {
    private const val TAG = "LoadFromFirebaseProduits"
    private const val CACHE_SIZE = 100L * 1024 * 1024 // 100MB

    suspend fun loadFromFirebase(initViewModel: ViewModelInitApp) {
        try {
            // Initialize Firebase with offline persistence
            FirebaseDatabase.getInstance().apply {
                setPersistenceEnabled(true)
                setPersistenceCacheSizeBytes(CACHE_SIZE)
            }
            _ModelAppsFather.produitsFireBaseRef.keepSynced(true)

            // Load data with progress updates
            initViewModel.loadingProgress = 0.1f
            val products = loadProductsFromFirebase()
            initViewModel.loadingProgress = 0.5f

            // Update ViewModel
            updateViewModel(initViewModel, products)

            // Setup realtime sync
            setupRealtimeSync(initViewModel)
            initViewModel.loadingProgress = 1.0f

        } catch (e: Exception) {
            Log.e(TAG, "Failed to load data", e)
            throw e
        }
    }

    private suspend fun loadProductsFromFirebase(): List<ProduitModel> =
        suspendCancellableCoroutine { continuation ->
            _ModelAppsFather.produitsFireBaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products = parseProductsSnapshot(snapshot)
                    continuation.resume(products)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }

    private fun parseProductsSnapshot(snapshot: DataSnapshot): List<ProduitModel> =
        snapshot.children.mapNotNull { childSnapshot ->
            try {
                parseProduct(childSnapshot)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse product ${childSnapshot.key}", e)
                null
            }
        }.toMutableStateList()

    fun parseProduct(snapshot: DataSnapshot): ProduitModel? {
        val productId = snapshot.key?.toLongOrNull() ?: return null
        val productMap = snapshot.value as? Map<*, *> ?: return null

        return ProduitModel(
            id = productId,
            itsTempProduit = (productMap["itsTempProduit"] as? Boolean) ?: false,
            init_nom = (productMap["nom"] as? String) ?: "",
            init_besoin_To_Be_Updated = (productMap["besoin_To_Be_Updated"] as? Boolean) ?: false,
            initialNon_Trouve = (productMap["non_Trouve"] as? Boolean) ?: false,
            init_visible = false,
        ).apply {
            // Base status
            statuesBase = snapshot.child("statuesBase")
                .getValue(ProduitModel.StatuesBase::class.java) ?: ProduitModel.StatuesBase()
            statuesBase.imageGlidReloadTigger = 0

            // Lists
            parseListData(snapshot)

            // BonCommend
            parseBonCommend(snapshot)
        }
    }

    private fun ProduitModel.parseListData(snapshot: DataSnapshot) {
        coloursEtGoutsList = snapshot.child("coloursEtGoutsList")
            .getValue(object : GenericTypeIndicator<List<ProduitModel.ColourEtGout_Model>>() {})
            ?: emptyList()

        bonsVentDeCetteCotaList = snapshot.child("bonsVentDeCetteCotaList")
            .getValue(object : GenericTypeIndicator<List<ProduitModel.ClientBonVentModel>>() {})
            ?: emptyList()

        historiqueBonsVentsList = snapshot.child("historiqueBonsVentsList")
            .getValue(object : GenericTypeIndicator<List<ProduitModel.ClientBonVentModel>>() {})
            ?: emptyList()

        historiqueBonsCommendList = snapshot.child("historiqueBonsCommendList")
            .getValue(object : GenericTypeIndicator<List<ProduitModel.GrossistBonCommandes>>() {})
            ?: emptyList()
    }

    private fun ProduitModel.parseBonCommend(snapshot: DataSnapshot) {
        snapshot.child("bonCommendDeCetteCota").let { bonCommendSnapshot ->
            if (bonCommendSnapshot.exists()) {
                bonCommendDeCetteCota = bonCommendSnapshot
                    .getValue(ProduitModel.GrossistBonCommandes::class.java)?.apply {
                        grossistInformations = bonCommendSnapshot.child("grossistInformations")
                            .getValue(ProduitModel.GrossistBonCommandes.GrossistInformations::class.java)

                        mutableBasesStates = bonCommendSnapshot.child("mutableBasesStates")
                            .getValue(ProduitModel.GrossistBonCommandes.MutableBasesStates::class.java)

                        coloursEtGoutsCommendeeList = bonCommendSnapshot.child("coloursEtGoutsCommendeeList")
                            .getValue(object : GenericTypeIndicator<List<ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee>>() {})
                            ?: emptyList()
                    }
            }
        }
    }

    private fun setupRealtimeSync(initViewModel: ViewModelInitApp) {
        _ModelAppsFather.produitsFireBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = parseProductsSnapshot(snapshot)
                updateViewModel(initViewModel, products)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Real-time sync failed: ${error.message}")
            }
        })
    }

    private fun updateViewModel(initViewModel: ViewModelInitApp, products: List<ProduitModel>) {
        try {
            initViewModel._modelAppsFather.produitsMainDataBase.clear()
            initViewModel._modelAppsFather.produitsMainDataBase.addAll(products)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update ViewModel", e)
        }
    }
}
