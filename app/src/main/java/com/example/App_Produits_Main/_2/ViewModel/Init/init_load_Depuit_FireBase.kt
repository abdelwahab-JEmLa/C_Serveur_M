package com.example.App_Produits_Main._2.ViewModel.Init

import android.util.Log
import com.example.App_Produits_Main._1.Model.App_Initialize_Model
import com.example.App_Produits_Main._2.ViewModel.Apps_Produits_Main_DataBase_ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

private const val TAG = "FirebaseLoader"
private const val DATABASE_PATH = "0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase"
private const val DEFAULT_PRODUCTS_COUNT = 20

class ProductLoader(private val viewModel: Apps_Produits_Main_DataBase_ViewModel) {

    private val database = Firebase.database.getReference(DATABASE_PATH)

    suspend fun loadAllProducts() {
        try {
            initializeProducts()
            loadProductsFromFirebase()
        } catch (e: Exception) {
            Log.e(TAG, "Erreur de chargement des données", e)
            throw e
        }
    }

    private fun initializeProducts() {
        with(viewModel._app_Initialize_Model.produits_Main_DataBase) {
            val missingProducts = DEFAULT_PRODUCTS_COUNT - size
            if (missingProducts > 0) {
                repeat(missingProducts) { add(App_Initialize_Model.Produit_Main_DataBase()) }
            }
            forEachIndexed { index, product -> product.id = index.toLong() }
        }
    }

    private suspend fun loadProductsFromFirebase() {
        viewModel._app_Initialize_Model.produits_Main_DataBase.forEach { product ->
            try {
                loadProductDetails(product)
            } catch (e: Exception) {
                handleLoadError(product)
            }
        }
    }

    private suspend fun loadProductDetails(product: App_Initialize_Model.Produit_Main_DataBase) {
        loadName(product)
        loadColors(product)
        load_Transaction_Vent_Grossist(product)
        load_acheteurs_pour_Cette_Cota(product)
    }

    private suspend fun loadName(product: App_Initialize_Model.Produit_Main_DataBase) {
        val name = database
            .child(product.id.toString())
            .child("nom")
            .get()
            .await()
            .value?.toString()

        product.nom = when {
            !name.isNullOrEmpty() -> name
            product.id == 0L -> ""
            else -> "Empty"
        }
    }

    private suspend fun loadColors(product: App_Initialize_Model.Produit_Main_DataBase) {
        val colors = database
            .child(product.id.toString())
            .child("colours_Et_Gouts")
            .get()
            .await()

        product.colours_Et_Gouts.clear()

        colors.children.forEach { colorSnapshot ->
            colorSnapshot.getValue(App_Initialize_Model.Produit_Main_DataBase.Colours_Et_Gouts::class.java)
                ?.let { product.colours_Et_Gouts.add(it) }
        }
    }
    private suspend fun load_Transaction_Vent_Grossist(product: App_Initialize_Model.Produit_Main_DataBase) {
        val snapshot = database
            .child(product.id.toString())
            .child("grossist_Choisi_Pour_Acheter_CeProduit")
            .child("0")
            .get()
            .await()

        // Get the grossist transaction data
        val grossistTransaction = snapshot.getValue(App_Initialize_Model.Produit_Main_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction::class.java)

        // Update the product's grossist transaction
        product.grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota = when {
            grossistTransaction != null -> grossistTransaction
            product.id == 0L -> null  // For product with id 0, set to null
            else -> null  // For other cases, set to null
        }
    }
    private suspend fun load_acheteurs_pour_Cette_Cota(product: App_Initialize_Model.Produit_Main_DataBase) {
        val snapshot = database
            .child(product.id.toString())
            .child("acheteurs_pour_Cette_Cota")
            .get()
            .await()


        snapshot.children.forEach { snapshot ->
            snapshot.getValue(App_Initialize_Model.Produit_Main_DataBase.Acheteurs_pour_Cette_Cota::class.java)
                ?.let { product.acheteurs_pour_Cette_Cota.add(it) }
        }
    }
    private fun handleLoadError(product: App_Initialize_Model.Produit_Main_DataBase) {
        product.apply {
            nom = "Produit $id (Erreur)"
            colours_Et_Gouts.clear()
            besoin_To_Be_Updated = true
            it_Image_besoin_To_Be_Updated = true
        }
    }
}

// Extension function for the ViewModel
suspend fun Apps_Produits_Main_DataBase_ViewModel.init_load_Depuit_FireBase() {
    ProductLoader(this).loadAllProducts()
}
