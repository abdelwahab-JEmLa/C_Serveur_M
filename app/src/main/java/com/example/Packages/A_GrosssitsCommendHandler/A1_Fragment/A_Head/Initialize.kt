package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head

import androidx.compose.runtime.mutableStateListOf
import com.example.Apps_Head._4.Init.GetAncienDataBasesMain
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model_CodingWithMaps.Companion.batchFireBaseUpdateGrossist
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model_CodingWithMaps.Companion.mapsFireBaseRef
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model_CodingWithMaps.Mapping.Grossist
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

suspend fun start(viewModel: ViewModel_Head) {
    try {
        mapsFireBaseRef.removeValue().await()
        val ancienData = GetAncienDataBasesMain()

        val totalProducts = ancienData.produitsDatabase.size
        val selectedIndices = if (totalProducts <= 50) {
            (0 until totalProducts).toSet()
        } else {
            (0 until totalProducts).shuffled().take(50).toSet()
        }

        // Créer une structure de données compatible Firebase
        val grossists = listOf(1L, 2L).map { grossistId ->
            mapOf(
                "grossistId" to grossistId,
                "produits" to ancienData.produitsDatabase.mapIndexedNotNull { index, produit ->
                    if (index in selectedIndices) {
                        mapOf(
                            "produitId" to produit.idArticle,
                            "commendCouleurs" to List(4) {
                                mapOf(
                                    "couleurId" to (10..50).random().toLong(),
                                    "quantityCommend" to (10..50).random()
                                )
                            }
                        )
                    } else null
                }
            )
        }

        batchFireBaseUpdateGrossist(grossists)

        // Configurer l'écouteur
        mapsFireBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val newMappings = mutableStateListOf<Grossist>()

                    snapshot.children.forEach { grossistSnapshot ->
                        // Conversion manuelle des données Firebase en objets Compose
                        val grossistId = grossistSnapshot.child("grossistId").getValue(Long::class.java) ?: 0L
                        val grossist = Grossist(grossistId = grossistId)

                        grossistSnapshot.child("produits").children.forEach { produitSnapshot ->
                            val produitId = produitSnapshot.child("produitId").getValue(Long::class.java) ?: 0L
                            val produit = Grossist.Produits(
                                produitId = produitId,
                                commendCouleurs = mutableStateListOf()
                            )

                            produitSnapshot.child("commendCouleurs").children.forEach { couleurSnapshot ->
                                val couleurId = couleurSnapshot.child("couleurId").getValue(Long::class.java) ?: 0L
                                val quantity = couleurSnapshot.child("quantityCommend").getValue(Int::class.java) ?: 0

                                produit.commendCouleurs.add(
                                    Grossist.Produits.CommendCouleurs(
                                        couleurId = couleurId,
                                        quantityCommend = quantity
                                    )
                                )
                            }

                            grossist.produits.add(produit)
                        }

                        newMappings.add(grossist)
                    }

                    viewModel._mapsModel.maps.mapGrossistIdToProduitId.clear()
                    viewModel._mapsModel.maps.mapGrossistIdToProduitId.addAll(newMappings)
                } catch (e: Exception) {
                    println("Error parsing Firebase data: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase operation cancelled: ${error.message}")
            }
        })
    } catch (e: Exception) {
        println("Initialization error: ${e.message}")
        throw e
    }
}


