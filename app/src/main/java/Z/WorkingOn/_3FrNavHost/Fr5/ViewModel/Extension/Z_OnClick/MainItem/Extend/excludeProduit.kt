package Z.WorkingOn._3FrNavHost.Fr5.ViewModel.Extension.Z_OnClick.MainItem.Extend

import Z.WorkingOn._3FrNavHost.Fr5.ViewModel.Extension.ViewModelExtension_App1_F5
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather

fun ViewModelExtension_App1_F5.excludeProduit(
    product: _ModelAppsFather.ProduitModel,
    verifieProduits: MutableList<_ModelAppsFather.ProduitModel>,
    excludedProduits: MutableList<_ModelAppsFather.ProduitModel>
) {
    verifieProduits.remove(product)
    excludedProduits.add(product)
}
