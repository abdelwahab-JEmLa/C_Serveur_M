package com.example.App_Produits_Main._2.ViewModel.Init

import android.util.Log
import com.example.App_Produits_Main._1.Model.AppInitializeModel
import com.example.App_Produits_Main._2.ViewModel.Apps_Produits_Main_DataBase_ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

suspend fun Apps_Produits_Main_DataBase_ViewModel.load_Depuit_FireBase() {
    val TAG = "Produit_Loader"
    val CHEMIN_BASE = "0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase"
    val NOMBRE_PRODUITS = 20
    val baseRef = Firebase.database.getReference(CHEMIN_BASE)

    try {
        // Préparation des produits
        _app_Initialize_Model.produits_Main_DataBase.apply {
            // Ajouter les produits manquants
            val produitsManquants = NOMBRE_PRODUITS - size
            if (produitsManquants > 0) {
                repeat(produitsManquants) { add(AppInitializeModel.ProduitModel()) }
            }
            // Attribuer les IDs
            forEachIndexed { index, produit -> produit.id = index.toLong() }
        }

        // Chargement des données pour chaque produit
        _app_Initialize_Model.produits_Main_DataBase.forEach { produit ->
            try {
                val refProduit = baseRef.child(produit.id.toString())

                // Charger le nom
                val nom = refProduit.child("nom").get().await().value?.toString()
                produit.nom = when {
                    !nom.isNullOrEmpty() -> nom
                    produit.id == 0L -> ""
                    else -> "Empty"
                }

                // Charger les couleurs
                produit.coloursEtGouts.clear()
                refProduit.child("coloursEtGouts").get().await().children.forEach { snapshot ->
                    snapshot.getValue(AppInitializeModel.ProduitModel.ColourEtGout_Model::class.java)
                        ?.let { produit.coloursEtGouts.add(it) }
                }

                // Charger le bon de commande actuel
                val bonCommande = refProduit
                    .child("bonCommendDeCetteCota")
                    .get()
                    .await()
                    .getValue(AppInitializeModel.ProduitModel.GrossistBonCommandes::class.java)

                produit.bonCommendDeCetteCota = bonCommande

                // Charger les bons de vente de cette cota
                produit.bonsVentDeCetteCota.clear()
                refProduit.child("bonsVentDeCetteCota")
                    .get()
                    .await()
                    .children
                    .forEach { snapshot ->
                        snapshot.getValue(AppInitializeModel.ProduitModel.ClientBonVent_Model::class.java)
                            ?.let { produit.bonsVentDeCetteCota.add(it) }
                    }

                // Charger l'historique des bons de vente
                produit.historiqueBonsVents.clear()
                refProduit.child("historiqueBonsVents")
                    .get()
                    .await()
                    .children
                    .forEach { snapshot ->
                        snapshot.getValue(AppInitializeModel.ProduitModel.ClientBonVent_Model::class.java)
                            ?.let { produit.historiqueBonsVents.add(it) }
                    }

                // Charger l'historique des bons de commande
                produit.historiqueBonsCommend.clear()
                refProduit.child("historiqueBonsCommend")
                    .get()
                    .await()
                    .children
                    .forEach { snapshot ->
                        snapshot.getValue(AppInitializeModel.ProduitModel.GrossistBonCommandes::class.java)
                            ?.let { produit.historiqueBonsCommend.add(it) }
                    }

            } catch (e: Exception) {
                Log.e(TAG, "Erreur pour produit ${produit.id}", e)
                // Gestion des erreurs pour ce produit
                produit.apply {
                    nom = "Produit ${produit.id} (Erreur)"
                    coloursEtGouts.clear()
                    besoin_To_Be_Updated = true
                    it_Image_besoin_To_Be_Updated = true
                }
            }
        }

    } catch (e: Exception) {
        Log.e(TAG, "Erreur lors du chargement", e)
        throw e
    }
}
