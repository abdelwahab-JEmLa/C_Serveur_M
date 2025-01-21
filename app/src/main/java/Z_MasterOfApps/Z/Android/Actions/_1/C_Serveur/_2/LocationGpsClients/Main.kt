package Z_MasterOfApps.Z.Android.Actions._1.C_Serveur._2.LocationGpsClients

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.ClientsModel
import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.SoldArticlesTabelle
import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Package_3.calQuantityButton

class OnClickOneShotAction(val viewModelInitApp: ViewModelInitApp) {
    fun onClickComposeQuantityButton(
        quantity: Int,
        currentSale: SoldArticlesTabelle?,
        currentClient: ClientsModel?,
        colorDetails: ColorsArticlesTabelle
    ) {
        calQuantityButton(
            quantity,
            currentSale,
            currentClient,
            colorDetails,
            viewModelInitApp
        )
    }
}
