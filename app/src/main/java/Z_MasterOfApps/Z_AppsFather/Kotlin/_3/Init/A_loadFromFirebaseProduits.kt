package Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.runtime.toMutableStateList
import com.example.c_serveur.FirebaseOfflineHandler
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object LoadFromFirebaseProduits {
    private const val TAG = "LoadFromFirebaseProduits"

    suspend fun loadFromFirebase(initViewModel: ViewModelInitApp) {
        try {
            logInfo("Starting Firebase data load")
            initViewModel.loadingProgress = 0.1f

            // Utiliser FirebaseOfflineHandler pour activer la persistence
            FirebaseOfflineHandler.keepSynced(_ModelAppsFather.produitsFireBaseRef)

            // Charger les données hors ligne d'abord
            val offlineSnapshot = FirebaseOfflineHandler.loadOfflineFirst(_ModelAppsFather.produitsFireBaseRef)
            offlineSnapshot?.let { snapshot ->
                val offlineProducts = parseSnapshot(snapshot)
                if (offlineProducts.isNotEmpty()) {
                    updateViewModel(initViewModel, offlineProducts)
                    initViewModel.loadingProgress = 0.5f
                }
            }

            // Ensuite charger/synchroniser avec les données en ligne
            val onlineProducts = loadProducts()
            updateViewModel(initViewModel, onlineProducts)

            setupRealTimeSync(initViewModel)

            initViewModel.loadingProgress = 1.0f
            logInfo("Firebase data load completed successfully")

        } catch (e: Exception) {
            logError("Failed to load data from Firebase", e)
            throw e
        }
    }

    private suspend fun loadProducts(): List<ProduitModel> =
        suspendCancellableCoroutine { continuation ->
            _ModelAppsFather.produitsFireBaseRef
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val products = parseSnapshot(snapshot)
                            logInfo("Loaded ${products.size} products")
                            continuation.resume(products)
                        } catch (e: Exception) {
                            logError("Failed to parse data", e)
                            continuation.resumeWithException(e)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        logError("Data load cancelled", error.toException())
                        continuation.resumeWithException(error.toException())
                    }
                })
        }

    private fun parseSnapshot(snapshot: DataSnapshot): List<ProduitModel> {
        return snapshot.children.mapNotNull { childSnapshot ->
            try {
                parseProduct(childSnapshot)
            } catch (e: Exception) {
                logError("Failed to parse product ${childSnapshot.key}", e)
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

                            parseList<ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee>(
                                "coloursEtGoutsCommendeeList",
                                bonCommendSnapshot
                            ) { coloursEtGoutsCommendeeList = it }
                        }
                    }
                }

                // Parse Lists
                parseList<ProduitModel.ColourEtGout_Model>("coloursEtGoutsList", snapshot) {
                    coloursEtGoutsList = it
                }

                parseList<ProduitModel.ClientBonVentModel>("bonsVentDeCetteCotaList", snapshot) {
                    bonsVentDeCetteCotaList = it
                }

                parseList<ProduitModel.ClientBonVentModel>("historiqueBonsVentsList", snapshot) {
                    historiqueBonsVentsList = it
                }

                parseList<ProduitModel.GrossistBonCommandes>("historiqueBonsCommendList", snapshot) {
                    historiqueBonsCommendList = it
                }
            }
        } catch (e: Exception) {
            logError("Failed to parse product ID $productId", e)
            null
        }
    }

    private inline fun <reified T> parseList(
        path: String,
        snapshot: DataSnapshot,
        crossinline onSuccess: (List<T>) -> Unit
    ) {
        try {
            val type = object : GenericTypeIndicator<List<T>>() {}
            snapshot.child(path).getValue(type)?.let(onSuccess)
        } catch (e: Exception) {
            logError("Failed to parse list: $path", e)
        }
    }

    private fun setupRealTimeSync(initViewModel: ViewModelInitApp) {
        _ModelAppsFather.produitsFireBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val products = parseSnapshot(snapshot)
                    updateViewModel(initViewModel, products)
                } catch (e: Exception) {
                    logError("Real-time sync failed", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                logError("Real-time sync cancelled", error.toException())
            }
        })
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
            logInfo("Updated ViewModel with ${products.size} products")
        } catch (e: Exception) {
            logError("Failed to update ViewModel", e)
        }
    }

    private fun logInfo(message: String) {
        Log.i(TAG, message)
    }

    private fun logError(message: String, error: Throwable) {
        Log.e(TAG, message, error)
    }
}
