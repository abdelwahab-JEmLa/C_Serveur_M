package com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components

import android.util.Log
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model._1.MAIN.UiState


const val TAG_Snap = "InitialeUiState"

// _1.Initialize.kt update
internal suspend fun P3_ViewModel._1Initialize(
) {
    try {
        Log.d(TAG_Snap, "Starting _1Initialize")
        initializationProgress = (1/10).toFloat()

        initializationProgress = (3/10).toFloat()
        // First Init
        if (true) { // Replace with actual condition
            List(1500) { i ->
                val produit = UiState.Produit_DataBase(
                    id = i.toLong(),
                    it_ref_Id_don_FireBase = 1L,
                    it_ref_don_FireBase = "produit_DataBase",
                    init_besoin_To_Be_Updated = true
                )
                _uiState.produit_DataBase.add(produit)
            }
        }

        // Get ancient data
        val ancienData = get_Ancien_Datas()

        // Update products
        _uiState.produit_DataBase.forEach { produit ->
            produit.besoin_To_Be_Updated = false
            // Find matching ancient product
            ancienData.produitsDatabase.find { it.idArticle == produit.id }?.let { ancien_DataBase ->
                produit.nom = ancien_DataBase.nomArticleFinale

                ancienData.couleurs_List.find { it.idColore== ancien_DataBase.idcolor1}?.let {
                    UiState.Produit_DataBase.Colours_Et_Gouts(
                        position_Du_Couleur_Au_Produit=1L,
                        nom=  it.nameColore,
                        imogi = it.iconColore
                    )
                }?.let {
                    produit.colours_Et_Gouts.add(
                        it
                    )
                }

                ancienData.couleurs_List.find { it.idColore== ancien_DataBase.idcolor2}?.let {
                    UiState.Produit_DataBase.Colours_Et_Gouts(
                        position_Du_Couleur_Au_Produit=2L,
                        nom=  it.nameColore,
                        imogi = it.iconColore
                    )
                }?.let {
                    produit.colours_Et_Gouts.add(
                        it
                    )
                }

                ancienData.couleurs_List.find { it.idColore== ancien_DataBase.idcolor3}?.let {
                    UiState.Produit_DataBase.Colours_Et_Gouts(
                        position_Du_Couleur_Au_Produit=3L,
                        nom=  it.nameColore,
                        imogi = it.iconColore
                    )
                }?.let {
                    produit.colours_Et_Gouts.add(
                        it
                    )
                }

                ancienData.couleurs_List.find { it.idColore== ancien_DataBase.idcolor4}?.let {
                    UiState.Produit_DataBase.Colours_Et_Gouts(
                        position_Du_Couleur_Au_Produit=4L,
                        nom=  it.nameColore,
                        imogi = it.iconColore
                    )
                }?.let {
                    produit.colours_Et_Gouts.add(
                        it
                    )
                }


            }
        }


        initializationProgress = (1/10).toFloat()

        Log.d(TAG_Snap, "Completed _1Initialize")
    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in _1Initialize", e)
        throw e
    }
}
