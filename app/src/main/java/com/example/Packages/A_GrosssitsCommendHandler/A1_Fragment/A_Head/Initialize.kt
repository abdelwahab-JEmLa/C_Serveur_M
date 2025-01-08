package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head

import androidx.compose.runtime.mutableStateListOf
import com.example.Apps_Head._4.Init.Z.Components.get_Ancien_DataBases_Main
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model_CodingWithMaps.Companion.mapsFireBaseRef
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model_CodingWithMaps.Maper.MapGrossistIdToProduitId
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

suspend fun start(viewModel: ViewModel_Head) {
    try {
        // Clear existing database and get old data
        mapsFireBaseRef.removeValue().await()
        val ancienData = get_Ancien_DataBases_Main()

        // Calculate selected product indices
        val totalProducts = ancienData.produitsDatabase.size
        val selectedIndices = if (totalProducts <= 50) {
            (0 until totalProducts).toSet()
        } else {
            (0 until totalProducts).shuffled().take(50).toSet()
        }

        // Initialize grossists (Alpha and Beta)
        val grossists = listOf(1L, 2L).map { grossistId ->
            MapGrossistIdToProduitId(grossistId = grossistId).apply {
                // Add only selected products with random colors for each grossist
                produits.addAll(ancienData.produitsDatabase.mapIndexedNotNull { index, produit ->
                    if (index in selectedIndices) {
                        MapGrossistIdToProduitId.Produit(
                            produitId = produit.idArticle,
                            commendCouleurs = mutableStateListOf<MapGrossistIdToProduitId.Produit.CommendCouleur>().apply {
                                // Add 4 random colors with random quantities
                                repeat(4) {
                                    add(MapGrossistIdToProduitId.Produit.CommendCouleur(
                                        couleurId = (10..50).random().toLong(),
                                        quantityCommend = (10..50).random()
                                    ))
                                }
                            }
                        )
                    } else null
                })
            }
        }

        // Update Firebase with all grossists at once
        val updates = grossists.mapIndexed { index, grossist ->
            "/$index" to grossist
        }.toMap()

        mapsFireBaseRef.updateChildren(updates).await()

        // Set up real-time listener
        mapsFireBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newMappings = mutableStateListOf<MapGrossistIdToProduitId>()
                snapshot.children.forEach { grossistSnapshot ->
                    grossistSnapshot.getValue(MapGrossistIdToProduitId::class.java)?.let {
                        newMappings.add(it)
                    }
                }
                viewModel._mapsModel.maps.mapGrossistIdToProduitId.clear()
                viewModel._mapsModel.maps.mapGrossistIdToProduitId.addAll(newMappings)
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
