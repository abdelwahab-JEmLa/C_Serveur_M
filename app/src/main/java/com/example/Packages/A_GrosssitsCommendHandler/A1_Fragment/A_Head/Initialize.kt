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
        // Get old data and clear existing database
        val ancienData = get_Ancien_DataBases_Main()
        mapsFireBaseRef.removeValue().await()

        // Create default grossists with their mappings
        val defaultGrossists = listOf(
            MapGrossistIdToProduitId(grossistId = 1L), // Grossist Alpha
            MapGrossistIdToProduitId(grossistId = 2L)  // Grossist Beta
        )

        // Initialize each grossist with products and random colors
        defaultGrossists.forEach { grossist ->
            // Add all products, but with random colors for each
            ancienData.produitsDatabase.forEach { produit ->
                val produitMapping = createProductWithRandomColors(produit.idArticle)
                grossist.produits.add(produitMapping)
            }

            // Create updates map for Firebase
            val updates = mutableMapOf<String, Any>()
            updates["/${defaultGrossists.indexOf(grossist)}"] = grossist

            // Update Firebase with the current grossist
            mapsFireBaseRef.updateChildren(updates).await()
        }

        // Set up Firebase listener for real-time updates
        setupFirebaseListener(viewModel)

    } catch (e: Exception) {
        println("Initialization error: ${e.message}")
        throw e
    }
}

private fun createProductWithRandomColors(produitId: Long): MapGrossistIdToProduitId.Produit {
    return MapGrossistIdToProduitId.Produit(
        produitId = produitId,
        commendCouleurs = mutableStateListOf<MapGrossistIdToProduitId.Produit.CommendCouleur>().apply {
            // For each possible color (1-4), decide whether to add it
            repeat(4) { colorIndex ->
                if (shouldAddColor()) {  // Using probability for colors instead of products
                    add(MapGrossistIdToProduitId.Produit.CommendCouleur(
                        couleurId = (10..50).random().toLong(),
                        quantityCommend = (10..50).random()
                    ))
                }
            }
        }
    )
}

// Changed function name to reflect its new purpose
private fun shouldAddColor(): Boolean = Math.random() < 0.5 // 50% chance to add each color

private fun setupFirebaseListener(viewModel: ViewModel_Head) {
    mapsFireBaseRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                val newMappings = mutableStateListOf<MapGrossistIdToProduitId>()
                snapshot.children.forEach { grossistSnapshot ->
                    grossistSnapshot.getValue(MapGrossistIdToProduitId::class.java)?.let {
                        newMappings.add(it)
                    }
                }

                viewModel._mapsModel.maps.mapGrossistIdToProduitId.clear()
                viewModel._mapsModel.maps.mapGrossistIdToProduitId.addAll(newMappings)
            } catch (e: Exception) {
                println("Firebase sync error: ${e.message}")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            println("Firebase operation cancelled: ${error.message}")
        }
    })
}
