package com.example.App_Produits_Main._2.ViewModel.Init

import android.util.Log
import com.example.App_Produits_Main._1.Model.AppInitializeModel
import com.example.App_Produits_Main._2.ViewModel.Apps_Produits_Main_DataBase_ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class Produit_Loader(private val viewModel: Apps_Produits_Main_DataBase_ViewModel) {
    companion object {
        private const val TAG = "Produit_Loader"
        private const val CHEMIN_BASE = "0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase"
        private const val NOMBRE_PRODUITS = 20
    }

    private val baseRef = Firebase.database.getReference(CHEMIN_BASE)

    // Point d'entrée principal
    suspend fun loadAllProducts() {
        try {
            preparerProduits()
            chargerDonnees()
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors du chargement", e)
            throw e
        }
    }

    // Étape 1: Préparation des produits
    private fun preparerProduits() {
        viewModel._app_Initialize_Model.produits_Main_DataBase.apply {
            // Ajouter les produits manquants
            val produitsManquants = NOMBRE_PRODUITS - size
            if (produitsManquants > 0) {
                repeat(produitsManquants) { add(AppInitializeModel.Produit_Model()) }
            }
            // Attribuer les IDs
            forEachIndexed { index, produit -> produit.id = index.toLong() }
        }
    }

    // Étape 2: Chargement des données
    private suspend fun chargerDonnees() {
        viewModel._app_Initialize_Model.produits_Main_DataBase.forEach { produit ->
            try {
                chargerProduit(produit)
            } catch (e: Exception) {
                Log.e(TAG, "Erreur pour produit ${produit.id}", e)
                gererErreur(produit)
            }
        }
    }

    // Chargement d'un produit
    private suspend fun chargerProduit(produit: AppInitializeModel.Produit_Model) {
        val refProduit = baseRef.child(produit.id.toString())

        // Charger le nom
        chargerNom(refProduit, produit)
        // Charger les couleurs
        chargerCouleurs(refProduit, produit)
        // Charger les transactions
        chargerTransactions(refProduit, produit)
        // Charger les acheteurs
        chargerAcheteurs(refProduit, produit)
    }

    // Chargement du nom
    private suspend fun chargerNom(ref: com.google.firebase.database.DatabaseReference, produit: AppInitializeModel.Produit_Model) {
        val nom = ref.child("nom").get().await().value?.toString()
        produit.nom = when {
            !nom.isNullOrEmpty() -> nom
            produit.id == 0L -> ""
            else -> "Empty"
        }
    }

    // Chargement des couleurs
    private suspend fun chargerCouleurs(ref: com.google.firebase.database.DatabaseReference, produit: AppInitializeModel.Produit_Model) {
        produit.colours_Et_Gouts.clear()
        ref.child("colours_Et_Gouts").get().await().children.forEach { snapshot ->
            snapshot.getValue(AppInitializeModel.Produit_Model.Colours_Et_Gouts::class.java)
                ?.let { produit.colours_Et_Gouts.add(it) }
        }
    }

    // Chargement des transactions grossiste
    private suspend fun chargerTransactions(ref: com.google.firebase.database.DatabaseReference, produit: AppInitializeModel.Produit_Model) {
        val grossiste = ref
            .child("grossist_Choisi_Pour_Acheter_CeProduit")
            .child("0")
            .get()
            .await()
            .getValue(AppInitializeModel.Produit_Model.GrossistBonCommandesModel::class.java)

        produit.grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota = when {
            grossiste != null -> grossiste
            else -> null
        }
    }

    // Chargement des acheteurs
    private suspend fun chargerAcheteurs(ref: com.google.firebase.database.DatabaseReference, produit: AppInitializeModel.Produit_Model) {
        ref.child("acheteurs_pour_Cette_Cota")
            .get()
            .await()
            .children
            .forEach { snapshot ->
                snapshot.getValue(AppInitializeModel.Produit_Model.Client_Bon_Vent_Model::class.java)
                    ?.let { produit.acheteurs_pour_Cette_Cota.add(it) }
            }
    }

    // Gestion des erreurs
    private fun gererErreur(produit: AppInitializeModel.Produit_Model) {
        produit.apply {
            nom = "Produit $id (Erreur)"
            colours_Et_Gouts.clear()
            besoin_To_Be_Updated = true
            it_Image_besoin_To_Be_Updated = true
        }
    }
}

// Extension pour faciliter l'utilisation
suspend fun Apps_Produits_Main_DataBase_ViewModel.init_load_Depuit_FireBase() {
    Produit_Loader(this).loadAllProducts()
}
