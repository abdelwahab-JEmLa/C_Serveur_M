package Z.WorkingOn._3FrNavHost.Fr5.ViewModel.Extension.Z_OnClick.MainItem.Actions

import Z.WorkingOn._3FrNavHost.Fr5.ViewModel.Extension.ViewModelExtension_App1_F5
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather

fun ViewModelExtension_App1_F5.excludeProduit(
    product: _ModelAppsFather.ProduitModel,
) {
    produitsVerifie.remove(product)
    excludedProduits.add(product)

}
