package Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.A_LoadFireBase

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

object LoadFromFirebaseProduits {
    private const val TAG = "LoadFromFirebaseProduits"
    private var realtimeListener: ValueEventListener? = null

    suspend fun loadFromFirebase(initViewModel: ViewModelInitApp) {
        try {
            initViewModel.loadingProgress = 0.1f

            // Enable persistence
            FirebaseOfflineHandler.keepSynced(_ModelAppsFather.produitsFireBaseRef)

            // Load offline data first
            val offlineSnapshot =
                FirebaseOfflineHandler.loadOfflineFirst(_ModelAppsFather.produitsFireBaseRef)
            offlineSnapshot?.let { snapshot ->
                val offlineProducts = parseSnapshot(snapshot)
                updateViewModel(initViewModel, offlineProducts)
                initViewModel.loadingProgress = 0.5f
            }

            // Load online data
            val products = FirebaseOfflineHandler.loadData(
                _ModelAppsFather.produitsFireBaseRef,
                LoadFromFirebaseProduits::parseSnapshot
            )
            updateViewModel(initViewModel, products)

            // Setup realtime sync
            setupRealtimeSync(initViewModel)

            initViewModel.loadingProgress = 1.0f
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load data from Firebase", e)
            throw e
        }
    }

    private fun parseSnapshot(snapshot: DataSnapshot): List<ProduitModel> {
        return snapshot.children.mapNotNull { childSnapshot ->
            try {
                parseProduct(childSnapshot)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse product ${childSnapshot.key}", e)
                null
            }
        }.toMutableStateList()
    }

    fun parseProduct(snapshot: DataSnapshot): ProduitModel? {
        val productId = snapshot.key?.toLongOrNull() ?: return null
        val productMap = snapshot.value as? Map<*, *> ?: return null

        return try {
            ProduitModel(
                id = productId,
                itsTempProduit = (productMap["itsTempProduit"] as? Boolean) ?: false,
                init_nom = (productMap["nom"] as? String) ?: "",
                init_besoin_To_Be_Updated = (productMap["besoin_To_Be_Updated"] as? Boolean) ?: false,
                initialNon_Trouve = (productMap["non_Trouve"] as? Boolean) ?: false,
                init_visible = false,
            ).apply {
                // Parse StatuesBase
                snapshot.child("statuesBase").getValue(ProduitModel.StatuesBase::class.java)?.let {
                    statuesBase = it
                    statuesBase.imageGlidReloadTigger = 0
                }

                // Parse BonCommend
                snapshot.child("bonCommendDeCetteCota").let { bonCommendSnapshot ->
                    if (bonCommendSnapshot.exists()) {
                        bonCommendDeCetteCota = bonCommendSnapshot.getValue(ProduitModel.GrossistBonCommandes::class.java)?.apply {
                            grossistInformations = bonCommendSnapshot.child("grossistInformations")
                                .getValue(ProduitModel.GrossistBonCommandes.GrossistInformations::class.java)

                            bonCommendSnapshot.child("mutableBasesStates")
                                .getValue(ProduitModel.GrossistBonCommandes.MutableBasesStates::class.java)?.let {
                                    mutableBasesStates = it
                                }

                            FirebaseOfflineHandler.parseChild<ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee>(
                                "coloursEtGoutsCommendeeList",
                                bonCommendSnapshot
                            ) { coloursEtGoutsCommendeeList = it }
                        }
                    }
                }

                // Parse Lists using FirebaseOfflineHandler
                FirebaseOfflineHandler.parseChild<ProduitModel.ColourEtGout_Model>(
                    "coloursEtGoutsList",
                    snapshot
                ) { coloursEtGoutsList = it }

                FirebaseOfflineHandler.parseChild<ProduitModel.ClientBonVentModel>(
                    "bonsVentDeCetteCotaList",
                    snapshot
                ) { bonsVentDeCetteCotaList = it }

                FirebaseOfflineHandler.parseChild<ProduitModel.ClientBonVentModel>(
                    "historiqueBonsVentsList",
                    snapshot
                ) { historiqueBonsVentsList = it }

                FirebaseOfflineHandler.parseChild<ProduitModel.GrossistBonCommandes>(
                    "historiqueBonsCommendList",
                    snapshot
                ) { historiqueBonsCommendList = it }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse product ID $productId", e)
            null
        }
    }

    private fun setupRealtimeSync(initViewModel: ViewModelInitApp) {
        realtimeListener = FirebaseOfflineHandler.setupRealtimeSync(
            _ModelAppsFather.produitsFireBaseRef,
            onDataChange = { snapshot ->
                val products = parseSnapshot(snapshot)
                updateViewModel(initViewModel, products)
            },
            onError = { error ->
                Log.e(TAG, "Real-time sync failed", error)
            }
        )
    }

    private fun updateViewModel(
        initViewModel: ViewModelInitApp,
        products: List<ProduitModel>
    ) {
        try {
            initViewModel.apply {
                _modelAppsFather.produitsMainDataBase.clear()
                _modelAppsFather.produitsMainDataBase.addAll(products)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update ViewModel", e)
        }
    }
}
