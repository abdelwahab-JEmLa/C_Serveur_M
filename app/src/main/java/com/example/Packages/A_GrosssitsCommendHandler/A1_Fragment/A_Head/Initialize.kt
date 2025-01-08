package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head

import androidx.compose.runtime.mutableStateListOf
import com.example.Apps_Head._4.Init.Z.Components.get_Ancien_DataBases_Main
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model_CodingWithMaps.Maper.MapGrossistIdToProduitId
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

suspend fun start(viewModel: ViewModel_Head) {
    try {

        val ancienData = get_Ancien_DataBases_Main()

        // 3. Création et envoi des données par défaut
        val defaultGrossists = listOf(
            Pair(1L, "Grossist Alpha"),
            Pair(2L, "Grossist Beta")
        )

        // 4. Traitement des données pour chaque grossiste
        defaultGrossists.forEach { (grossistId, _) ->
            val mapping = MapGrossistIdToProduitId(
                grossistId = grossistId,
                produits = mutableStateListOf()
            )

            // 5. Ajout des produits pour chaque grossiste
            ancienData.produitsDatabase.forEach { produit ->
                // Création du produit avec couleurs aléatoires
                val produitMapping = MapGrossistIdToProduitId.Produit(
                    produitId = produit.idArticle,
                    commendCouleurs = mutableStateListOf()
                )

                // Ajout de 1 à 4 couleurs aléatoires
                repeat((1..4).random()) {
                    produitMapping.commendCouleurs.add(
                        MapGrossistIdToProduitId.Produit.CommendCouleur(
                            couleurId = (10..50).random().toLong(),
                            quantityCommend = (10..50).random()
                        )
                    )
                }

                mapping.produits.add(produitMapping)
            }

            // 6. Mise à jour dans Firebase
            viewModel._mapsModel.updateGrossistMapping(mapping)
        }

    } catch (e: Exception) {
        println("Erreur d'initialisation: ${e.message}")
        e.printStackTrace()
    }
}

private fun setupFirebaseListener(viewModel: ViewModel_Head) {
    Model_CodingWithMaps.mapsFireBaseRef.addValueEventListener(object : ValueEventListener {
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
                println("Erreur de synchronisation Firebase: ${e.message}")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            println("Opération Firebase annulée: ${error.message}")
        }
    })
}
