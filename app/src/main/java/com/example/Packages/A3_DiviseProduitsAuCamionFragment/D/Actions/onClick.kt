// onClick.kt
package com.example.Packages.A3_DiviseProduitsAuCamionFragment.D.Actions

import androidx.compose.runtime.toMutableStateList
import com.example._AppsHeadModel._1.Model.AppsHeadModel.Companion.update_produitsViewModelEtFireBases
import com.example._AppsHeadModel._1.Model.AppsHeadModel.ProduitModel.ClientBonVentModel.ClientInformations.Companion.groupedProductsByClientBonVentModelClientInformations
import com.example._AppsHeadModel._2.ViewModel.InitViewModel

class onClickOn_Fragment_3(private val initViewModel: InitViewModel) {
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
