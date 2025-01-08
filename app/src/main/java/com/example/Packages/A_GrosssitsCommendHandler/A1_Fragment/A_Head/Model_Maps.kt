package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class Model_CodingWithMaps {
    var maps by mutableStateOf(MutableStatesVars())

    class MutableStatesVars {
        var grossistList: List<Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations,
                List<AppsHeadModel.ProduitModel>>> by mutableStateOf(emptyList())

        var mapGrossistIdToProduitId: SnapshotStateList<Maper.MapGrossistIdToProduitId> =
            mutableStateListOf()

        var visibleGrossistAssociatedProduits: Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations,
                List<AppsHeadModel.ProduitModel>> by mutableStateOf(
            Pair(
                AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations(),
                emptyList()
            )
        )
    }

    class Maper {
        data class MapGrossistIdToProduitId(
            var grossistId: Long = 0,
            var produits: SnapshotStateList<Produit> = mutableStateListOf()
        ) {
            data class Produit(
                var produitId: Long = 0,
                var commendCouleurs: SnapshotStateList<CommendCouleur> = mutableStateListOf()
            ) {
                data class CommendCouleur(
                    var couleurId: Long = 0,
                    var quantityCommend: Int = 0
                )
            }
        }
    }


    fun updateGrossistMapping(mapping: Maper.MapGrossistIdToProduitId) {
        val grossistIndex = maps.mapGrossistIdToProduitId.indexOfFirst {
            it.grossistId == mapping.grossistId
        }

        val indexToUse = if (grossistIndex != -1) {
            grossistIndex
        } else {
            maps.mapGrossistIdToProduitId.size
        }

        // Créer un Map pour les mises à jour multiples
        val updates = mutableMapOf<String, Any>()

        // Ajouter la mise à jour du grossiste au chemin correct
        updates["$indexToUse"] = mapping

        // Effectuer toutes les mises à jour en une seule opération
        mapsFireBaseRef.updateChildren(updates) 
            .addOnSuccessListener {
                println("Successfully updated grossist mapping at index $indexToUse")
                if (grossistIndex == -1) {
                    maps.mapGrossistIdToProduitId.add(mapping)
                } else {
                    maps.mapGrossistIdToProduitId[grossistIndex] = mapping
                }
            }
            .addOnFailureListener { e ->
                println("Error updating grossist mapping at index $indexToUse: ${e.message}")
                e.printStackTrace()
            }
    }

    // Amélioration de la fonction deleteGrossistMapping pour utiliser updateChildren
    fun deleteGrossistMapping(grossistId: Long) {
        val grossistIndex = maps.mapGrossistIdToProduitId.indexOfFirst {
            it.grossistId == grossistId
        }

        if (grossistIndex != -1) {
            val updates = mutableMapOf<String, Any?>()

            // Marquer le nœud pour suppression
            updates["/${grossistIndex}"] = null

            // Réorganiser les indices restants
            for (i in grossistIndex + 1 until maps.mapGrossistIdToProduitId.size) {
                val nextMapping = maps.mapGrossistIdToProduitId[i]
                updates["/${i - 1}"] = nextMapping
            }

            mapsFireBaseRef.updateChildren(updates)
                .addOnSuccessListener {
                    println("Successfully deleted and reorganized grossist mappings")
                    maps.mapGrossistIdToProduitId.removeAt(grossistIndex)
                }
                .addOnFailureListener { e ->
                    println("Error in delete operation: ${e.message}")
                    e.printStackTrace()
                }
        }
    }

    fun updateCommandQuantity(
        grossistId: Long,
        produitId: Long,
        couleurId: Long,
        newQuantity: Int
    ) {
        try {
            val grossistIndex = maps.mapGrossistIdToProduitId.indexOfFirst {
                it.grossistId == grossistId
            }

            if (grossistIndex != -1) {
                val currentGrossist = maps.mapGrossistIdToProduitId[grossistIndex]

                // Find or create product
                val produitIndex = currentGrossist.produits.indexOfFirst {
                    it.produitId == produitId
                }

                if (produitIndex != -1) {
                    val produit = currentGrossist.produits[produitIndex]
                    val couleurIndex = produit.commendCouleurs.indexOfFirst {
                        it.couleurId == couleurId
                    }

                    if (couleurIndex != -1) {
                        // Update existing color quantity
                        produit.commendCouleurs[couleurIndex] =
                            produit.commendCouleurs[couleurIndex].copy(
                                quantityCommend = newQuantity
                            )
                    } else {
                        // Add new color
                        produit.commendCouleurs.add(
                            Maper.MapGrossistIdToProduitId.Produit.CommendCouleur(
                                couleurId = couleurId,
                                quantityCommend = newQuantity
                            )
                        )
                    }
                } else {
                    // Add new product with color
                    currentGrossist.produits.add(
                        Maper.MapGrossistIdToProduitId.Produit(
                            produitId = produitId,
                            commendCouleurs = mutableStateListOf(
                                Maper.MapGrossistIdToProduitId.Produit.CommendCouleur(
                                    couleurId = couleurId,
                                    quantityCommend = newQuantity
                                )
                            )
                        )
                    )
                }

                updateGrossistMapping(currentGrossist)
            } else {
                // Create new grossist with product and color
                val newGrossist = Maper.MapGrossistIdToProduitId(
                    grossistId = grossistId,
                    produits = mutableStateListOf(
                        Maper.MapGrossistIdToProduitId.Produit(
                            produitId = produitId,
                            commendCouleurs = mutableStateListOf(
                                Maper.MapGrossistIdToProduitId.Produit.CommendCouleur(
                                    couleurId = couleurId,
                                    quantityCommend = newQuantity
                                )
                            )
                        )
                    )
                )
                updateGrossistMapping(newGrossist)
            }
        } catch (e: Exception) {
            println("Error updating command quantity: ${e.message}")
            e.printStackTrace()
        }
    }


    fun deleteProductFromGrossist(grossistId: Long, produitId: Long) {
        val grossistIndex = maps.mapGrossistIdToProduitId.indexOfFirst {
            it.grossistId == grossistId
        }

        if (grossistIndex != -1) {
            val currentGrossist = maps.mapGrossistIdToProduitId[grossistIndex]
            val initialSize = currentGrossist.produits.size
            currentGrossist.produits.removeAll { it.produitId == produitId }

            if (currentGrossist.produits.size != initialSize) {
                updateGrossistMapping(currentGrossist)
            }
        }
    }

    fun deleteCouleurFromProduct(grossistId: Long, produitId: Long, couleurId: Long) {
        val grossistIndex = maps.mapGrossistIdToProduitId.indexOfFirst {
            it.grossistId == grossistId
        }

        if (grossistIndex != -1) {
            val currentGrossist = maps.mapGrossistIdToProduitId[grossistIndex]
            val produitIndex = currentGrossist.produits.indexOfFirst {
                it.produitId == produitId
            }

            if (produitIndex != -1) {
                val currentProduit = currentGrossist.produits[produitIndex]
                val initialSize = currentProduit.commendCouleurs.size
                currentProduit.commendCouleurs.removeAll { it.couleurId == couleurId }

                if (currentProduit.commendCouleurs.size != initialSize) {
                    updateGrossistMapping(currentGrossist)
                }
            }
        }
    }

    companion object {
        val mapsFireBaseRef = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("A_CodingWithListsPatterns")

        suspend fun batchFireBaseUpdateGrossist(grossists: List<Map<String, Any>>) {
            // Mettre à jour Firebase
            val updates = grossists.mapIndexed { index, grossist ->
                "/$index" to grossist
            }.toMap()

            mapsFireBaseRef.updateChildren(updates).await()
        }
    }
}
