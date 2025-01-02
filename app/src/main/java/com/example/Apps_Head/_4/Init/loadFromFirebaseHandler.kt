package com.example.Apps_Head._4.Init

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.example.Apps_Head._1.Model.AppsHeadModel.ProduitModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

object loadFromFirebaseHandler {
     const val TAG = "LoadFromFirebaseHandler"
    private const val CHEMIN_BASE = "0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase"
    private const val DEBUG_LIMIT = 7
    private val databaseRef = Firebase.database.getReference(CHEMIN_BASE)

    fun loadFromFirebase(onComplete: (SnapshotStateList<ProduitModel>?) -> Unit) {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "Starting data load from Firebase")
                    val newProducts = mutableListOf<ProduitModel>()

                    snapshot.children.forEach { productSnapshot ->
                        parseSnapshot(productSnapshot, DEBUG_LIMIT)?.let {
                            newProducts.add(it)
                        }
                    }

                    Log.d(TAG, "Successfully loaded ${newProducts.size} products")
                    onComplete(newProducts.toMutableStateList())
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading products from Firebase", e)
                    onComplete(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase load cancelled: ${error.message}", error.toException())
                onComplete(null)
            }
        })
    }

    private fun parseSnapshot(snapshot: DataSnapshot, debugLimit: Int): ProduitModel? {
        return try {
            val productId = snapshot.key?.toLongOrNull() ?: -1
            val shouldLog = productId <= debugLimit

            if (shouldLog) {
                Log.d(TAG, "Parsing snapshot for product ID: $productId")
            }

            // Get the data as a Map first
            val productMap = snapshot.value as? Map<*, *>
            if (productMap == null) {
                Log.e(TAG, "Product data is null for ID: $productId")
                return null
            }

            // Create a new ProduitModel with base properties
            ProduitModel(
                id = productId,
                it_ref_Id_don_FireBase = (productMap["it_ref_Id_don_FireBase"] as? Number)?.toLong() ?: 0,
                it_ref_don_FireBase = (productMap["it_ref_don_FireBase"] as? String) ?: "",
                init_nom = (productMap["nom"] as? String) ?: "",
                init_besoin_To_Be_Updated = (productMap["besoin_To_Be_Updated"] as? Boolean) ?: false,
                init_it_Image_besoin_To_Be_Updated = (productMap["it_Image_besoin_To_Be_Updated"] as? Boolean) ?: false,
                initialNon_Trouve = (productMap["non_Trouve"] as? Boolean) ?: false,
                init_visible = (productMap["isVisible"] as? Boolean) ?: true
            ).apply {
                parseCollections(snapshot, shouldLog)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing product snapshot for key: ${snapshot.key}", e)
            null
        }
    }

    private fun ProduitModel.parseCollections(snapshot: DataSnapshot, shouldLog: Boolean) {
        try {
            parseColoursEtGouts(snapshot, shouldLog)
            parseBonCommend(snapshot, shouldLog)
            parseBonsVent(snapshot, shouldLog)
            parseHistoriques(snapshot, shouldLog)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing collections for product ${this.id}", e)
            throw e
        }
    }


    private fun ProduitModel.parseColoursEtGouts(snapshot: DataSnapshot, shouldLog: Boolean) {
        val coloursType = object : GenericTypeIndicator<List<ProduitModel.ColourEtGout_Model>>() {}
        snapshot.child("coloursEtGoutsList").getValue(coloursType)?.let { colours ->
            coloursEtGoutsList = colours
            if (shouldLog) {
                Log.d(TAG, "Loaded ${colours.size} colours for product $id")
            }
        }
    }

    private fun ProduitModel.parseBonCommend(snapshot: DataSnapshot, shouldLog: Boolean) {
        snapshot.child("bonCommendDeCetteCota").let { bonCommendSnapshot ->
            if (bonCommendSnapshot.exists()) {
                bonCommendDeCetteCota = parseBonCommandes(bonCommendSnapshot, shouldLog)
                if (shouldLog) {
                    Log.d(TAG, "Loaded bonCommend for product $id")
                }
            }
        }
    }

    private fun ProduitModel.parseBonsVent(snapshot: DataSnapshot, shouldLog: Boolean) {
        val bonsType = object : GenericTypeIndicator<List<ProduitModel.ClientBonVent_Model>>() {}
        snapshot.child("bonsVentDeCetteCotaList").getValue(bonsType)?.let { bons ->
            bonsVentDeCetteCotaList = bons
            if (shouldLog) {
                Log.d(TAG, "Loaded ${bons.size} bons vents for product $id")
            }
        }
    }

    private fun ProduitModel.parseHistoriques(snapshot: DataSnapshot, shouldLog: Boolean) {
        val ventType = object : GenericTypeIndicator<List<ProduitModel.ClientBonVent_Model>>() {}
        val commendType = object : GenericTypeIndicator<List<ProduitModel.GrossistBonCommandes>>() {}

        snapshot.child("historiqueBonsVentsList").getValue(ventType)?.let { vents ->
            historiqueBonsVentsList = vents
            if (shouldLog) {
                Log.d(TAG, "Loaded ${vents.size} historique vents for product $id")
            }
        }

        snapshot.child("historiqueBonsCommendList").getValue(commendType)?.let { commends ->
            historiqueBonsCommendList = commends
            if (shouldLog) {
                Log.d(TAG, "Loaded ${commends.size} historique commends for product $id")
            }
        }
    }

    private fun parseBonCommandes(
        snapshot: DataSnapshot,
        shouldLog: Boolean
    ): ProduitModel.GrossistBonCommandes? {
        return try {
            snapshot.getValue(ProduitModel.GrossistBonCommandes::class.java)?.apply {
                parseGrossistInformations(snapshot)
                parseColoursCommendee(snapshot, shouldLog)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing GrossistBonCommandes", e)
            null
        }
    }

    private fun ProduitModel.GrossistBonCommandes.parseGrossistInformations(
        snapshot: DataSnapshot
    ) {
        snapshot.child("grossistInformations").let { infoSnapshot ->
            if (infoSnapshot.exists()) {
                grossistInformations = infoSnapshot.getValue(
                    ProduitModel.GrossistBonCommandes.GrossistInformations::class.java
                )
            }
        }
    }

    private fun ProduitModel.GrossistBonCommandes.parseColoursCommendee(
        snapshot: DataSnapshot,
        shouldLog: Boolean
    ) {
        val type = object : GenericTypeIndicator<List<ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee>>() {}
        snapshot.child("coloursEtGoutsCommendeeList").getValue(type)?.let { colours ->
            coloursEtGoutsCommendeeList = colours
            if (shouldLog) {
                Log.d(TAG, "Loaded ${colours.size} colours commandee")
            }
        }
    }
}
