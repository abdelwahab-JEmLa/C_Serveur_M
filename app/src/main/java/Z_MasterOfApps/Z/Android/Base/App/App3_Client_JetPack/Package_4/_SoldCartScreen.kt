package Z_MasterOfApps.Z.Android.Packages._3.C_Serveur.App.Client_JetPack.Package_4

import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Kotlin.Model.ClientsDataBase
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp

class _SoldCartScreen(val viewModelInitApp: ViewModelInitApp) {
    fun onClickOnMain(
        viewModelInitApp: ViewModelInitApp,
        colorIndex: Int,
        article: ArticlesBasesStatsTable,
        clientBuyerNow: ClientsDataBase
    ) {
        deleteColore(viewModelInitApp,colorIndex, article, clientBuyerNow)
    }
}
