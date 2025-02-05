package Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init

import Z_MasterOfApps.Kotlin.Model.A_ProduitModel
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.update_AllProduits
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.Z.GetAncienDataBasesMain.GetAncienDataBasesMain
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

fun creeDepuitAncienDataBases(
    viewModelInitApp: ViewModelInitApp,
    ) {
    viewModelInitApp.viewModelScope.launch {
        try {
            val ancienData = GetAncienDataBasesMain()

            // Process products and filter out IDs above 2000
            ancienData.produitsDatabase.forEach { ancien ->
                // Skip products with ID > 2000
                if (ancien.idArticle <= 2000) {
                    val depuitAncienDataBase = A_ProduitModel(
                        id = ancien.idArticle,
                        init_nom = ancien.nomArticleFinale,
                        init_visible = false,
                        init_besoin_To_Be_Updated = true
                    ).apply {
                        statuesBase.characterProduit.emballageCartone=
                            ancien.cartonState.contains("rton")
                    }

                    var colorsAdded = 0
                    listOf(
                        ancien.idcolor1 to 1L,
                        ancien.idcolor2 to 2L,
                        ancien.idcolor3 to 3L,
                        ancien.idcolor4 to 4L
                    ).forEach { (colorId, position) ->
                        ancienData.couleurs_List.find { it.idColore == colorId }?.let { couleur ->
                            depuitAncienDataBase.coloursEtGouts.add(
                                A_ProduitModel.ColourEtGout_Model(
                                    position_Du_Couleur_Au_Produit = position,
                                    nom = couleur.nameColore,
                                    imogi = couleur.iconColore,
                                    sonImageNeExistPas = depuitAncienDataBase.itsTempProduit && position == 1L,
                                )
                            )
                            colorsAdded++
                        }
                    }

                    // Add product to main database
                    viewModelInitApp._modelAppsFather.produitsMainDataBase.add(depuitAncienDataBase)
                }
            }

            _ModelAppsFather.produitsFireBaseRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("creeDepuitAncienDataBases", "Successfully cleared Firebase database")
                } else {
                    Log.e("creeDepuitAncienDataBases", "Failed to clear Firebase database", task.exception)
                }
            }

            update_AllProduits(viewModelInitApp._modelAppsFather.produitsMainDataBase,viewModelInitApp)

        } catch (e: Exception) {
            Log.e("creeDepuitAncienDataBases", "Error in creeDepuitAncienDataBases", e)
            throw e
        }
    }
}
