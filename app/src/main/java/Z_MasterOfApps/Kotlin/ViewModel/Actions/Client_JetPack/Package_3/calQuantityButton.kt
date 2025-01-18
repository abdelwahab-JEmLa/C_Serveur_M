package Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Package_3

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.GrossistBonCommandes
import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.ClientsModel
import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.SoldArticlesTabelle
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.LogUtils
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun calQuantityButton(
    quantity: Int,
    currentSale: SoldArticlesTabelle?,
    currentClient: ClientsModel?,
    colorDetails: ColorsArticlesTabelle,
    viewModelInitApp: ViewModelInitApp
) {
    try {
        if (currentSale == null || currentClient == null) {
            LogUtils.logError(LogUtils.Tags.QUANTITY_BUTTON, "Missing required data")
            return
        }

        val productIndex = viewModelInitApp._modelAppsFather.produitsMainDataBase
            .indexOfFirst { it.id == currentSale.idArticle }
        val product =
            viewModelInitApp._modelAppsFather.produitsMainDataBase.getOrNull(productIndex)
                ?: return

        val colorPurchase = ClientBonVentModel.ColorAchatModel(
            vidPosition = System.currentTimeMillis(),
            couleurId = colorDetails.idColore,
            nom = colorDetails.nameColore,
            quantity_Achete = quantity,
            imogi = colorDetails.iconColore
        )

        val existingSaleIndex = product.bonsVentDeCetteCota
            .indexOfFirst { it.clientInformations?.id == currentClient.idClientsSu }

        if (existingSaleIndex != -1) {
            val existingSale = product.bonsVentDeCetteCota[existingSaleIndex]
            val colorIndex = existingSale.colours_Achete
                .indexOfFirst { it.couleurId == colorDetails.idColore }

            if (colorIndex != -1) {
                existingSale.colours_Achete[colorIndex] = colorPurchase
            } else {
                existingSale.colours_Achete.add(colorPurchase)
            }
        } else {
            val newSale = ClientBonVentModel(
                vid = System.currentTimeMillis()
            ).apply {
                clientInformations = ClientBonVentModel.ClientInformations(
                    id = currentClient.idClientsSu,
                    nom = currentClient.nomClientsSu,
                    couleur = currentClient.couleurSu
                )
                colours_Achete.add(colorPurchase)
            }
            product.bonsVentDeCetteCota.add(newSale)
        }

        // Format current date as yyyy-MM-dd
        val currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        if (product.bonCommendDeCetteCota == null ||
            !product.historiqueBonsCommend.any { it.mutableBasesStates?.dateInString == currentDate }
        ) {
            // Get grossist information with fallback to default
            val lastGrossistInfo = product.historiqueBonsCommend.lastOrNull()?.grossistInformations
                ?: GrossistBonCommandes.GrossistInformations() // Uses default values defined in the class

            val aggregatedColors = product.bonsVentDeCetteCota
                .flatMap { it.colours_Achete }
                .groupBy { it.couleurId }
                .mapNotNull { (couleurId, colorList) ->
                    colorList.firstOrNull()?.let { firstColor ->
                        val totalQuantity = colorList.sumOf { it.quantity_Achete }
                        if (totalQuantity > 0) {
                            GrossistBonCommandes.ColoursGoutsCommendee(
                                id = couleurId,
                                nom = firstColor.nom,
                                emogi = firstColor.imogi
                            ).apply {
                                quantityAchete = totalQuantity
                            }
                        } else null
                    }
                }

            val newBonCommande = GrossistBonCommandes(
                vid = System.currentTimeMillis(),
                init_grossistInformations = lastGrossistInfo,
                init_coloursEtGoutsCommendee = aggregatedColors
            ).apply {
                mutableBasesStates?.dateInString = currentDate
            }

            // Update current bon commande
            product.bonCommendDeCetteCota = newBonCommande

            // Add to history if date doesn't exist
            if (!product.historiqueBonsCommend.any { it.mutableBasesStates
                    ?.dateInString == currentDate }) {
                product.historiqueBonsCommend.add(newBonCommande)
            }
        }

        viewModelInitApp.viewModelScope.launch {
            viewModelInitApp._modelAppsFather.produitsMainDataBase[productIndex] = product
            _ModelAppsFather.updateProduit(product, viewModelInitApp)
        }

    } catch (e: Exception) {
        LogUtils.logError(LogUtils.Tags.QUANTITY_BUTTON, "Error updating sale", e)
    }
}
