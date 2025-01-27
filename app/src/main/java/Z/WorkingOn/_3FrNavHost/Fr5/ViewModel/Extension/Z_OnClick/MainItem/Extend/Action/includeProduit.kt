package Z.WorkingOn._3FrNavHost.Fr5.ViewModel.Extension.Z_OnClick.MainItem.Extend.Action

import Z.WorkingOn._3FrNavHost.Fr5.ViewModel.Extension.ViewModelExtension_App1_F5
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.update_AllProduits
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel

fun ViewModelExtension_App1_F5.includeProduit(clickeProduct: ProduitModel) {
    excludedProduits.remove(prochenClickIncludeProduit)
    val targetIndex = produitsVerifie.indexOf(clickeProduct)
    if (targetIndex != -1) {
        produitsVerifie.add(targetIndex + 1, prochenClickIncludeProduit!!)
    } else {
        produitsVerifie.add(prochenClickIncludeProduit!!)
    }
    prochenClickIncludeProduit = null

    // Fixed: Use forEachIndexed instead of trying to destructure
    produitsVerifie.forEachIndexed { index, produit ->
        produit.bonCommendDeCetteCota
            ?.mutableBasesStates
            ?.positionProduitDonGrossistChoisiPourAcheterCeProduit =
            index + 1
    }

    update_AllProduits(produitsVerifie,viewModel)
}
