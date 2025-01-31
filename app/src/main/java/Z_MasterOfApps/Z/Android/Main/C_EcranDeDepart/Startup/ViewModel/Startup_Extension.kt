// ViewModelExtension_App1_F5.kt
package Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.ViewModel

import Z_MasterOfApps.Kotlin.Model.ClientsDataBase.Companion.updateClientsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import com.google.firebase.Firebase
import com.google.firebase.database.database

class Startup_Extension(
    val viewModelInitApp: ViewModelInitApp,
) {
    val clientDataBaseSnapList = viewModelInitApp.clientDataBaseSnapList
    val produitsMainDataBase = viewModelInitApp.produitsMainDataBase

    fun clearAchats() {
        viewModelInitApp._modelAppsFather.produitsMainDataBase.forEach { produit ->
            // Safely add current data to history
            produit.bonCommendDeCetteCota?.let { currentBonCommend ->
                produit.historiqueBonsCommend.add(currentBonCommend)
            }
            if (produit.bonsVentDeCetteCotaList.isNotEmpty()) {
                produit.historiqueBonsVents.addAll(produit.bonsVentDeCetteCotaList)
            }

            // Clear current data
            produit.bonCommendDeCetteCota = null
            produit.bonsVentDeCetteCota.clear()

            // Clear Firebase references
            val database = Firebase.database
            database.getReference("K_GroupeurBonCommendToSupplierRef").removeValue()
            database.getReference("O_SoldArticlesTabelle").removeValue()
            updateProduit(produit, viewModelInitApp)

        }
    }

    // Startup_Extension.kt
    fun implimentClientsParProduits() {
        clientDataBaseSnapList.clear()
        clientDataBaseSnapList.forEachIndexed { _, client ->
            val matchingProducts = produitsMainDataBase.filter { product ->
                product.bonsVentDeCetteCota.any { bonVent ->
                    bonVent.clientInformations?.id == client.id
                }
            }

            // Fix: Update the ProduitsAcheterDeIt.cetteCota list
            client.produitsAcheterDeIt = client.produitsAcheterDeIt.copy(
                cetteCota = matchingProducts
            )

            // Update the client in the database
            client.updateClientsDataBase(viewModelInitApp)
        }
    }

}
