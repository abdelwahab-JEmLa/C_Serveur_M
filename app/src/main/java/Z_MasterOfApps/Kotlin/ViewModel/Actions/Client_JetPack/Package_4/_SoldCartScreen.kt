package Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Package_4

import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.ClientsModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp

class _SoldCartScreen(val viewModelInitApp: ViewModelInitApp) {
    fun onClickOnMain(
        viewModelInitApp: ViewModelInitApp,
        colorIndex: Int,
        article: ArticlesBasesStatsTable,
        clientBuyerNow: ClientsModel
    ) {
        deleteColore(viewModelInitApp,colorIndex, article, clientBuyerNow)
    }
}
