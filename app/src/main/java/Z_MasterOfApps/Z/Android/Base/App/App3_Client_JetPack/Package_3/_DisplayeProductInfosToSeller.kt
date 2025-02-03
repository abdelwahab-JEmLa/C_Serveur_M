package Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_3

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Kotlin.Model.B_ClientsDataBase
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.SoldArticlesTabelle
import Z_MasterOfApps.Z.Android.Packages._3.C_Serveur.App.Client_JetPack.Package_3.deleteProduitCommende

class _DisplayeProductInfosToSeller(val viewModelInitApp: ViewModelInitApp) {
    fun onClickOnMain(
        viewModelInitApp: ViewModelInitApp,
        currentSale: SoldArticlesTabelle,
        currentClient: B_ClientsDataBase?
    ) {
        deleteProduitCommende(viewModelInitApp, currentSale, currentClient)
    }

    fun onClickComposeQuantityButton(
        quantity: Int,
        currentSale: SoldArticlesTabelle?,
        currentClient: B_ClientsDataBase?,
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
