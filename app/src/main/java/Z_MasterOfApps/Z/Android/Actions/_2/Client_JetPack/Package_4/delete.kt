package Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Package_4

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.ClientsModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp

fun deleteColore(
    viewModelInitApp: ViewModelInitApp,
    colorIndex: Int,
    article: ArticlesBasesStatsTable,
    clientBuyerNow: ClientsModel
) {
    val colorId = when (colorIndex) {
        0 -> article.idcolor1
        1 -> article.idcolor2
        2 -> article.idcolor3
        3 -> article.idcolor4
        else -> 0L
    }
    viewModelInitApp._modelAppsFather.produitsMainDataBase
        .find { it.id.toInt() == article.idArticle }?.let { product ->
            // Find and remove the specific sale for the current client
            product.bonsVentDeCetteCota
                .filter { it.clientInformations?.id == clientBuyerNow.idClientsSu }
                .forEach { bonVente ->
                    bonVente.colours_Achete.removeIf { it.couleurId == colorId }
                }

            // Update the product in Firebase
            updateProduit(product, viewModelInitApp)
        }
}
