package com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.NH_3.id2_TravaillieurListProduitAchercheChezLeGrossist.ViewModel.Extension.Z_OnClick.MainItem.Extend

import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.NH_3.id2_TravaillieurListProduitAchercheChezLeGrossist.ViewModel.Extension.ViewModelExtension_App1_F2
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel

fun ViewModelExtension_App1_F2.changeColours_AcheteQuantity_Achete(
    selectedBonVent: _ModelAppsFather.ProduitModel.ClientBonVentModel?,
    produit: ProduitModel,
    color: ProduitModel.ClientBonVentModel.ColorAchatModel,
    newQuantity: Int
    ) {
        val updatedProduit = produit.apply {
            bonsVentDeCetteCota.find { it==selectedBonVent }
                ?.let { bonVent ->
                bonVent.colours_Achete.find { it == color }
                    ?.quantity_Achete = newQuantity
            }
        }
        updateProduit(updatedProduit,viewModel)
    }
