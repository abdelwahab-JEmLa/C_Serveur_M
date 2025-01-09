// onClick.kt
package com.example.c_serveur.Archives.A3_DiviseProduitsAuCamionFragment.D.Actions

import androidx.compose.runtime.toMutableStateList
import com.example.Y_AppsFather.Kotlin.ModelAppsFather.Companion.update_produitsViewModelEtFireBases
import com.example.Y_AppsFather.Kotlin.ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations.Companion.groupedProductsByClientBonVentModelClientInformations
import com.example.Y_AppsFather.Kotlin.ViewModelProduits

class onClickOn_Fragment_3(private val initViewModel: ViewModelProduits) {
       fun ClientsFloatingActionButton(grpFabClientInfoId: Long) {
              // Get grouped products
              val groupedProducts = groupedProductsByClientBonVentModelClientInformations(
                     initViewModel.appsHeadModel.produitsMainDataBase
              )

              // Find the products associated with the selected client ID
              val clientProducts = groupedProducts.entries
                     .find { (clientInfo, _) -> clientInfo.id == grpFabClientInfoId }
                     ?.value ?: return

              // Update visibility and filter status for each product
              clientProducts.forEach { product ->
                     // Update visibility based on whether any bon vent has the selected client
                     product.isVisible = product.bonsVentDeCetteCota.any { bonVent ->
                            bonVent.clientInformations?.id == grpFabClientInfoId
                     }

                     // Update filter status for the client in all bon vents
                     product.bonsVentDeCetteCota.forEach { bonVent ->
                            bonVent.clientInformations?.let { clientInfo ->
                                   if (clientInfo.id == grpFabClientInfoId) {
                                          clientInfo.auFilterFAB = true
                                   }
                            }
                     }
              }

              // Update both local and Firebase databases
              clientProducts.toMutableStateList().update_produitsViewModelEtFireBases(initViewModel)
       }
}
