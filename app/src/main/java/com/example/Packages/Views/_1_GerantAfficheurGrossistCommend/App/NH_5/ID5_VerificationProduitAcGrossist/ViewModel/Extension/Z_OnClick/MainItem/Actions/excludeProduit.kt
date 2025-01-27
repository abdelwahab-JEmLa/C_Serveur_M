package com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.NH_5.ID5_VerificationProduitAcGrossist.ViewModel.Extension.Z_OnClick.MainItem.Actions

import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.NH_5.ID5_VerificationProduitAcGrossist.ViewModel.Extension.ViewModelExtension_App1_F5
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather

fun ViewModelExtension_App1_F5.excludeProduit(
    product: _ModelAppsFather.ProduitModel,
) {
    produitsVerifie.remove(product)
    excludedProduits.add(product)

}
