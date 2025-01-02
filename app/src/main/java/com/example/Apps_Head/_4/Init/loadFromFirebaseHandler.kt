package com.example.Apps_Head._4.Init

import android.util.Log
import androidx.compose.runtime.toMutableStateList
import com.example.Apps_Head._1.Model.AppsHeadModel.ProduitModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object LoadFromFirebaseHandler {
    private const val TAG = "LoadFromFirebaseHandler"
    private const val CHEMIN_BASE = "0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase"
    private const val DEBUG_LIMIT = 7
    private val databaseRef = Firebase.database.getReference(CHEMIN_BASE)

    suspend fun loadFromFirebase(initViewModel: InitViewModel) = try {
        initViewModel.apply {
            _appsHead.produits_Main_DataBase = loadProducts()
            initializationProgress = 1f
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error loading products from Firebase", e)
        throw e
    }

    private suspend fun loadProducts() = suspendCancellableCoroutine { continuation ->
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) = try {
                val products = snapshot.children
                    .mapNotNull { parseProduct(it) }
                    .toMutableStateList()
                continuation.resume(products)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    private fun parseProduct(snapshot: DataSnapshot): ProduitModel? {
        val productId = snapshot.key?.toLongOrNull() ?: return null
        val shouldLog = productId <= DEBUG_LIMIT
        val productMap = snapshot.value as? Map<*, *> ?: return null

        return ProduitModel(
            id = productId,
            init_nom = (productMap["nom"] as? String) ?: "",
            init_besoin_To_Be_Updated = (productMap["besoin_To_Be_Updated"] as? Boolean) ?: false,
            init_it_Image_besoin_To_Be_Updated = (productMap["it_Image_besoin_To_Be_Updated"] as? Boolean) ?: false,
            initialNon_Trouve = (productMap["non_Trouve"] as? Boolean) ?: false,
            init_visible = (productMap["isVisible"] as? Boolean) ?: true
        ).apply {
            if (shouldLog) Log.d(TAG, "Parsing product ID: $productId")
            parseProductDetails(snapshot, shouldLog)
        }
    }

    private fun ProduitModel.parseProductDetails(snapshot: DataSnapshot, shouldLog: Boolean) {
        parseList<ProduitModel.ColourEtGout_Model>("coloursEtGoutsList", snapshot) { coloursEtGoutsList = it }
        parseList<ProduitModel.ClientBonVent_Model>("bonsVentDeCetteCotaList", snapshot) { bonsVentDeCetteCotaList = it }
        parseList<ProduitModel.ClientBonVent_Model>("historiqueBonsVentsList", snapshot) { historiqueBonsVentsList = it }
        parseList<ProduitModel.GrossistBonCommandes>("historiqueBonsCommendList", snapshot) { historiqueBonsCommendList = it }

        snapshot.child("bonCommendDeCetteCota").let { bonCommendSnapshot ->
            if (bonCommendSnapshot.exists()) {
                bonCommendDeCetteCota = bonCommendSnapshot.getValue(ProduitModel.GrossistBonCommandes::class.java)?.apply {
                    grossistInformations = snapshot.child("bonCommendDeCetteCota/grossistInformations")
                        .getValue(ProduitModel.GrossistBonCommandes.GrossistInformations::class.java)

                    parseList<ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee>(
                        "coloursEtGoutsCommendeeList",
                        bonCommendSnapshot
                    ) { coloursEtGoutsCommendeeList = it }
                }
            }
        }
    }

    private inline fun <reified T> parseList(
        path: String,
        snapshot: DataSnapshot,
        crossinline onSuccess: (List<T>) -> Unit
    ) {
        val type = object : GenericTypeIndicator<List<T>>() {}
        snapshot.child(path).getValue(type)?.let(onSuccess)
    }
}
